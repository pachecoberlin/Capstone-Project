package de.pacheco.android.utilities

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    @Test
    fun calculateNoOfColumnsTest1() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("de.pacheco.android.utilities.test", appContext.packageName)
        assertEquals(2, calculateNoOfColumns(appContext))
    }
}