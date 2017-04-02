package com.leaf.magicnetwork;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.leaf.magic.Magic;
import com.leaf.magic.image.listener.LoadListener;
import com.leaf.magic.request.Request;
import com.leaf.magic.request.Response;
import com.leaf.magic.request.factory.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    String url = "http://images.all-free-download.com/images/graphiclarge/beautiful_natural_scenery_01_hd_picture_166232.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button imageBnt = (Button) findViewById(R.id.image);
        Button networkBnt = (Button) findViewById(R.id.network);
        final ImageView mView = (ImageView) findViewById(R.id.mview);
        imageBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Magic.with(MainActivity.this).loadImage(url).addListener(new LoadListener() {
                    @Override
                    public void onLoadStarted(String url) {
                        Log.d(TAG, "onLoadStarted");
                    }

                    @Override
                    public void onLoadSucessed(Bitmap bmp, String url) {
                        Log.d(TAG, "onLoadSucessed");
                    }

                    @Override
                    public void onLoadFailed(String error) {
                        Log.d(TAG, "onLoadFailed");
                    }
                }).into(mView);
            }
        });
        networkBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginRequest();
            }
        });

    }

    private void beginRequest() {
        String url = "url...";
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject var1) {
                if (var1 != null) {
                    Log.d(TAG, "response:" + var1.toString());
                } else {
                    Log.d(TAG, "response is null");
                }
            }
        }, new Response.ErrorResponse<String>() {
            @Override
            public void onErrorResponse(String var1) {
                Log.d(TAG, "onErrorResponse is " + var1);
            }
        }) {
            @Override
            protected Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("key","value");
                return map;
            }
        };
        Magic.with(MainActivity.this).addRequest(mRequest);
    }
}
