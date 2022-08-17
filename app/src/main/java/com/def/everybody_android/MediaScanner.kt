package com.def.everybody_android

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.util.Log


class MediaScanner(val context: Context) {

    private var mPath: String? = null

    private var mMediaScanner: MediaScannerConnection? = null
    private var mMediaScannerClient: MediaScannerConnectionClient? = null

    fun mediaScanning(path: String?) {
        if (mMediaScanner == null) {
            mMediaScannerClient = object : MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {
                    mMediaScanner!!.scanFile(mPath, null)
                }

                override fun onScanCompleted(path: String?, uri: Uri?) {
                    Log.d("Success : ", "MediaScan Complete!")
                    mMediaScanner!!.disconnect()
                }
            }
            mMediaScanner = MediaScannerConnection(context, mMediaScannerClient)
        }
        mPath = path
        mMediaScanner!!.connect()
    }
}