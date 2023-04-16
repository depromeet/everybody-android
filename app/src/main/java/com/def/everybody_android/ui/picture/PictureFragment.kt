package com.def.everybody_android.ui.picture

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.Camera
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.def.everybody_android.*
import com.def.everybody_android.base.BaseFragment
import com.def.everybody_android.databinding.FragmentPictureBinding
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.viewmodel.ContentUriUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import okhttp3.internal.assertThreadDoesntHoldLock
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PictureFragment(private val image: String, private val isAlbum: Boolean) :
    BaseFragment<FragmentPictureBinding, PictureFragmentViewModel>() {
    override val viewModel: PictureFragmentViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_picture
    private val datePictureFormat = SimpleDateFormat("yyyy.MM.dd a hh:mm", Locale("en", "KR"))
    private val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en", "KR"))
    private val timeFormat = SimpleDateFormat("a hh:mm", Locale("en", "KR"))
    private val dateTextFormat = SimpleDateFormat("yyyy MMM dd kk:mm", Locale("en", "KR"))
    private var part = "whole"
    private var isFirst = true

    @Inject
    lateinit var localStorage: LocalStorage

    private fun initSetting() {
        setPictureTime()
        viewModel.onClickEvent(PictureFragmentViewModel.ClickEvent.PartTab)
        viewModel.onClickEvent(
            when (localStorage.getPicturePart()) {
                "whole" -> PictureFragmentViewModel.ClickEvent.PartWhole
                "upper" -> PictureFragmentViewModel.ClickEvent.PartUpper
                else -> PictureFragmentViewModel.ClickEvent.PartLower
            }
        )
        viewModel.onClickEvent(when(localStorage.getTimeSetting()){
            "photoTime"->PictureFragmentViewModel.ClickEvent.TimePicture
            "currentTime"->PictureFragmentViewModel.ClickEvent.TimeNow
            else->PictureFragmentViewModel.ClickEvent.TimePerson
        })
        binding.includePictureTime.npDay.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npDay.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npYear.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npYear.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npMonth.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npMonth.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npMin.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npMin.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npHour.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npHour.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureWeight.npFirst.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureWeight.npFirst.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureWeight.npLast.typeface = requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureWeight.npLast.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        settingNumberPickerValue()
        settingNumberPickerEvent()
        settingWeightEvent()
        settingWeightValue()
        isFirst = false
    }

    private fun setPictureTime() {
        val date = datePictureFormat.format(Date(File(image).lastModified())).split(" ")
        val infoText = dateTextFormat.format(System.currentTimeMillis()).split(" ")
        binding.twDate.text = "${date[0]} ${date[1]} ${date[2]}"
        binding.includePictureTime.twYear.text = infoText[0]
        binding.includePictureTime.twMonth.text = infoText[1]
        binding.includePictureTime.twDay.text = infoText[2]
        binding.includePictureTime.twTime.text = infoText[3]
    }

    private fun setNowTime() {
        val date = datePictureFormat.format(System.currentTimeMillis()).split(" ")
        val infoText = dateTextFormat.format(System.currentTimeMillis()).split(" ")
        binding.twDate.text = "${date[0]} ${date[1]} ${date[2]}"
        binding.includePictureTime.twYear.text = infoText[0]
        binding.includePictureTime.twMonth.text = infoText[1]
        binding.includePictureTime.twDay.text = infoText[2]
        binding.includePictureTime.twTime.text = infoText[3]
    }

    override fun init() {
        binding.imgPicture.imageLoad(image)
        initSetting()
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    PictureFragmentViewModel.ClickEvent.PartTab, PictureFragmentViewModel.ClickEvent.TimeTab, PictureFragmentViewModel.ClickEvent.WeightTab -> {
                        binding.twPartChoice.isSelected = it == PictureFragmentViewModel.ClickEvent.PartTab
                        binding.twTimeChoice.isSelected = it == PictureFragmentViewModel.ClickEvent.TimeTab
                        binding.twWeightChoice.isSelected = it == PictureFragmentViewModel.ClickEvent.WeightTab
                        binding.twPartChoice.typeface =
                            requireContext().typeFace(if (it == PictureFragmentViewModel.ClickEvent.PartTab) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.twTimeChoice.typeface =
                            requireContext().typeFace(if (it == PictureFragmentViewModel.ClickEvent.TimeTab) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.twWeightChoice.typeface =
                            requireContext().typeFace(if (it == PictureFragmentViewModel.ClickEvent.WeightTab) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.includePicturePart.root.isVisible = it == PictureFragmentViewModel.ClickEvent.PartTab
                        binding.includePictureTime.root.isVisible = it == PictureFragmentViewModel.ClickEvent.TimeTab
                        binding.includePictureWeight.root.isVisible = it == PictureFragmentViewModel.ClickEvent.WeightTab
                        if (!isFirst) {
                            if (it == PictureFragmentViewModel.ClickEvent.PartTab) viewModel.sendingClickEvents("photo/tab/selectPart")
                            if (it == PictureFragmentViewModel.ClickEvent.TimeTab) viewModel.sendingClickEvents("photo/tab/inputTime")
                            if (it == PictureFragmentViewModel.ClickEvent.WeightTab) viewModel.sendingClickEvents("photo/tab/inputWeight")
                        }
                    }
                    PictureFragmentViewModel.ClickEvent.PartUpper, PictureFragmentViewModel.ClickEvent.PartWhole, PictureFragmentViewModel.ClickEvent.PartLower -> {
                        binding.includePicturePart.flWhole.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartWhole
                        binding.includePicturePart.twWhole.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartWhole
                        binding.includePicturePart.flLower.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartLower
                        binding.includePicturePart.twLower.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartLower
                        binding.includePicturePart.flUpper.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartUpper
                        binding.includePicturePart.twUpper.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartUpper
                        part = when (it) {
                            PictureFragmentViewModel.ClickEvent.PartLower -> "lower"
                            PictureFragmentViewModel.ClickEvent.PartUpper -> "upper"
                            else -> "whole"
                        }
                        localStorage.setPicturePart(part)
                        if (!isFirst) {
                            if (it == PictureFragmentViewModel.ClickEvent.PartWhole) viewModel.sendingClickEvents("photo/selectPart/btn/whole")
                            if (it == PictureFragmentViewModel.ClickEvent.PartLower) viewModel.sendingClickEvents("photo/selectPart/btn/lower")
                            if (it == PictureFragmentViewModel.ClickEvent.PartUpper) viewModel.sendingClickEvents("photo/selectPart/btn/upper")
                        }
                    }
                    PictureFragmentViewModel.ClickEvent.TimeNow, PictureFragmentViewModel.ClickEvent.TimePicture, PictureFragmentViewModel.ClickEvent.TimePerson -> {
                        binding.includePictureTime.groupNumberpicker.isVisible =
                            it == PictureFragmentViewModel.ClickEvent.TimePerson
                        binding.includePictureTime.groupTimeTw.isVisible =
                            it == PictureFragmentViewModel.ClickEvent.TimePicture || it == PictureFragmentViewModel.ClickEvent.TimeNow
                        binding.includePictureTime.twTimePicture.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.TimePicture
                        binding.includePictureTime.twNowPicture.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.TimeNow
                        binding.includePictureTime.twPersonPicture.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.TimePerson
                        if (it == PictureFragmentViewModel.ClickEvent.TimePicture) setPictureTime()
                        if (it == PictureFragmentViewModel.ClickEvent.TimeNow) setNowTime()
                        if (!isFirst) {
                            if (it == PictureFragmentViewModel.ClickEvent.TimePicture) {
                                localStorage.setTimeSetting("photoTime")
                                viewModel.sendingClickEvents("photo/btn/photoTime")
                            }
                            if (it == PictureFragmentViewModel.ClickEvent.TimeNow) {
                                localStorage.setTimeSetting("currentTime")
                                viewModel.sendingClickEvents("photo/btn/currentTime")
                            }
                            if (it == PictureFragmentViewModel.ClickEvent.TimePerson) {
                                localStorage.setTimeSetting("directInput")
                                viewModel.sendingClickEvents("photo/btn/directInput")
                            }
                        }
                    }
                    PictureFragmentViewModel.ClickEvent.WeightCheck -> {
                        binding.includePictureWeight.ibWeightCheck.isSelected = !binding.includePictureWeight.ibWeightCheck.isSelected
                        binding.twWeight.isVisible = binding.includePictureWeight.ibWeightCheck.isSelected
                        localStorage.setWeightVisible(binding.includePictureWeight.ibWeightCheck.isSelected)
                        if (!isFirst) {
                            if (binding.twWeight.isVisible) viewModel.sendingClickEvents("photo/inputWeight/toggle/on")
                            else viewModel.sendingClickEvents("photo/inputWeight/toggle/off")
                        }
                    }
                }
            }
        }
    }

    fun saveView() {
        val pictureView = binding.clPicture
        val bitmap =
            Bitmap.createBitmap(pictureView.width, pictureView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.clPicture.draw(canvas)
        localStorage.setWeight(binding.twWeight.text.toString().replace("kg", ""))
        if (!localStorage.isAppStorage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(bitmap)
            else saveTheImageLegacyStyle(bitmap)
        } else {
            val image = CameraActivity.createFile(requireContext().filesDir, CameraActivity.FILENAME, CameraActivity.PHOTO_EXTENSION)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(image))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                requireContext().sendBroadcast(Intent(Camera.ACTION_NEW_PICTURE, image.toUri()))
            }
            val newExif = ExifInterface(image)
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, "1")
            newExif.saveAttributes()
            activity?.apply {
                if (this is PictureActivity) {
                    val valueMap = hashMapOf(
                        "bodyPart" to part,
                        "image" to image.path
                    )
                    saveComplete(valueMap)
                }
            }
        }
    }

    private fun saveImageInQ(bitmap: Bitmap) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        //use application context to get contentResolver
        val contentResolver = requireContext().contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageUri?.let { resolver.openOutputStream(it) })
        }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)
        val fileUri =
            ContentUriUtil.getFilePath(requireContext(), imageUri!!).toString()
        activity?.apply {
            if (this is PictureActivity) {
                val valueMap = hashMapOf(
                    "bodyPart" to part,
                    "image" to fileUri
                )
                saveComplete(valueMap)
            }
        }
    }

    private fun saveTheImageLegacyStyle(bitmap: Bitmap) {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(
            imagesDir, SimpleDateFormat(CameraActivity.FILENAME, Locale.US)
                .format(System.currentTimeMillis()) + CameraActivity.PHOTO_EXTENSION
        )
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(image))
        requireContext().sendBroadcast(Intent(Camera.ACTION_NEW_PICTURE, image.toUri()))
        val newExif = ExifInterface(image)
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, "1")
        newExif.saveAttributes()
        activity?.apply {
            if (this is PictureActivity) {
                val valueMap = hashMapOf(
                    "bodyPart" to part,
                    "image" to image.toString()
                )
                saveComplete(valueMap)
            }
        }
    }

    private fun settingNumberPickerEvent() {
        val time = binding.includePictureTime
        val oldTimeFormat = SimpleDateFormat("kk:mm")
        val timeFormat = SimpleDateFormat("a hh:mm", Locale("en", "KR"))
        time.npYear.setOnValueChangedListener { picker, _, newVal ->
            val dateText = binding.twDate.text.split(".")
            binding.twDate.text =
                "${picker.displayedValues[newVal - 1]}.${dateText[1]}.${dateText[2]}"
        }
        time.npMonth.setOnValueChangedListener { _, _, newVal ->
            val dateText = binding.twDate.text.split(".")
            binding.twDate.text = "${dateText[0]}.${newVal}.${dateText[2]}"
        }
        time.npDay.setOnValueChangedListener { _, _, newVal ->
            val dateText = binding.twDate.text.split(".")
            val timeText = binding.twDate.text.split(" ")
            binding.twDate.text = "${dateText[0]}.${dateText[1]}.${newVal} ${timeText[1]} ${timeText[2]}"
        }
        time.npHour.setOnValueChangedListener { picker, _, newVal ->
            val old =
                oldTimeFormat.parse("$newVal:${time.npMin.value}")
            val dateText = binding.twDate.text.split(" ")[0].split(".")
            binding.twDate.text = "${dateText[0]}.${dateText[1]}.${dateText[2]} ${timeFormat.format(old)}"
        }
        time.npMin.setOnValueChangedListener { picker, _, newVal ->
            val old =
                oldTimeFormat.parse("${time.npHour.value}:$newVal")
            val dateText = binding.twDate.text.split(" ")[0].split(".")
            binding.twDate.text = "${dateText[0]}.${dateText[1]}.${dateText[2]} ${timeFormat.format(old)}"
        }
    }

    private fun settingWeightEvent() {
        val weight = binding.includePictureWeight
        weight.npFirst.setOnValueChangedListener { _, _, newVal ->
            val value = binding.twWeight.text.toString().split(".")[1].replace("kg", "")
            binding.twWeight.text = "${newVal}.${value}kg"
        }
        weight.npLast.setOnValueChangedListener { _, _, newVal ->
            val value = binding.twWeight.text.toString().split(".")[0]
            binding.twWeight.text = "${value}.${newVal}kg"
        }
    }

    private fun settingWeightValue() {
        val weight = binding.includePictureWeight
        val weightValue = localStorage.getWeight().split(".")
        weight.npFirst.minValue = 0
        weight.npFirst.maxValue = 150
        weight.npLast.minValue = 1
        weight.npLast.maxValue = 9
        weight.npFirst.value = weightValue[0].toIntDefault()
        weight.npLast.value = weightValue[1].toIntDefault()
        binding.includePictureWeight.ibWeightCheck.isSelected = localStorage.isWeightVisible()
        binding.twWeight.isVisible = localStorage.isWeightVisible()
        binding.twWeight.text = localStorage.getWeight() + "kg"
    }

    private fun settingNumberPickerValue() {
        val time = binding.includePictureTime
        val dd = Date()
        val yearArray = Array(10) { i -> dd.year - i }.map { it.toString() }.toTypedArray()
        time.npYear.minValue = 1
        time.npYear.maxValue = yearArray.size
        time.npYear.value = 1
        time.npYear.displayedValues = yearArray
        time.npMonth.minValue = 1
        time.npMonth.maxValue = DateData.month.size
        time.npMonth.displayedValues = DateData.month
        time.npDay.minValue = 1
        time.npDay.maxValue = 31
        time.npDay.value = dd.day
        time.npHour.minValue = 1
        time.npHour.maxValue = 24
        time.npHour.value = dd.hours
        time.npMin.minValue = 0
        time.npMin.maxValue = 59
        time.npMin.value = dd.minutes
    }
}