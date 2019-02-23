package cz.jsc.electronics.arduinosms.utilities

import android.content.Context
import cz.jsc.electronics.arduinosms.data.AppDatabase
import cz.jsc.electronics.arduinosms.data.DeviceRepository
import cz.jsc.electronics.arduinosms.viewmodels.AddDeviceViewModelFactory
import cz.jsc.electronics.arduinosms.viewmodels.DeviceListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getDeviceRepository(context: Context): DeviceRepository {
        return DeviceRepository.getInstance(
                AppDatabase.getInstance(context.applicationContext).deviceDao())
    }

    fun provideDeviceListViewModelFactory(context: Context): DeviceListViewModelFactory {
        val repository = getDeviceRepository(context)
        return DeviceListViewModelFactory(repository)
    }

    fun provideAddDeviceViewModelFactory(context: Context): AddDeviceViewModelFactory {
        val repository = getDeviceRepository(context)
        return AddDeviceViewModelFactory(repository)
    }
}
