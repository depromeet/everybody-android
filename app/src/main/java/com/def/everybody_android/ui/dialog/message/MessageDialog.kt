package com.def.everybody_android.ui.dialog.message

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogServiceNotBinding

class MessageDialog(private val isCancel: Boolean = false, private val action: () -> Unit) :
    BaseDialogFragment<DialogServiceNotBinding, MessageViewModel>() {
    override val viewModel: MessageViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_service_not
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
        binding.twOk.setOnClickListener {
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