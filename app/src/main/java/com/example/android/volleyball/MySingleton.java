package com.example.android.volleyball;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.Iterator;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        // a helper class that handles loading and caching images from remote URLs
        // provides an in-memory cache to sit in front of the normal Volley cache
//        mImageLoader = new ImageLoader(mRequestQueue,
//                // provide a custom in-memory cache
//                new ImageLoader.ImageCache() {
//
//                    private final LruCache<String, Bitmap>
//                            cache = new LruCache<String, Bitmap>(20);
//
//                    // Image will be downloaded once. Then it will be put to cache
//                    // and retrieved from it ratehr than downloading again
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        Log.d("ImageLoader Cache","getBitmap() for "+url);
//                        Log.d("ImageLoader Cache","cache size: "+cache.size());
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        Log.d("ImageLoader Cache","putBitmap()");
//                        Log.d("ImageLoader Cache","cache size: "+cache.size());
//                        cache.put(url, bitmap);
//                    }
//
//                });
        // TODO посмотреть как и сколько сохраняет
        // or use our LruBitmapCache
        mImageLoader= new ImageLoader(mRequestQueue,new LruBitmapCache(mCtx));
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 1024 * 1024); // 1MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);
            // Start the queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
