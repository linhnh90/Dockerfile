package com.styl.pa.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.styl.pa.entities.payment.PaymentItem

/**
 * Created by trangpham on 10/13/2018
 */
@Dao
interface TransactionItemDao {

    @Query("SELECT * FROM TransactionItem WHERE txnNo LIKE :txnNo")
    fun getItemsBy(txnNo: String): List<PaymentItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<PaymentItem>)

    @Query("DELETE FROM TransactionItem WHERE txnNo LIKE :txnNo")
    fun deleteItems(txnNo: String)
}