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
    private var isFolder = false
    private var albumId: String = ""
    private lateinit var pictureFragment: PictureFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra("image")) finish()
        albumId = intent.getStringExtra("albumId") ?: ""
        pictureFragment = PictureFragment(intent.getStringExtra("image") ?: "")
        addFragment(pictureFragment)
        addFragment(folderChoiceFragment)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(folderChoiceFragment)
        transaction.commit()
    }

    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_fragment, fragment)
        transaction.commit()
    }

    fun saveComplete(map: HashMap<String, String>) {
        if (albumId.isEmpty()) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.hide(pictureFragment)
            folderChoiceFragment.setValue(map)
            transaction.show(folderChoiceFragment)
            transaction.commit()
            isFolder = true
        } else {
            map["album_id"] = albumId
            photoUpload(map)
        }
    }

    fun photoUpload(map: Map<String, String>) {
        viewModel.photoUpload(map)
    }

    override fun init() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    PictureViewModel.ClickEvent.Back -> {
                        val transaction = supportFragmentManager.beginTransaction()
                        if (isFolder) {
                            transaction.hide(folderChoiceFragment)
                            transaction.show(pictureFragment)
                            transaction.commit()
                            isFolder = false
                        } else finish()
                    }
                    PictureViewModel.ClickEvent.Next -> {
                        if (isFolder) folderChoiceFragment.getValue()
                        else pictureFragment.saveView()
                    }
                    PictureViewModel.ClickEvent.Complete -> finish()
                }
            }
        }
    }
}