package cz.jscelectronics.adeon.billingrepo.localdb


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        entities = [
            AugmentedSkuDetails::class,
            CachedPurchase::class,
            NoAdvertisements::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(PurchaseTypeConverter::class)
abstract class LocalBillingDb : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao
    abstract fun entitlementsDao(): EntitlementsDao
    abstract fun skuDetailsDao(): AugmentedSkuDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: LocalBillingDb? = null
        private val DATABASE_NAME = "purchase_db"

        fun getInstance(context: Context): LocalBillingDb =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context.applicationContext).also {
                        INSTANCE = it
                    }
                }

        private fun buildDatabase(appContext: Context): LocalBillingDb {
            return Room.databaseBuilder(appContext, LocalBillingDb::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration() // Data is cache, so it is OK to delete
                    .build()
        }
    }
}
