package com.aliziwa.openweatherjpmc.ui.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A helper function to keep listening to the live data until an event is captured
 * Times out after 2 seconds of no events.
 *
 * @param time - Wait duration defaulted to seconds
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    if (!latch.await(time, TimeUnit.SECONDS)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}