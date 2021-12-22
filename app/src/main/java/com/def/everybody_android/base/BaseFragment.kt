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
import kotlinx.coroutines.flow.collect

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(
) : Fragment() {

    lateinit var binding: B
    abstract val viewModel: VM
    abstract val layoutId : Int

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
        binding.lifecycleOwner = this
        binding.setVariable(BR.vm, viewModel)
        init()
    }

    open fun init() {
        repeatOnStarted {
            viewModel.toast.collect { context?.toast(it) }
        }
    }
}