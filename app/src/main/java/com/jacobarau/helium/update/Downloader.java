package com.jacobarau.helium.update;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * One might think this would be a duplication of Android's DownloadManager, and to an extent you'd
 * be right.
 *
 * But after reading StackOverflow posts about how you can't manually pause and resume that guy,
 * and how it does weird things like randomly appending "-1" to the filename you specify...I'm
 * just going to avoid it, at least for now.
 */
public class Downloader {
    private final String TAG = this.getClass().getName();

    private boolean isCancelled = false;

    public interface ProgressListener {
        /**
         * Called from the downloader's thread periodically when there's progress to report
         * @param position Number of bytes downloaded so far
         * @param total Total number of bytes to be downloaded, or null if the server didn't tell us
         *              how long the file will be. Probably possible for the server to lie about the
         *              length too, so don't crash if position > total.
         */
        void onProgress(long position, Long total);
    }

    public void downloadFile(URL url, File destination, ProgressListener progressListener) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            boolean redirecting;
            Map<URL, Integer> visited = new HashMap<>();
            do {
                Integer times = visited.get(url);
                if (times == null) times = 1;
                visited.put(url, times);

                if (times > 3)
                    throw new IOException("Stuck in redirect loop");

                connection = (HttpURLConnection) url.openConnection();

                // Starts the actual network activity. Can block. TODO: how to unblock? connection.disconnect()? Javadoc is vague...
                connection.connect();

                // Some podcasts immediately redirect to another URL.
                // HttpURLConnection totally should manage this...but by design it will only redirect
                // HTTP-to-HTTP, HTTPS-to-HTTPS, and never between the two. We aren't concerned with
                // security here, so we just handle the redirects universally.
                switch (connection.getResponseCode()) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        String location = connection.getHeaderField("Location");
                        location = URLDecoder.decode(location, "UTF-8");
                        url = new URL(url, location);  // Deal with relative URLs
                        redirecting = true;
                        break;
                    default:
                        redirecting = false;
                }
            } while (redirecting);

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String msg = "downloadFile: response code was not ok (actual " + connection.getResponseCode() + "; message was '" + connection.getResponseMessage() + "'";
                Log.e(TAG, msg);
                throw new IOException(msg);
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(destination);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled) {
                    input.close();
                    return;
                }
                total += count;
                // publishing the progress....
                progressListener.onProgress(total, fileLength == -1 ? null : (long) fileLength);
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            Log.e(TAG, "downloadFile failed, propagating this exception", e);
            throw e;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
    }

}