/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.jsc.electronics.arduinosms.utilities

import android.content.Context
import cz.jsc.electronics.arduinosms.data.AppDatabase
import cz.jsc.electronics.arduinosms.data.SmartDeviceRepository

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getSmartDeviceRepository(context: Context): SmartDeviceRepository {
        return SmartDeviceRepository.getInstance(
                AppDatabase.getInstance(context.applicationContext).smartDeviceDao())
    }

//    fun provideDeviceListViewModelFactory(context: Context): DeviceListViewModelFactory {
//        val repository = getSmartDeviceRepository(context)
//        return DeviceListViewModelFactory(repository)
//    }
}
