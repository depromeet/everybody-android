package com.def.everybody_android.ui.dialog.message

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogMessageBinding
import com.def.everybody_android.databinding.DialogServiceNotBinding

class MessageDialog(private val isCancel: Boolean = false, private val action: () -> Unit) :
    BaseDialogFragment<DialogMessageBinding, MessageViewModel>() {
    override val viewModel: MessageViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_message
    private var title = ""
    private var content = ""
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun init() {
        super.init()
        binding.twTitle.text = title
        binding.twContent.text = content
        binding.twCancel.isVisible = isCancel
        binding.twCancel.setOnClickListener {
            dismiss()
        }
        binding.twOk.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }

    fun setMessage(title: String, content: String): MessageDialog {
        this.title = title
        this.content = content
        return this
    }

}