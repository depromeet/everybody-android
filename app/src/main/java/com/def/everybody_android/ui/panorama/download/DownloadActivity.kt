package com.def.everybody_android.ui.panorama.download

import android.content.Context
import android.graphics.Point
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager.widget.ViewPager
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityDownloadBinding
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.ui.dialog.message.MessageDialog
import com.def.everybody_android.ui.panorama.PanoramaViewPagerAdapter
import com.def.everybody_android.ui.panorama.download.loading.DownloadDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import okhttp3.ResponseBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DownloadActivity : BaseActivity<ActivityDownloadBinding, DownloadViewModel>() {
    override val layoutId: Int = R.layout.activity_download
    override val viewModel: DownloadViewModel by viewModels()
    private lateinit var adapter: RecyclerViewAdapter
    private val imageList = mutableListOf<MainFeedPictureData>()
    private val downloadDialog = DownloadDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra("image")) return finish()
        imageList.addAll(intent.getParcelableArrayExtra("image")?.map { it as MainFeedPictureData }.orEmpty())
        if (imageList.isEmpty()) return finish()
        binding.twTitle.text = intent.getStringExtra("title")
        adapter = RecyclerViewAdapter { }
        binding.recyclerPanorama.adapter = adapter
        adapter.setItems(imageList.mapIndexed { index, MainFeedPictureData ->
            RecyclerItem(
                Item(
                    MainFeedPictureData.imagePath,
                    R.drawable.test_feed,
                    (index + 1).toString(),
                    index == 0,
                    viewModel
                ), R.layout.item_download_tab, BR.data
            )
        })
        settingPanorama()
        binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(imageList) }
        binding.vpPanorama.offscreenPageLimit = imageList.size
    }

    private val screenWidth: Int
        get() {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }

    private fun settingPanorama() {
        var currentPosition = RecyclerView.NO_POSITION
        binding.recyclerPanorama.adapter = adapter
        binding.recyclerPanorama.addItemDecoration(OffsetItemDecoration(this))
        binding.recyclerPanorama.itemAnimator?.apply {
            (this as SimpleItemAnimator).supportsChangeAnimations = false
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerPanorama)
        val vpListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                (binding.recyclerPanorama.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    position,
                    (screenWidth / 2) + convertDpToPx(12)
                )
                if (currentPosition != position) {
                    currentPosition = position
                    val index =
                        adapter.getItems().indexOfFirst { (it.data as Item).isEnable }
                    if (index > -1) {
                        val item = adapter.getItems()[index]
                        adapter.changeItem(
                            item.copy(
                                data = (item.data as Item).copy(
                                    isEnable = false
                                )
                            ), index
                        )
                    }
                    val item = adapter.getItems()[position]
                    adapter.changeItem(
                        item.copy(
                            data = (item.data as Item).copy(
                                isEnable = true
                            )
                        ), position
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

        }
        val recyclerListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.layoutManager != null) {
                    val view = snapHelper.findSnapView(recyclerView.layoutManager) ?: return
                    val position = recyclerView.layoutManager!!.getPosition(view)
                    if (currentPosition != position) {
                        binding.vpPanorama.removeOnPageChangeListener(vpListener)
                        currentPosition = position
                        val index =
                            adapter.getItems().indexOfFirst { (it.data as Item).isEnable }
                        if (index > -1) {
                            val item = adapter.getItems()[index]
                            recyclerView.post {
                                adapter.changeItem(
                                    item.copy(
                                        data = (item.data as Item).copy(
                                            isEnable = false
                                        )
                                    ), index
                                )
                            }
                        }
                        val item = adapter.getItems()[position]
                        recyclerView.post {
                            adapter.changeItem(
                                item.copy(
                                    data = (item.data as Item).copy(
                                        isEnable = true
                                    )
                                ), position
                            )
                        }
                        binding.vpPanorama.setCurrentItem(position, false)
                        binding.vpPanorama.addOnPageChangeListener(vpListener)
                    }
                }
            }
        }
        binding.recyclerPanorama.addOnScrollListener(recyclerListener)
        binding.vpPanorama.addOnPageChangeListener(vpListener)
    }

    override fun init() {
        super.init()
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    is DownloadViewModel.Event.Delete -> {
                        if (adapter.itemCount <= 2) return@collect topToast("사진이 최소 2장이상 필요해요.")
                        val recyclerList = adapter.getItems().filter { item -> item.data != it.item }.mapIndexed { index, recyclerItem ->
                            recyclerItem.copy(data = (recyclerItem.data as Item).copy(isEnable = index == 0, count = (index + 1).toString()))
                        }
                        adapter.setItems(recyclerList)
                        val list = imageList.filter { image ->
                            adapter.getItems().find { (it.data as Item).imageUrl == image.imagePath } != null
                        }
                        binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(list) }
                        binding.vpPanorama.offscreenPageLimit = list.size
                        binding.vpPanorama.setCurrentItem(0, false)
                        (binding.recyclerPanorama.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            0,
                            (screenWidth / 2) + convertDpToPx(12)
                        )
                        binding.imgRefresh.isInvisible = false
                    }
                    DownloadViewModel.Event.Close -> finish()
                    DownloadViewModel.Event.Download -> {
                        downloadDialog.show(supportFragmentManager, "")
                        viewModel.onDownloadClick(adapter.getItems().map { (it.data as Item).imageUrl })
                    }
                    is DownloadViewModel.Event.DownloadFile -> {
                        runOnUiThread {
                            writeResponseBodyToDisk(it.body)
                        }
                    }
                    DownloadViewModel.Event.Refresh -> {
                        MessageDialog(true) {
                            adapter.setItems(imageList.mapIndexed { index, MainFeedPictureData ->
                                RecyclerItem(
                                    Item(
                                        MainFeedPictureData.imagePath,
                                        R.drawable.test_feed,
                                        (index + 1).toString(),
                                        index == 0,
                                        viewModel
                                    ), R.layout.item_download_tab, BR.data
                                )
                            })
                            binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(imageList) }
                            binding.vpPanorama.offscreenPageLimit = imageList.size
                            binding.vpPanorama.setCurrentItem(0, false)
                            binding.imgRefresh.isInvisible = true
                            (binding.recyclerPanorama.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                                0,
                                (screenWidth / 2) + convertDpToPx(12)
                            )
                        }.setMessage("삭제한 사진을 모두 복구하시겠어요?", "확인을 누르면 기존에 편집한 모든 사진이 복구됩니다.").show(supportFragmentManager, "")

                    }
                }
            }
        }
    }

    data class Item(
        val imageUrl: String,
        @DrawableRes val holder: Int,
        val count: String,
        val isEnable: Boolean,
        val viewModel: DownloadViewModel
    )

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        val futureStudioIconFile =
            File(getOutputDirectory(), SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".mp4")
        return try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.e("asdas", "${((fileSizeDownloaded.toFloat() / fileSize) * 100f).toInt()}")
                    downloadDialog.onDownload(((fileSizeDownloaded.toFloat() / fileSize) * 100f).toInt())
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
                MediaScannerConnection.scanFile(
                    applicationContext, arrayOf(futureStudioIconFile.path),  // 추가할 파일의 경로
                    null
                )  // Mime type
                { _, _ -> runOnUiThread { downloadDialog.onComplete() } }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}