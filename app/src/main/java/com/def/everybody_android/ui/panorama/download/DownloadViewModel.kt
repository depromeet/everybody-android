package com.def.everybody_android.ui.panorama.download

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DownloadViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onDeleteClick(item: DownloadActivity.Item) {
        viewModelScope.launch { _event.emit(Event.Delete(item)) }
    }

    fun onDownloadClick(filesDir: String, keys: List<String>) {
        val query = combineImagesAndVideos(keys, filesDir)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                Log.e("asdasd",logMessage.text)
            }

            override fun success() {
                viewModelScope.launch { _event.emit(Event.DownloadFile(filesDir)) }
            }

            override fun cancel() {
                viewModelScope.launch { _event.emit(Event.DownloadCancel) }
            }

            override fun failed() {
                viewModelScope.launch { _event.emit(Event.DownloadCancel) }
            }
        })
    }

    private fun combineImagesAndVideos(paths: List<String>, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        paths.forEach {
            inputs.add("-loop")
            inputs.add("1")
            inputs.add("-framerate")
            inputs.add("60")
            inputs.add("-t")
            inputs.add("1")
            inputs.add("-i")
            inputs.add(it)
        }

        var query: String? = ""
        var queryAudio: String? = ""
        for (i in paths.indices) {
            query = query?.trim()
            query += "[" + i + ":v]scale=${1080}x${1200},setdar=${1080}/${1200}[" + i + "v];"

            queryAudio = queryAudio?.trim()
            queryAudio += "[" + i + "v][" + paths.size + ":a]"
        }
        return getResult(inputs, query, queryAudio, paths, output)
    }

    private fun getResult(
        inputs: java.util.ArrayList<String>,
        query: String?,
        queryAudio: String?,
        paths: List<String>,
        output: String
    ): Array<String> {
        inputs.apply {
            add("-f")
            add("lavfi")
            add("-t")
            add("0.1")
            add("-i")
            add("anullsrc")
            add("-filter_complex")
            add(query + queryAudio + "concat=n=" + paths.size + ":v=1:a=1 [v][a]")
            add("-map")
            add("[v]")
            add("-map")
            add("[a]")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    sealed class Event {
        object Close : Event()
        object Download : Event()
        object Refresh : Event()
        data class DownloadFile(val path:String) : Event()
        object DownloadCancel : Event()
        data class Delete(val item: DownloadActivity.Item) : Event()
    }

}