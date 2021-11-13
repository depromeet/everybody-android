package com.example.everybody_android

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

fun ImageView.imageLoad(
    file: String,
    requestOptions: RequestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
) {
    Glide.with(context).load(file).apply(requestOptions).into(this)
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}


fun Context.convertDpToPx(dp: Int): Int {
    return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

@BindingAdapter("imageUrl", "placeholder")
fun folderLoadImage(view: ImageView, url: String, placeholder: Drawable) {
    if (url.isEmpty()) view.setImageDrawable(placeholder)
    else view.imageLoad(
        url,
        RequestOptions().transform(CenterCrop(), RoundedCorners(view.context.convertDpToPx(4)))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    )
}