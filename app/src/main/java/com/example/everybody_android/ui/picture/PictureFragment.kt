package com.example.everybody_android.ui.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.ExifInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseFragment
import com.example.everybody_android.databinding.FragmentPictureBinding
import com.example.everybody_android.imageLoad
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.typeFace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PictureFragment(private val image: String) :
    BaseFragment<FragmentPictureBinding, PictureFragmentViewModel>() {
    override val viewModel: PictureFragmentViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_picture
    private val datePictureFormat = SimpleDateFormat("yyyy.MM.dd a hh:mm", Locale("en", "US"))
    private val timeFormat = SimpleDateFormat("a hh:mm", Locale("en", "US"))
    private val dateTextFormat = SimpleDateFormat("yyyy MMM dd kk:mm", Locale("en", "US"))


    private fun initSetting() {
        setPictureTime()
        viewModel.onClickEvent(PictureFragmentViewModel.ClickEvent.PartTab)
        viewModel.onClickEvent(PictureFragmentViewModel.ClickEvent.PartWhole)
        viewModel.onClickEvent(PictureFragmentViewModel.ClickEvent.TimePicture)
        binding.includePictureTime.npDay.typeface =
            requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npDay.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npYear.typeface =
            requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npYear.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npMonth.typeface =
            requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npMonth.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npMin.typeface =
            requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npMin.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        binding.includePictureTime.npHour.typeface =
            requireContext().typeFace(R.font.pretendard_regular)
        binding.includePictureTime.npHour.setSelectedTypeface(requireContext().typeFace(R.font.pretendard_semibold))
        settingNumberPickerValue()
        settingNumberPickerEvent()
    }

    private fun setPictureTime() {
        val date = datePictureFormat.format(Date(File(image).lastModified())).split(" ")
        val infoText = dateTextFormat.format(System.currentTimeMillis()).split(" ")
        binding.twDate.text = date[0]
        binding.twTime.text = "${date[1]} ${date[2]}"
        binding.includePictureTime.twYear.text = infoText[0]
        binding.includePictureTime.twMonth.text = infoText[1]
        binding.includePictureTime.twDay.text = infoText[2]
        binding.includePictureTime.twTime.text = infoText[3]
    }

    private fun setNowTime() {
        val date = datePictureFormat.format(System.currentTimeMillis()).split(" ")
        val infoText = dateTextFormat.format(System.currentTimeMillis()).split(" ")
        binding.twDate.text = date[0]
        binding.twTime.text = "${date[1]} ${date[2]}"
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
                    PictureFragmentViewModel.ClickEvent.PartTab, PictureFragmentViewModel.ClickEvent.TimeTab -> {
                        binding.twPartChoice.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.PartTab
                        binding.twTimeChoice.isSelected =
                            it == PictureFragmentViewModel.ClickEvent.TimeTab
                        binding.twPartChoice.typeface =
                            requireContext().typeFace(if (it == PictureFragmentViewModel.ClickEvent.PartTab) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.twTimeChoice.typeface =
                            requireContext().typeFace(if (it == PictureFragmentViewModel.ClickEvent.TimeTab) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.includePicturePart.root.isVisible =
                            it == PictureFragmentViewModel.ClickEvent.PartTab
                        binding.includePictureTime.root.isVisible =
                            it == PictureFragmentViewModel.ClickEvent.TimeTab
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(image))
        val newExif = ExifInterface(image)
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, "1")
        newExif.saveAttributes()
        activity?.apply {
            if (this is PictureActivity) {
                saveComplete()
            }
        }
    }

    private fun settingNumberPickerEvent() {
        val time = binding.includePictureTime
        val oldTimeFormat = SimpleDateFormat("kk:mm")
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
            binding.twDate.text = "${dateText[0]}.${dateText[1]}.${newVal}"
        }
        time.npHour.setOnValueChangedListener { picker, _, newVal ->
            val old =
                oldTimeFormat.parse("$newVal:${time.npMin.value}")
            binding.twTime.text = timeFormat.format(old)
        }
        time.npMin.setOnValueChangedListener { picker, _, newVal ->
            val old =
                oldTimeFormat.parse("${time.npHour.value}:$newVal")
            binding.twTime.text = timeFormat.format(old)
        }
    }

    private fun settingNumberPickerValue() {
        val time = binding.includePictureTime
        val year = SimpleDateFormat("yyyy").format(System.currentTimeMillis()).toInt()
        val yearArray = Array(10) { i -> year - i }.map { it.toString() }.toTypedArray()
        time.npYear.minValue = 1
        time.npYear.maxValue = yearArray.size
        time.npYear.value = 1
        time.npYear.displayedValues = yearArray
        time.npMonth.minValue = 1
        time.npMonth.maxValue = DateData.month.size
        time.npMonth.displayedValues = DateData.month
        time.npDay.minValue = 1
        time.npDay.maxValue = 31
        time.npHour.minValue = 1
        time.npHour.maxValue = 24
        time.npMin.minValue = 0
        time.npMin.maxValue = 59
        time.npMin.value = 0
    }
}