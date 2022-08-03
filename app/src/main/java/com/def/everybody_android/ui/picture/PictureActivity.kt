package com.def.everybody_android.ui.picture

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityPictureBinding
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.ui.dialog.loading.LoadingDialog
import com.def.everybody_android.ui.picture.folder.FolderChoiceFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executors


@AndroidEntryPoint
class PictureActivity : BaseActivity<ActivityPictureBinding, PictureViewModel>() {
    override val layoutId: Int = R.layout.activity_picture
    override val viewModel: PictureViewModel by viewModels()
    private val folderChoiceFragment by lazy { FolderChoiceFragment() }
    private var isFolder = false
    private var albumId: String = ""
    private val loadingDialog = LoadingDialog()
    private var isAlbum = false
    private lateinit var pictureFragment: PictureFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra("image")) finish()
        albumId = intent.getStringExtra("id") ?: ""
        isAlbum = intent.getBooleanExtra("isAlbum", false)
        pictureFragment = PictureFragment(intent.getStringExtra("image") ?: "", isAlbum)
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
            map["albumId"] = albumId
            photoUpload(map)
        }
    }

    fun photoUpload(map: Map<String, String>) {
        loadingDialog.show(supportFragmentManager, "")
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
                        else {
                            if (SDK_INT >= Build.VERSION_CODES.R) {
                                setPermissionCallback(arrayOf(Manifest.permission.CAMERA)) {
                                    if (Environment.isExternalStorageManager()) {
                                        pictureFragment.saveView()
                                        return@setPermissionCallback
                                    }
                                    try {
                                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                        intent.addCategory("android.intent.category.DEFAULT")
                                        intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                                        setDataPermissionCallback(intent) { pictureFragment.saveView() }
                                    } catch (e: Exception) {
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                        setDataPermissionCallback(intent) { pictureFragment.saveView() }
                                    }
                                }
                            } else {
                                setPermissionCallback(
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                ) {
                                    pictureFragment.saveView()
                                }
                            }
                        }
                    }
                    PictureViewModel.ClickEvent.Complete -> {
                        loadingDialog.dismiss()
                        finish()
                    }
                }
            }
        }
    }
}