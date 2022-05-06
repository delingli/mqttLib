package com.itc.switchdevicecomponent

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itc.switchdevicecomponent.annation.DeviceType

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        DeviceOptManager.toInit(appContext)
//        assertEquals("com.itc.switchdevicecomponent.test", appContext.packageName)
        DeviceOptManager.getSwitchMachineOpt()?.setEndBootForHra()
        DeviceOptManager.getSwitchMachineOpt()?.handlerOpt(
            DeviceType.MODULE_HEZI,
            "2022-5-6",
            100,
            1,
            true

        )

    }
}