package com.aliziwa.openweatherjpmc.ui.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * BaseFragment
 *
 * A lot of crosscutting concerns can be thrown in here including lifecycle logging/analytics,
 * viewmodel instantiations etc.
 *
 * I'm only using it for creating a [ViewBinding] clearing property.
 */
open class BaseFragment<VB: ViewBinding> : Fragment() {
    var binding by ClearOnDestroyProperty<VB> { viewLifecycleOwner.lifecycle }
}