package com.jacobarau.helium.update;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
            connection = (HttpURLConnection) url.openConnection();

            // Starts the actual network activity. Can block. TODO: how to unblock? connection.disconnect()? Javadoc is vague...
            connection.connect();

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
            Log.e(TAG, "downloadFile: ", e);
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