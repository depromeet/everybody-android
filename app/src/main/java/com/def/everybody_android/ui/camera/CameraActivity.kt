package com.def.everybody_android.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import com.def.everybody_android.BR
import com.def.everybody_android.R
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityCameraBinding
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import com.def.everybody_android.ui.picture.PictureActivity
import com.def.everybody_android.viewmodel.ContentUriUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActivityCameraBinding, CameraViewModel>() {
    override val layoutId: Int = R.layout.activity_camera
    override val viewModel: CameraViewModel by viewModels()
    private var displayId: Int = -1
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    @Inject
    lateinit var localStorage: LocalStorage
    private lateinit var cameraExecutor: ExecutorService
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = binding.root.let { view ->
            if (displayId == this@CameraActivity.displayId) {
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        }
    }
    private lateinit var adapter: RecyclerViewAdapter
    private val poseSelectList = listOf(
        R.drawable.ic_not_pose,
        R.drawable.ic_pose_man_whole,
        R.drawable.ic_pose_man_upper,
        R.drawable.ic_pose_man_lower,
        R.drawable.ic_pose_woman_whole,
        R.drawable.ic_pose_woman_upper,
        R.drawable.ic_pose_woman_lower
    )
    private val poseList by lazy {
        listOf(
            Unit,
            R.drawable.ic_man_whole,
            R.drawable.ic_man_upper,
            R.drawable.ic_man_lower,
            R.drawable.ic_woman_whole,
            R.drawable.ic_woman_upper,
            R.drawable.ic_woman_lower
        ).mapIndexed { index, any ->
            RecyclerItem(
                PoseData(
                    poseSelectList[index],
                    index > 0,
                    any,
                    localStorage.getPose() == index
                ), R.layout.item_camera_pose, BR.data
            )
        }.toMutableList()
    }

    data class PoseData(
        @DrawableRes val image: Int,
        val isPose: Boolean,
        val poseImage: Any,
        val isCheck: Boolean
    )

    private var albumId: String = ""
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.apply {
                    val fileUri =
                        ContentUriUtil.getFilePath(this@CameraActivity, this).toString()
                    startActivity(
                        Intent(
                            this@CameraActivity,
                            PictureActivity::class.java
                        ).apply {
                            putExtra("image", fileUri)
                            putExtra("isAlbum", true)
                            if (albumId.isNotEmpty()) putExtra("id", albumId)
                        })
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumId = intent.getStringExtra("id") ?: ""
        binding.pvFinder.post {
            setPermissionCallback(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                displayId = binding.pvFinder.display.displayId
                cameraExecutor = Executors.newSingleThreadExecutor()
                cameraSetting()
            }
        }
        adapter = RecyclerViewAdapter {
            if (it is PoseData) {
                val checkIndex =
                    adapter.getItems().indexOfFirst { data -> (data.data as PoseData).isCheck }
                if (checkIndex > -1) {
                    val checkItem = adapter.getItems()[checkIndex].data as PoseData
                    adapter.changeItem(
                        adapter.getItems()[checkIndex].copy(
                            data = checkItem.copy(
                                isCheck = !checkItem.isCheck
                            )
                        ), checkIndex
                    )
                }
                val index = adapter.getItems().indexOfFirst { data -> data.data == it }
                if (index > -1) {
                    adapter.changeItem(
                        adapter.getItems()[index].copy(data = it.copy(isCheck = !it.isCheck)),
                        index
                    )
                    if (it.poseImage is Unit) {
                        binding.imgPvPose.isVisible = false
                        viewModel.sendingClickEvents("camera/poseFilter/noPose")
                    } else {
                        viewModel.sendingClickEvents("camera/poseFilter/pose$index")
                        binding.imgPvPose.isVisible = true
                        binding.imgPvPose.setImageResource(it.poseImage as @DrawableRes Int)
                    }
                    localStorage.setPose(index)
                }
            }
        }.apply {
            setItems(poseList)
        }
        poseList.map { it.data as PoseData }.find { it.isCheck }?.let {
            if (it.poseImage is Unit) {
                binding.imgPvPose.isVisible = false
            } else {
                binding.imgPvPose.isVisible = true
                binding.imgPvPose.setImageResource(it.poseImage as @DrawableRes Int)
            }
        }
        binding.imgGrid.isSelected = localStorage.isGrid()
        binding.includeGrid.isVisible = binding.imgGrid.isSelected
        binding.recyclerPose.adapter = adapter
        Handler(Looper.myLooper() ?: return).postDelayed({
            binding.groupInfo.isVisible = false
        }, 3000)
    }

    override fun init() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    CameraViewModel.ClickEvent.Album -> {
                        openFileDocument()
                        viewModel.sendingClickEvents("camera/btn/album")
                    }
                    CameraViewModel.ClickEvent.Pose -> {
                        binding.motionCamera.transitionToEnd()
                        viewModel.sendingClickEvents("camera/btn/pose")
                    }
                    CameraViewModel.ClickEvent.Shutter -> {
                        clickShutter()
                        viewModel.sendingClickEvents("camera/btn/shot")
                    }
                    CameraViewModel.ClickEvent.Back -> {
                        finish()
                        viewModel.sendingClickEvents("camera/btn/back")
                    }
                    CameraViewModel.ClickEvent.Switch -> {
                        lensFacing =
                            if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK
                            else CameraSelector.LENS_FACING_FRONT
                        bindCameraUseCases()
                    }
                    CameraViewModel.ClickEvent.Grid -> {
                        binding.imgGrid.isSelected = !binding.imgGrid.isSelected
                        binding.includeGrid.isVisible = binding.imgGrid.isSelected
                        localStorage.setGrid(binding.imgGrid.isSelected)
                        if (binding.includeGrid.isVisible) viewModel.sendingClickEvents("camera/toggle/grid/on")
                        else viewModel.sendingClickEvents("camera/toggle/grid/off")
                    }
                    CameraViewModel.ClickEvent.Expand -> {
                        binding.motionCamera.transitionToStart()
                    }
                }
            }
        }
        displayManager.registerDisplayListener(displayListener, null)
    }

    private fun clickShutter() {
        imageCapture?.let { imageCapture ->
            val photoFile = createFile(filesDir, FILENAME, PHOTO_EXTENSION)
            val metadata = ImageCapture.Metadata().apply {
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            imageCapture.takePicture(
                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                        Log.d(TAG, "Photo capture succeeded: $savedUri")
                        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                            this@CameraActivity,
                            arrayOf(savedUri.toFile().absolutePath),
                            arrayOf(mimeType)
                        ) { path, uri ->
                            val fileUri = if (uri != null) ContentUriUtil.getFilePath(this@CameraActivity, uri).toString()
                            else path
                            startActivity(
                                Intent(this@CameraActivity, PictureActivity::class.java).apply {
                                    putExtra(
                                        "image",
                                        fileUri
                                    )
                                    if (albumId.isNotEmpty()) putExtra("id", albumId)
                                })
                        }
                    }
                })
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.root.postDelayed({
                    binding.root.foreground = ColorDrawable(Color.WHITE)
                    binding.root.postDelayed(
                        { binding.root.foreground = null }, ANIMATION_FAST_MILLIS
                    )
                }, ANIMATION_SLOW_MILLIS)
            }
        }
    }


    private fun cameraSetting() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val rotation = binding.pvFinder.display.rotation
        val cameraProvider = cameraProvider ?: return run {
            toast("Camera Setting Error")
            finish()
        }
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setAnalyzer(Executors.newSingleThreadExecutor()) { luma ->
                    Log.d(TAG, "Average luminosity: $luma")
                }
            }
        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            preview.setSurfaceProvider(binding.pvFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    override fun onDestroy() {
        super.onDestroy()
        displayManager.unregisterDisplayListener(displayListener)
    }

    private fun openFileDocument() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            resultLauncher.launch(this)
        }
    }

    companion object {

        private const val TAG = "CameraXBasic"
        const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val PHOTO_EXTENSION = ".jpg"
        private const val ANIMATION_FAST_MILLIS = 50L
        private const val ANIMATION_SLOW_MILLIS = 100L

        /** Helper function used to create a timestamped file */
        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}