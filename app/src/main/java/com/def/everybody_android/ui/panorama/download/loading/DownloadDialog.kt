package com.def.everybody_android.ui.panorama.download.loading

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogDownloadBinding
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class DownloadDialog : BaseDialogFragment<DialogDownloadBinding, DownloadViewModel>() {
    override val viewModel: DownloadViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_download
    private var isComplete = false
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onDownload(percent: Int) {
        binding.twTitle.text = getString(R.string.str_download_title, "${percent}%")
    }

    fun onComplete() {
        isComplete = true
        binding.imgComplete.isVisible = true
        binding.twConfirm.text = getString(R.string.str_confirm)
        binding.twContent.text = getString(R.string.str_download_complete)
        binding.twTitle.text = getString(R.string.str_download_complete_title)
    }

    fun onCancel() {
        isComplete = true
        binding.imgComplete.isVisible = true
        binding.twConfirm.text = getString(R.string.str_confirm)
        binding.twContent.text = getString(R.string.str_download_complete)
        binding.twTitle.text = getString(R.string.str_download_cancel_title)
    }

    override fun init() {
        super.init()
        repeatOnStarted {
            viewModel.event.collect {
                dismiss()
            }
        }
    }
}