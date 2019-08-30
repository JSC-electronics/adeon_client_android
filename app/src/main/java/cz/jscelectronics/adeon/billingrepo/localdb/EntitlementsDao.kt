package cz.jscelectronics.adeon.billingrepo.localdb

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * No update methods necessary since for each table there is ever expecting one row, hence why
 * the primary key is hardcoded.
 */
@Dao
interface EntitlementsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noAds: NoAdvertisements)

    @Update
    fun update(noAds: NoAdvertisements)

    @Query("SELECT * FROM no_advertisements LIMIT 1")
    fun getNoAdvertisements(): LiveData<NoAdvertisements>

    @Delete
    fun delete(noAds: NoAdvertisements)

    /**
     * This is purely for convenience. The clients of this DAO don't have to discriminate among
     * [NoAdvertisements] or other entitlements, but can simply send in a list of
     * [entitlements][Entitlement].
     */
    @Transaction
    fun insert(vararg entitlements: Entitlement) {
        entitlements.forEach {
            when (it) {
                is NoAdvertisements -> insert(it)
            }
        }
    }

    @Transaction
    fun update(vararg entitlements: Entitlement) {
        entitlements.forEach {
            when (it) {
                is NoAdvertisements -> update(it)
            }
        }
    }
}