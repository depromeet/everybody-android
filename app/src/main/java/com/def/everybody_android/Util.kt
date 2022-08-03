package com.def.everybody_android

import android.content.Context
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.def.everybody_android.databinding.ViewTopToastBinding
import com.def.everybody_android.db.Album
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.dto.Feed
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.math.roundToInt


fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, this.resources.getText(resId), duration).show()
}

fun Context.typeFace(@FontRes id: Int): Typeface? {
    return ResourcesCompat.getFont(this, id)
}

fun Context.getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}

fun ImageView.imageLoad(
    file: String,
    requestOptions: RequestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
) {
    Glide.with(context).load(file).apply(requestOptions).into(this)
}

fun ImageView.imageLoad(
    file: File,
    requestOptions: RequestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
) {
    Glide.with(context).load(file).apply(requestOptions).into(this)
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

fun getFilePath(context: Context) : String {
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "눈바디")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val extension = ".mp4"
    val dest = File(dir.path + File.separator + System.currentTimeMillis().div(1000L) + extension)
    return dest.absolutePath
}

fun galleryScan(context: Context,path: String) {
    val file = File(path)
    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
}

fun Context.toast(@DrawableRes resId: Int) {
    val image = ImageView(applicationContext)
    image.setImageResource(resId)
    val toast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG)
    toast.setGravity(Gravity.TOP, Gravity.CENTER, Gravity.TOP)
    toast.view = image
    toast.show()
}


fun Context.convertDpToPx(dp: Int): Int {
    return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Context.topToast(message: String) {
    val view = ViewTopToastBinding.inflate(LayoutInflater.from(this), null, false)
    view.setVariable(BR.content, message)
    val toast = Toast(applicationContext)
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
    toast.view = view.root
    toast.show()
}

fun List<Album>.toFeeds(): List<Feed> {
    return this.map { it.toFeed() }
}

fun Album.toFeed(): Feed {
    val diffDays = (System.currentTimeMillis() - this.feedCreated.time) / (1000 * 24 * 60 * 60)
    return Feed(
        this.id,
        this.name,
        this.feedCreated,
        "${diffDays}일간의 기록",
        if (this.feedPictureDataList.isEmpty()) "" else this.feedPictureDataList.last()?.imagePath ?: "",
        R.drawable.test_feed,
        this.feedPictureDataList
    )
}

fun Realm.nextAlbumId(): Int {
    val currentIdNum = where(Album::class.java).max("id")
    return if (currentIdNum == null) 1
    else currentIdNum.toInt() + 1
}

fun Realm.nextPictureId(): Int {
    val currentIdNum = where(MainFeedPictureData::class.java).max("id")
    return if (currentIdNum == null) 1
    else currentIdNum.toInt() + 1
}

fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

@BindingAdapter("imageUrl", "placeholder")
fun folderLoadImage(view: ImageView, url: String, @DrawableRes placeholder: Int) {
    if (url.isEmpty()) view.setImageResource(placeholder)
    else view.imageLoad(
        File(url),
        RequestOptions().transform(CenterCrop(), RoundedCorners(view.context.convertDpToPx(4)))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    )
}

@BindingAdapter("imagePanoramaUrl", "placeholderPanorama")
fun panoramaLoadImage(view: ImageView, url: String, @DrawableRes placeholder: Int) {
    if (url.isEmpty()) view.setImageResource(placeholder)
    else view.imageLoad(
        File(url),
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    )
}

@BindingAdapter("selected")
fun setSelected(view: ImageView, isSelected: Boolean) {
    view.isSelected = isSelected
}

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

fun prepareFilePart(key: String, fileUri: Uri): MultipartBody.Part {
    val uri = if (!fileUri.toString().contains("file")) "file://$fileUri".toUri()
    else fileUri
    val file = uri.toFile()
    val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
    return MultipartBody.Part.createFormData(key, file.name, requestFile)
}
