package com.example.everybody_android.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityCreateFolderBinding
import com.example.everybody_android.viewmodel.CreateFolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFolderActivity : BaseActivity<ActivityCreateFolderBinding, CreateFolderViewModel>() {

    override val layoutId: Int = R.layout.activity_create_folder
    override val viewModel: CreateFolderViewModel = CreateFolderViewModel()

    override fun init() {
        edittextClick()
        back()
        createAlbum()
    }

    private fun back() {
        binding.ibBefore.setOnClickListener {
            finish()
        }
    }

    private fun edittextClick() {
        binding.etFolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etFolderName.text.toString() != "") binding.ibSave.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_blue_storage)
                )
                else binding.ibSave.setImageDrawable(resources.getDrawable(R.drawable.ic_gray_storage))
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun createAlbum() {
        binding.ibSave.setOnClickListener {
            if (binding.etFolderName.text.toString().isNotEmpty()) {
                viewModel.createFolder(binding.etFolderName.text.toString())
                toast()
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun toast() {
        val image = ImageView(applicationContext)
        image.setImageResource(R.drawable.ic_toast_album)
        val toast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, Gravity.CENTER, Gravity.TOP)
        toast.view = image
        toast.show()
    }

}