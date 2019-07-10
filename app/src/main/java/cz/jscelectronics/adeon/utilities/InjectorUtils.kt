package cz.jscelectronics.adeon.utilities

import android.content.Context
import cz.jscelectronics.adeon.data.AppDatabase
import cz.jscelectronics.adeon.data.DeviceRepository
import cz.jscelectronics.adeon.ui.device.viewmodels.DeviceListViewModelFactory
import cz.jscelectronics.adeon.ui.device.viewmodels.ManageDeviceViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getDeviceRepository(context: Context): DeviceRepository {
        return DeviceRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).deviceDao()
        )
    }

    fun provideDeviceListViewModelFactory(context: Context): DeviceListViewModelFactory {
        return DeviceListViewModelFactory(context.applicationContext, getDeviceRepository(context))
    }

    fun provideManageDeviceViewModelFactory(context: Context, deviceId: Long?): ManageDeviceViewModelFactory {
        return ManageDeviceViewModelFactory(context.applicationContext, getDeviceRepository(context), deviceId)
    }
}
