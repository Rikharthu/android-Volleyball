package com.example.android.volleyball;

import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String TAG=MainActivity.class.getSimpleName();

    @BindView(R.id.output)
    TextView mOutputTextView;
    @BindView(R.id.activity_main)
    LinearLayout mRootView;
    @BindView(R.id.downloadButton)
    Button mDownloadButton;
    @BindView(R.id.downloadImgButton)
    Button mDownloadImgButton;
    @BindView(R.id.spinner)
    Spinner mUrlSpinner;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.networkImageView)
    NetworkImageView mNetworkImageView;
    ImageLoader mImageLoader;

    // Requests are queued up here to be executed
    private RequestQueue mRequestQueue;
    public static final String URL="http://echo.jsontest.com/name/Vasja/surname/Pukpin/skill_level/100500";
    /*  {
           "name": "Vasja",
           "surname": "Pukpin"
        }  */
    private static final String REQUEST_VASJA_PUPKIN="VASJA_PUPKIN";
    public static final String IMAGE_URL="http://images.genius.com/2c9cbdbe9db317402a551462934980db.444x444x1.jpg";
    boolean isInternetAllowed=true;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        View view = getLayoutInflater().inflate(R.layout.activity_main,null);
//        mOutputTextView= ButterKnife.findById(getLayoutInflater().inflate(view,R.id.output);
        ButterKnife.bind(this);

        mOutputTextView.setText(R.string.doc);
        List<String> spinnerTitles=new ArrayList<String>();
        spinnerTitles.add("Phosphor");
        spinnerTitles.add("Item 2");
        spinnerTitles.add("Item 3");
        spinnerTitles.add("Item 4");
        spinnerTitles.add("Item 5");
        ArrayAdapter<String> adp1=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,spinnerTitles);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUrlSpinner.setAdapter(adp1);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadJSON();
            }
        });
        mDownloadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = (String) mUrlSpinner.getSelectedItem();
                // TODO use this
                mUrlSpinner.getSelectedItemPosition();

                Log.d(TAG,"image url: "+url);
                mOutputTextView.setText("");
//                downloadImage();
                // You can also use ImageLoader to download and set image
                // Get the ImageLoader through your singleton class.
                mImageLoader = MySingleton.getInstance(MainActivity.this).getImageLoader();
                // check if this image is available in the cache
                boolean isCached=mImageLoader.isCached(IMAGE_URL,0,0);
                if(isCached){
                    Toast.makeText(MainActivity.this, "Image was retrieved from the cache", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Image was downloaded from the internet", Toast.LENGTH_SHORT).show();
                }
                // TODO создать спиннер для выбора ссылок картинок для проверки кэша
                // You can use ImageLoader by itself to display an image:
                mImageLoader.get(IMAGE_URL, ImageLoader.getImageListener(mImageView,
                        android.R.drawable.alert_dark_frame, android.R.drawable.alert_dark_frame));
                // However, NetworkImageView can do this for you
                /* builds on ImageLoader and effectively replaces ImageView for situations
                 where your image is being fetched over the network via URL.
                 NetworkImageView also manages canceling pending requests
                 if the view is detached from the hierarchy. */
                mNetworkImageView.setImageUrl(IMAGE_URL, mImageLoader);

            }
        });

        mGson=new Gson();

        // Instantiate the RequestQueue.
        // simple way
        //mRequestQueue = Volley.newRequestQueue(this);
        // hard way
        // Instantiate the cache
        //Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        //Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        //mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        //mRequestQueue.start();

        // FIXME we have moved RequestQueue to MySingleton class
        mRequestQueue=MySingleton.getInstance(this).getRequestQueue();
    }

    private void downloadJSON(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Always runs on the main thread.
                        VolleyLog.d("Success; ");
                        Snackbar.make(mRootView,"Success!",Snackbar.LENGTH_SHORT);

                        // Operate on response data
                        mOutputTextView.setText("Response is:\n"+ response.toString());

                        // Parse response
                        Programmer programmer = mGson.fromJson(response.toString(),Programmer.class);
                        mOutputTextView.setText(mOutputTextView.getText()+"\n"+programmer.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mRootView,"That didn't work!",Snackbar.LENGTH_SHORT);
                //mOutputTextView.setText("That didn't work!");
                VolleyLog.e("Error; ",error.getMessage());
            }
        });
        // you can set a tag on the request
        stringRequest.setTag(REQUEST_VASJA_PUPKIN);
        Log.d(TAG,"sending request: "+REQUEST_VASJA_PUPKIN);
        // Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);
        // or, following SingleTon pattern:
        //MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        if(!isInternetAllowed){
            // cancel all request with passed TAG (by identity)
            mRequestQueue.cancelAll(REQUEST_VASJA_PUPKIN);
            // or
            //stringRequest.cancel();
            Log.d(TAG,"canceling request: "+REQUEST_VASJA_PUPKIN);
        }
    }

    /** Retrieves an image specified by the URL, displays it in the UI. */
    private void downloadImage(){
        //  request for getting an image at a given URL and calling back with a decoded bitmap
        ImageRequest request = new ImageRequest(IMAGE_URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mOutputTextView.setText(error.toString());
                        mImageView.setImageResource(android.R.drawable.alert_light_frame);
                    }
                });
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancel all requests
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

}
