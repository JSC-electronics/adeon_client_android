package cz.jsc.electronics.smscontrol.utilities

import android.content.Context
import cz.jsc.electronics.smscontrol.data.AppDatabase
import cz.jsc.electronics.smscontrol.data.DeviceRepository
import cz.jsc.electronics.smscontrol.viewmodels.DeviceListViewModelFactory
import cz.jsc.electronics.smscontrol.viewmodels.ManageDeviceViewModelFactory

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
