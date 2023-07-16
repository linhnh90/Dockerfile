package com.styl.pa.database

import androidx.room.*
import com.styl.pa.entities.reservation.ReceiptRequest

/**
 * Created by trangpham on 10/16/2018
 */
@Dao
interface ReceiptDao {

    @Query("SELECT * FROM Receipt")
    fun getAllReceipt(): List<ReceiptRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ReceiptRequest)

    @Delete
    fun delete(item: ReceiptRequest)

    @Query("DELETE FROM Receipt WHERE txnNo LIKE :txnNo")
    fun deleteByTxn(txnNo: String?)

    @Query("UPDATE Receipt SET errorCode = :errorCode, errorMessage = :errorMessage, errorLatestUpdate = :latestUpdate, createdAt = :createdAt WHERE shoppingCartId LIKE :shoppingCartId")
    fun updateErrorGenerateReceipt(shoppingCartId: String, errorCode: String?, errorMessage: String?,
                                   createdAt: Long?, latestUpdate: Long?)

    @Query("UPDATE Receipt SET errorCode = :errorCode, errorMessage = :errorMessage, createdAt = :createdAt WHERE shoppingCartId LIKE :shoppingCartId")
    fun updateErrorGenerateReceipt(shoppingCartId: String, errorCode: String?, errorMessage: String?, createdAt: Long?)

    @Query("UPDATE Receipt SET errorCode = :errorCode, errorMessage = :errorMessage WHERE shoppingCartId LIKE :shoppingCartId")
    fun updateErrorGenerateReceipt(shoppingCartId: String, errorCode: String?, errorMessage: String?)

    @Query("UPDATE Receipt SET errorLatestUpdate = :latestUpdateAt WHERE shoppingCartId LIKE :shoppingCartIds")
    fun updateLatestUpdate(shoppingCartIds: List<String>, latestUpdateAt: Long?)
}