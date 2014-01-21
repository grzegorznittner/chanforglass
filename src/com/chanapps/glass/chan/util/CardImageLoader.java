package com.chanapps.glass.chan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CardImageLoader {
    
    private static final String TAG = CardImageLoader.class.getSimpleName(); 
    private static final boolean DEBUG = false;
    
    private static final int MAX_IMAGE_LOAD_RETRIES = 3;
    private static Map<String, Integer> mLoadFailures = new ConcurrentHashMap<String, Integer>();

    public static void init(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static void loadCardImage(final Card card, Card.ImageLayout imageLayout, final String imageUri,
                                     final CardScrollView cardScrollView) {
        card.setImageLayout(imageLayout);
        int failures = mLoadFailures.containsKey(imageUri) ? mLoadFailures.get(imageUri) : 0;
        if (failures > MAX_IMAGE_LOAD_RETRIES) {
            if (DEBUG) Log.i(TAG, "Exceeded max retries on imageUri=" + imageUri);
            return;
        }
        File file = ImageLoader.getInstance().getDiscCache().get(imageUri);
        if (file != null && file.exists() && file.length() > 0) {
            Uri uri = Uri.fromFile(file);
            card.addImage(uri);
        }
        else {
            ImageLoader.getInstance().loadImage(imageUri, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if (DEBUG) Log.i(TAG, "onLoadingStarted");
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if (DEBUG) Log.i(TAG, "onLoadingFailed");
                    int failures = mLoadFailures.containsKey(imageUri) ? mLoadFailures.get(imageUri) : 0;
                    mLoadFailures.put(imageUri, ++failures);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    File file = ImageLoader.getInstance().getDiscCache().get(imageUri);
                    if (DEBUG) Log.i(TAG, "onLoadingComplete uri=" + imageUri + " file=" + file
                            + " len=" + (file == null ? 0 : file.length()));
                    if (file != null && file.exists() && file.length() > 0) {
                        if (DEBUG) Log.i(TAG, "onLoadingComplete scheduling update of scroll views");
                        if (cardScrollView != null)
                            cardScrollView.updateViews(true);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if (DEBUG) Log.i(TAG, "onLoadingCancelled");
                }
            });
        }
    }
}
