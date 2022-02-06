package com.def.everybody_android.ui.dialog.feedback

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogFeedbackBinding
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class FeedBackDialog : BaseDialogFragment<DialogFeedbackBinding, FeedBackViewModel>() {
    override val viewModel: FeedBackViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_feedback

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onNumberClick(1)
    }

    override fun init() {
        super.init()
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    FeedBackViewModel.Event.Cancel -> dismiss()
                    FeedBackViewModel.Event.FeedBack -> dismiss()
                    is FeedBackViewModel.Event.Number -> {
                        binding.twNumber1.isSelected = it.number == 1
                        binding.twNumber2.isSelected = it.number == 2
                        binding.twNumber3.isSelected = it.number == 3
                        binding.twNumber4.isSelected = it.number == 4
                        binding.twNumber5.isSelected = it.number == 5
                    }
                }
            }
        }
    }
}