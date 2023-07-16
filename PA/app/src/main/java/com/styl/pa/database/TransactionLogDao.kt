package com.styl.pa.database

import androidx.room.*
import com.styl.pa.entities.payment.PaymentRequest

/**
 * Created by trangpham on 10/13/2018
 */
@Dao
interface TransactionLogDao {

    @Query("SELECT * FROM TransactionLog WHERE txnNo LIKE :id")
    fun getLogBy(id: String): PaymentRequest

//    @Query("SELECT * FROM TransactionLog WHERE paymentStatus = ${PaymentRequest.STATUS_GENERATED} OR paymentStatus = ${PaymentRequest.STATUS_PAID} OR paymentStatus = ${PaymentRequest.STATUS_UPDATED} OR paymentStatus = ${PaymentRequest.STATUS_UNSUCCESSFUL}")
//    @Query("SELECT * FROM TransactionLog WHERE paymentStatus = ${PaymentRequest.STATUS_PAID} OR paymentStatus = ${PaymentRequest.STATUS_UNSUCCESSFUL}")
    @Query("SELECT * FROM TransactionLog WHERE paymentStatus != ${PaymentRequest.STATUS_DONE} AND paymentStatus != ${PaymentRequest.STATUS_CREATED}")
    fun getPendingLogs(): List<PaymentRequest>

    @Query("SELECT * FROM TransactionLog WHERE paymentStatus != ${PaymentRequest.STATUS_DONE} limit :logPerRequest offset :currentOffset")
    fun getPendingLogs(currentOffset: Int, logPerRequest: Int): List<PaymentRequest>

    @Query("SELECT * FROM TransactionLog WHERE :time - createdAt > :threshold AND paymentStatus = ${PaymentRequest.STATUS_DONE}")
    fun getSubmittedLogs(time: Long, threshold: Int): List<PaymentRequest>

    @Query("SELECT * FROM TransactionLog WHERE :time - createdAt > :threshold AND (paymentStatus = ${PaymentRequest.STATUS_CREATED} OR paymentStatus = ${PaymentRequest.STATUS_UNSUCCESSFUL})")
    fun getOutdatedLogs(time: Long, threshold: Int): List<PaymentRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(item: PaymentRequest)

    @Update
    fun updateLog(item: PaymentRequest)

    @Query("UPDATE TransactionLog SET paReceiptId = :receiptNo, paymentStatus = ${PaymentRequest.STATUS_GENERATED} WHERE txnNo LIKE :txnNo")
    fun updateReceiptId(txnNo: String, receiptNo: String)

    @Query("UPDATE TransactionLog SET paymentStatus = :status WHERE txnNo LIKE :txnNo")
    fun updateStatus(status: Int, txnNo: String)

    @Query("UPDATE TransactionLog SET signatureImage = :signatureImage WHERE txnNo LIKE :txnNo")
    fun updateSignature(txnNo: String, signatureImage: String)

    @Query("UPDATE TransactionLog SET pdfFile = :pdfFile, signatureImage = :signatureImage WHERE txnNo LIKE :txnNo")
    fun updateSignatureAndPdfFile(txnNo: String, pdfFile: String?, signatureImage: String)

    @Query("DELETE FROM TransactionLog WHERE :time - createdAt > :threshold AND paymentStatus = ${PaymentRequest.STATUS_DONE}")
    fun deleteSubmittedLogs(time: Long, threshold: Int)

    @Query("DELETE FROM TransactionLog WHERE :time - createdAt > :threshold AND (paymentStatus = ${PaymentRequest.STATUS_CREATED} OR paymentStatus = ${PaymentRequest.STATUS_UNSUCCESSFUL})")
    fun deleteOutdatedLogs(time: Long, threshold: Int)

    @Query("DELETE FROM TransactionLog WHERE :txnNo = txnNo")
    fun deleteLog(txnNo: String)
}