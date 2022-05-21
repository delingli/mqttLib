package com.itc.switchdevicecomponent

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.work.RebotWork
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var context: Context
    private lateinit var executor: Executor
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val worker = TestListenableWorkerBuilder<RebotWork>(context).build()
        runBlocking {
            val result = worker.doWork()
//            MatcherAssert.assertThat(result,true)
//            assertThat(result, 'is`(WorkInfo.State.ENQUEUED))
        }
//        testSwitch()

    }

    private fun testSwitch() {
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