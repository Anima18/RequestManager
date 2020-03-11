package com.anima.networkrequest.data.okhttp

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.internal.closeQuietly
import okio.BufferedSink
import okio.Source
import okio.source
import java.io.File

/**
 * Created by jianjianhong on 20-3-11
 */
class CountingFileRequestBody( var file: File, var contentType: String, var progressListener: ProgressListener) : RequestBody(){
    private val SEGMENT_SIZE = 1024 * 256 // okio.Segment.SIZE

    override fun contentLength(): Long {
        return file.length()
    }

    override fun contentType(): MediaType? {
        return contentType.toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        var source: Source? = null
        try {
            source = file.source()
            var segmentReaded = 0L

            var read = source!!.read(sink.buffer(), SEGMENT_SIZE.toLong())
            while (read != -1L) {
                segmentReaded += read
                if (segmentReaded >= SEGMENT_SIZE) {
                    sink.flush()
                    progressListener.onUpdate(segmentReaded, file.name)
                    segmentReaded = 0L
                }
                read = source!!.read(sink.buffer(), SEGMENT_SIZE.toLong())
            }
            if (segmentReaded != 0L) {
                sink.flush()
                progressListener.onUpdate(segmentReaded, file.name)
                segmentReaded = 0L
            }


        } finally {
            source!!.closeQuietly()
        }
    }

    interface ProgressListener {
        fun onUpdate(bytesRead: Long, fileName: String)
    }
}