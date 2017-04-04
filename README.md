# magic_network

This Android library Magic_network on GitHub.  aims to provide a flexible instrument for loading image and network request.
For loading image ,it supports http, drawable, assets,video thumbnail and local file.It also supports to customize the mode of loading image.
For network request , it provides JsonObjectRequest JsonArrayRequest StringRequest InputstreamRequest.


Simple
   
   1.for image laoder
   
     Magic.with(mContext).loadImage(url).into(mView);

   with the listener
     Magic.with(mContext).loadImage(url).addListener(new LoadListener() {
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
                
                
    2.for network request with JsonobjectRequest JsonArrayRequest StringRequest FileRequest
    
       private void beginRequest() {
        String url = "url...";
       JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()               {
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
    
    
    
    
    
    
    
    
    
    
    
    
    
