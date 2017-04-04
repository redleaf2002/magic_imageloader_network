# magic_network

This Android library Magic_network on GitHub.  aims to provide a flexible instrument for loading image and network request.
For loading image ,it supports http, drawable, assets,video thumbnail and local file.It also supports to customize the mode of loading image.
For network request , it provides JsonObjectRequest JsonArrayRequest StringRequest InputstreamRequest.

## Features
Image cache in memory and disk

## Usage:
   
   ### 1.for Imageloader
    ImageType:  HTTP FILE DRAWABLE ASSETS VIDEO
  
    #### HTTP
    String url = "http://images.all-free-download.com/images/graphiclarge/beautiful_natural_scenery_01_hd_picture_166232.jpg";
    Magic.with(MainActivity.this).loadImage(url, ImageType.HTTP).into(mImageView);
    
    #### FILE:
    String filePath = "/storage/emulated/0/gnowwp/resized/london_night.png";
    Magic.with(MainActivity.this).loadImage(filePath, ImageType.FILE).into(mImageView);

    #### DRAWABLE:
    Magic.with(MainActivity.this).loadImage(R.drawable.beautiful_natural).into(mImageView);

    #### ASSETS:
    Magic.with(MainActivity.this).loadImage("beautiful_natural.jpg", ImageType.ASSETS).into(mImageView);
   
    #### VIDEO thumbnail:
    have 3 mode:     MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
                     MediaStore.Images.Thumbnails.MICRO_KIND
                     MediaStore.Images.Thumbnails.MINI_KIND
    
    String videoUrl = "/storage/emulated/0/DCIM/Camera/VID_20160711_113933.mp4";
    Magic.with(MainActivity.this).loadImage(videoUrl,ImageType.VIDEO).addExtra(MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)     .into(mImageVie
 

   #### With the listener
   onLoadListener
   private void load(){
       Magic.with(mContext).loadImage(url).addListener(new OnLoadListener() {
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
     
     #### customize a load mode:
     Firstly instantiate the DownloadStream
  
     private void customizeLoader(){
        Magic.with(MainActivity.this).addStreamType("new_type", new DownloadStream() {
         @Override
        public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
           try {
               return new FileInputStream(new File(imageDownloadInfo.getImageUrl()));
           } catch (FileNotFoundException e) {
           }
           return null;
          }
        });

        //And then:
        String newPath = "/storage/emulated/0/gnowwp/resized/london_night.png";
        Magic.with(MainActivity.this).loadImage(newPath, "new_type").into(mImageView);
     }   
 
    ### AsyncImage loading
    very easy :
     ```Java 
     private void asyncLoad(){
       String url ="http://images.allfreedownload.com/images/graphiclarge/beautiful_natural_scenery_01_hd_picture_166232.jpg";
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
                }).into();
     //or   .into(null);
     }
    ```
     
   ### 2.for network request 
   with JsonobjectRequest JsonArrayRequest StringRequest FileRequest
    
    #### get:
    ```java
    private void beginRequest() {
        String url = "url...";
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, url, null, 
        new Response.Listener<JSONObject>()                     {
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
    ```
    ### Post:
    
    private void beginPostRequest() {
      String requestBody = "requestbody";
      JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new   Response.Listener<JSONObject>() {
        public void onResponse(JSONObject object) {
            if (object != null) {
                Log.d(TAG, "response:" + object.toString());
            } else {
                Log.d(TAG, "response is null");
            }
        }
    }, new Response.ErrorResponse<String>() {
        @Override
        public void onErrorResponse(String s) {
            Log.d(TAG, "onErrorResponse is " + s);
        }
    }) {
        @Override
        protected Map<String, String> getHeaders() {
            Map<String, String> map = new HashMap<>();
            map.put("info", "1|zh|CN|19|");
            return map;
        }
    };
    Magic.with(MainActivity.this).addRequest(mRequest);
    }
