package com.example.everybody_android.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Camera
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.everybody_android.BR
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityCameraBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.toast
import com.example.everybody_android.ui.picture.PictureActivity
import com.example.everybody_android.viewmodel.ContentUriUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
    private val postList = listOf(
        Unit,
        R.drawable.ic_man_whole,
        R.drawable.ic_man_upper,
        R.drawable.ic_man_lower,
        R.drawable.ic_woman_whole,
        R.drawable.ic_woman_upper,
        R.drawable.ic_woman_lower
    ).map { RecyclerItem(it, R.layout.item_camera_pose, BR.data) }.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.recyclerPose.adapter = RecyclerViewAdapter {
            if (it is Unit) binding.imgPvPose.isVisible = false
            else {
                binding.imgPvPose.isVisible = true
                binding.imgPvPose.setImageResource(it as @androidx.annotation.DrawableRes Int)
            }
        }.apply {
            setItems(postList)
        }
        Handler(Looper.myLooper() ?: return).postDelayed({
            binding.groupInfo.isVisible = false
        }, 3000)
    }

    private fun getOutputDirectory(): File {
        val appContext = applicationContext
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }

    override fun init() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    CameraViewModel.ClickEvent.Album -> toast("앨범 버튼")
                    CameraViewModel.ClickEvent.Pose -> binding.motionCamera.transitionToEnd()
                    CameraViewModel.ClickEvent.Shutter -> clickShutter()
                    CameraViewModel.ClickEvent.Back -> finish()
                    CameraViewModel.ClickEvent.Switch -> {
                        lensFacing =
                            if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK
                            else CameraSelector.LENS_FACING_FRONT
                        bindCameraUseCases()
                    }
                    CameraViewModel.ClickEvent.Grid -> {
                        binding.imgGrid.isSelected = !binding.imgGrid.isSelected
                        binding.includeGrid.isVisible = binding.imgGrid.isSelected
                    }
                    CameraViewModel.ClickEvent.Expand -> binding.motionCamera.transitionToStart()
                }
            }
        }
        displayManager.registerDisplayListener(displayListener, null)
    }

    private fun clickShutter() {
        imageCapture?.let { imageCapture ->
            val photoFile = createFile(getOutputDirectory(), FILENAME, PHOTO_EXTENSION)
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

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            sendBroadcast(
                                Intent(Camera.ACTION_NEW_PICTURE, savedUri)
                            )
                        }

                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                            this@CameraActivity,
                            arrayOf(savedUri.toFile().absolutePath),
                            arrayOf(mimeType)
                        ) { _, uri ->
                            val fileUri =
                                ContentUriUtil.getFilePath(this@CameraActivity, uri).toString()

                            startActivity(
                                Intent(
                                    this@CameraActivity,
                                    PictureActivity::class.java
                                ).apply {
                                    putExtra("image", fileUri)
                                })
                            finish()
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
                it.setAnalyzer(Executors.newSingleThreadExecutor(), { luma ->
                    Log.d(TAG, "Average luminosity: $luma")
                })
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

    companion object {

        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val ANIMATION_FAST_MILLIS = 50L
        private const val ANIMATION_SLOW_MILLIS = 100L

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}