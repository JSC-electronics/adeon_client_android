/**
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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