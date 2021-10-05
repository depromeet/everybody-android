package com.example.everybody_android.ui.camera

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityCameraBinding

class CameraActivity : BaseActivity<ActivityCameraBinding, CameraViewModel>() {
    override val layoutId: Int = R.layout.activity_camera
    override val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPermissionCallback(arrayOf(Manifest.permission.CAMERA)) {
            //카메라 엑티비티 키기 전에 먼저 체크
        }
    }

    override fun init() {

    }
}