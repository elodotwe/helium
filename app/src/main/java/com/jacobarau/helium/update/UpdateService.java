package com.jacobarau.helium.update;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.ui.Notifications;
import com.jacobarau.helium.update.rss.ParseException;
import com.jacobarau.helium.update.rss.ParseResult;
import com.jacobarau.helium.update.rss.RssParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jacobarau.helium.ui.Notifications.ONGOING_NOTIFICATION_ID;

public class UpdateService extends Service {
    private final static String TAG = "UpdateService";
    private ExecutorService updateService = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startForeground(ONGOING_NOTIFICATION_ID, new Notifications().buildUpdateServiceNotification(this));

        onServiceOnCreate();
    }

    /**
     * Called only from the UpdateService when said service is created.
     *
     * Called on main thread, needs to return quickly.
     */
    public void onServiceOnCreate() {
        List<Subscription> subscriptions = HeliumApplication.wiring.podcastRepository.subscriptions.getClonedList();
        for (final Subscription subscription: subscriptions) {
            updateService.submit(new Runnable() {
                @Override
                public void run() {
                    ParseResult result = parseFromUrl(subscription.url);
                    if (result == null) {
                        //TODO: tell the user there's an error
                        Log.e(TAG, "Error updating subscription with URL " + subscription.url);

                        new Handler(getApplicationContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "failed to update " + subscription.url, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });

                        return;
                    }

                    subscription.title = result.subscription.title;
                    subscription.link = result.subscription.link;
                    subscription.imageUrl = result.subscription.imageUrl;
                    subscription.description = result.subscription.description;
                    subscription.lastUpdated = new Date();

                    HeliumApplication.wiring.podcastRepository.save(subscription);

                    for (Item item: result.items) {
                        item.subscriptionId = subscription.id;
                    }
                    HeliumApplication.wiring.podcastRepository.save(result.items);
                }
            });
        }
    }

    // TODO: I don't like that this returns null, but I dislike exceptions even more at the moment.
    private ParseResult parseFromUrl(String url) {
        // Clean up any existing cache. Some crappy apps let their cache directory grow until the
        // user goes and cleans it up manually. Not us.
        // TODO: this makes us not threadsafe. Don't particularly care just now.
        for (File file : getApplicationContext().getCacheDir().listFiles()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }

        File destination;
        try {
            // Local temp file will be in the cache directory, named after the base64 conversion
            // of the given URL. I'd just use the URL, except URLs contain characters I'm sure
            // some file systems will not enjoy.

            // Suppressed because we can't actually use the suggested charset object because our
            // minimum API level is too low.

            //noinspection CharsetObjectCanBeUsed
            destination = new File(getApplicationContext().getCacheDir(),
                    Base64.encodeToString(url.getBytes("utf8"), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't encode URL to base64 oddly enough", e);
        }

        try {
            new Downloader().downloadFile(new URL(url), destination, new Downloader.ProgressListener() {
                @Override
                public void onProgress(long position, Long total) {
                    Log.i(TAG, "onProgress: position is " + position + ", total is " + total);
                    // TODO: download progress to the user?
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "getSubscriptionFromURL: IOException doing downloadFile()", e);
        }

        RssParser parser = new RssParser();
        ParseResult result;
        try {
            result = parser.parseRSS(new FileInputStream(destination), "utf-8", url);
        } catch (XmlPullParserException | IOException | ParseException e) {
            Log.e(TAG, "getSubscriptionFromURL: Error parsing downloaded file", e);
            //TODO how to manage cached file cleanup...some sort of RAII thing?
            return null;
        }
        // Clean up the downloaded RSS file as we'll never use it again. Don't care if the deletion fails.
        //noinspection ResultOfMethodCallIgnored
        destination.delete();
        Log.i(TAG, "run: result was " + result);
        return result;
    }

    public static void startUpdate(Context appContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(new Intent(appContext, UpdateService.class));
        } else {
            appContext.startService(new Intent(appContext, UpdateService.class));
        }
    }
}
