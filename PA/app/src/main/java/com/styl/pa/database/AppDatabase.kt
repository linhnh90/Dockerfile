package com.styl.pa.database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import android.content.Context
import com.styl.pa.entities.payment.PaymentItem
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.reservation.ReceiptRequest

/**
 * Created by trangpham on 10/13/2018
 */
@Database(
    entities = arrayOf(PaymentRequest::class, PaymentItem::class, ReceiptRequest::class),
    version = AppDatabase.DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        const val DB_NAME = "pa.db"
        const val DB_VERSION = 8

        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add itemType, outletId, resourceDateTime, outletName, customerAge, outletTypeName columns into TransactionItem table
                database.execSQL("ALTER TABLE TransactionItem ADD outletId TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD resourceDateTime TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD outletName TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD customerAge TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD outletTypeName TEXT")

                // add pdfFile column into TransactionLog table
                database.execSQL("ALTER TABLE TransactionLog ADD pdfFile TEXT")
            }
        }

        private val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add itemType, outletId, resourceDateTime, outletName, customerAge, outletTypeName columns into TransactionItem table
                database.execSQL("ALTER TABLE TransactionItem ADD outletId TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD resourceDateTime TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD outletName TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD customerAge TEXT")
                database.execSQL("ALTER TABLE TransactionItem ADD outletTypeName TEXT")

                // add pdfFile column into TransactionLog table
                database.execSQL("ALTER TABLE TransactionLog ADD pdfFile TEXT")
                database.execSQL("ALTER TABLE TransactionLog ADD completedAt INTEGER")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add completedAt column into TransactionLog table
                database.execSQL("ALTER TABLE TransactionLog ADD completedAt INTEGER")
            }
        }

        private val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add completedAt column into TransactionLog table
                database.execSQL("ALTER TABLE TransactionLog ADD completedAt INTEGER")

                // add committeeId column into Receipt table
                database.execSQL("ALTER TABLE Receipt ADD committeeId TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add committeeId column into Receipt table
                database.execSQL("ALTER TABLE Receipt ADD committeeId TEXT")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add errorCode column into Receipt table
                database.execSQL("ALTER TABLE Receipt ADD errorCode TEXT")
                database.execSQL("ALTER TABLE Receipt ADD errorMessage TEXT")
                database.execSQL("ALTER TABLE Receipt ADD errorLatestUpdate INTEGER")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add errorCode column into Receipt table
                database.execSQL("ALTER TABLE Receipt ADD createdAt INTEGER")


            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add referenceId column to
                database.execSQL("ALTER TABLE TransactionLog ADD referenceId TEXT")
                database.execSQL("ALTER TABLE TransactionLog ADD email TEXT")
                database.execSQL("ALTER TABLE TransactionLog ADD cartId TEXT")
                database.execSQL("ALTER TABLE TransactionLog ADD paymentCode INTEGER")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add outletId column to
                database.execSQL("ALTER TABLE TransactionLog ADD outletId TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DB_NAME
                    )
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_1_3)
                        .addMigrations(MIGRATION_2_3)
                        .addMigrations(MIGRATION_2_4)
                        .addMigrations(MIGRATION_3_4)
                        .addMigrations(MIGRATION_4_5)
                        .addMigrations(MIGRATION_5_6)
                        .addMigrations(MIGRATION_6_7)
                        .addMigrations(MIGRATION_7_8)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
//            if (instance?.isOpen == true && instance?.inTransaction() == false) {
//                instance?.close()
            instance = null
//            }
        }
    }

    abstract fun txnLogDao(): TransactionLogDao
    abstract fun txnItemDao(): TransactionItemDao
    abstract fun receiptDao(): ReceiptDao
}