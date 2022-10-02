package com.def.everybody_android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.def.everybody_android.BR
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.coroutines.flow.collect

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(
) : Fragment() {

    lateinit var binding: B
    abstract val viewModel: VM
    abstract val layoutId : Int
    private val mixpanelAPI: MixpanelAPI by lazy { MixpanelAPI.getInstance(requireContext(), "96abc171423dea4c9d24642c4e40f1b9", true) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMixpanel(mixpanelAPI)
        binding.lifecycleOwner = this
        binding.setVariable(BR.vm, viewModel)
        init()
    }

    fun sendingClickEvents(event: String) {
        mixpanelAPI.track(event)
    }

    open fun init() {
        repeatOnStarted {
            viewModel.toast.collect { context?.toast(it) }
        }
    }
}