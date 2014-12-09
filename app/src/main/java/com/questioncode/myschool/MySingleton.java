package com.questioncode.myschool;


/**
 * Created by alain on 03/12/14.
 * Source can be find here  gist: https://gist.github.com/alain75007/64fcd7b01bef3126d316e
 *
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;



/**
 * This class is a singleton class which initializes core objects of Android Volley library
 */
public class MySingleton {
    private static final String TAG = "XXXXXXXX";
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private  ImageLoader mImageLoader;
    private static Context mCtx;

    public DefaultHttpClient getmHttpClient() {
        return mHttpClient;
    }

    private final DefaultHttpClient mHttpClient = new DefaultHttpClient();

    //private MyCookieStore mCookieStore = new MyCookieStore();

    private MySingleton(Context context) {
        mCtx = context;
        //mHttpClient.setCookieStore((CookieStore) mCookieStore);


        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            // getApplicadtionContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HttpClientStack(mHttpClient));
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        setCookie();
        getRequestQueue().add(req);
    }


    public <T> void addToRequestQueue(Request<T> req) {
        setCookie();
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Method to set a cookie
     */
    public void setCookie() {
        CookieStore cs = mHttpClient.getCookieStore();
        // create a cookie
        Log.d(TAG, "setCookie");
        for (Cookie cookie : cs.getCookies()) {
            Log.d(TAG,"Found cookie " +  cookie.toString());
        };
        cs.addCookie(new BasicClientCookie2("cookie", "spooky"));
    }


}