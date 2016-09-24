package com.example.webserviceutil.OkHttp;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class CountingFileRequestBody extends RequestBody {

    private static final int SEGMENT_SIZE = 1024*256; // okio.Segment.SIZE

    private final File file;
    private final ProgressListener progressListener;
    private final String contentType;

    public CountingFileRequestBody(File file, String contentType, ProgressListener progressListener) {
        this.file = file;
        this.contentType = contentType;
        this.progressListener = progressListener;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long read;
            long segmentReaded = 0L;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                segmentReaded += read;
                if(segmentReaded >= SEGMENT_SIZE) {
                    sink.flush();
                    progressListener.onUpdate(segmentReaded, file.getName());
                    segmentReaded = 0L;
                }
            }
            if(segmentReaded != 0) {
                sink.flush();
                progressListener.onUpdate(segmentReaded, file.getName());
                segmentReaded = 0L;
            }


        } finally {
            Util.closeQuietly(source);
        }
    }

    interface ProgressListener {
        void onUpdate(long bytesRead, String fileName);
    }

}