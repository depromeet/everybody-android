package com.example.everybody_android.ui.picture

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityPictureBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.picture.folder.FolderChoiceFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PictureActivity : BaseActivity<ActivityPictureBinding, PictureViewModel>() {
    override val layoutId: Int = R.layout.activity_picture
    override val viewModel: PictureViewModel by viewModels()
    private val folderChoiceFragment by lazy { FolderChoiceFragment() }
    private lateinit var pictureFragment: PictureFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra("image")) finish()
        pictureFragment = PictureFragment(intent.getStringExtra("image") ?: "")
        addFragment(pictureFragment)
    }

    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_fragment, fragment)
        transaction.commit()
    }

    private fun removeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }

    fun saveComplete() {
        removeFragment(pictureFragment)
        addFragment(folderChoiceFragment)
    }

    override fun init() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    PictureViewModel.ClickEvent.Back -> {
                        if (supportFragmentManager.fragments.contains(folderChoiceFragment)) removeFragment(
                            folderChoiceFragment
                        )
                        else finish()
                    }
                    PictureViewModel.ClickEvent.Next -> {
                        pictureFragment.saveView()
                    }
                }
            }
        }
    }
}