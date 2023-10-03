package com.aliziwa.openweatherjpmc.ui.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Property with nullable backing field that is cleared when the provided [Lifecycle]s [onDestroy] event is triggered.
 *
 */
class ClearOnDestroyProperty<T>(
    private val lifecycleProvider: () -> Lifecycle
) : ReadWriteProperty<Any, T>, DefaultLifecycleObserver {

    @VisibleForTesting
    var field: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        field ?: throw UninitializedPropertyAccessException("Not set")

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        field = value
        lifecycleProvider().addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        field = null
    }
}