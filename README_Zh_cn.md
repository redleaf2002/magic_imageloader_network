# magic_network

这个框架提供了一个简单方便的图片加载的方式和网络请求。集合和扩展了universal-imageloader和volley两个框架

图片加载：支持网络 本地文件和视频缩略图 图片的形式：http drawable assets video_thumbnail
也支持自定义的加载图片的方法 同样支持内存缓存和文件缓存


网络请求：支持sonObjectRequest JsonArrayRequest StringRequest InputstreamRequest 使用比较简单方便

## 特性
1. 快速 方便
2. 整个框架比较小 (72kb jar)
3. 支持内存缓存和文件缓存
4. 自定义加载方式
5. 根据图片和imageview的大小决定bitmap加载的大小 有效的防止oom


## 加入在自己的项目中有下面三种方式

### 1.直接使用jar包magicnetwork.jar 
```java
把downloads目录下的jar包放入项目的libs下面
```

### 2.Gradle:
使用android studio开发的 直接在build.gradle中加入下面一行代码
```java
compile 'com.leaf:magicnetwork:1.0.1'
```

### 3.Maven
```java
<dependency>
  <groupId>com.leaf</groupId>
  <artifactId>magicnetwork</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

## 具体的使用:
   
### 1.对于图片加载

    支持的图片类型 HTTP FILE DRAWABLE ASSETS VIDEO
  
  #### 网络请求的 以http开始的url
    String url = "http://images.all-free-download.com/images/graphiclarge/beautiful_natural_scenery_01_hd_picture_166232.jpg";
    
    Magic.with(context).loadImage(url, ImageType.HTTP).into(mImageView);
    
  #### FILE 包括本地文件和通过相册选取的文件:
    String filePath = "/storage/emulated/0/gnowwp/resized/london_night.png";
    Magic.with(mContext).loadImage(filePath, ImageType.FILE).into(mImageView);

  #### DRAWABLE:
    Magic.with(mContext).loadImage(R.drawable.beautiful_natural).into(mImageView);

  #### ASSETS:
    Magic.with(mContext).loadImage("beautiful_natural.jpg", ImageType.ASSETS).into(mImageView);
   
  #### VIDEO缩略图:
    have 3 mode:     MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
                     MediaStore.Images.Thumbnails.MICRO_KIND
                     MediaStore.Images.Thumbnails.MINI_KIND
    
    String videoUrl = "/storage/emulated/0/DCIM/Camera/VID_20160711_113933.mp4";
    Magic.with(mContext).loadImage(videoUrl,ImageType.VIDEO).addExtra(MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)     .into(mImageVie
 

  #### 加载文件的监听
   onLoadListener
   ```Java
      private void load(Context mContext, ImageView imageView, String url) {
           Magic.with(mContext).loadImage(url,ImageType.HTTP).addListener(new OnLoadListener() {
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
           }).into(imageView);
      }
   ```
  #### 自定义加载的方式:
   首先实例化类DownloadStream 
   ```Java
      private void customizeLoader(){
          Magic.with(mContext).addStreamType("new_type", new DownloadStream() {
          @Override
             public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
           try {
               return new FileInputStream(new File(imageDownloadInfo.getImageUrl()));
           } catch (FileNotFoundException e) {
           }
           return null;
          }
         });
   ```
   
        上面定义完成以后 就可以使用新的加载方式:
   ``` java
         String newPath = "/storage/emulated/0/gnowwp/resized/london_night.png";
         Magic.with(MainActivity.this).loadImage(newPath, "new_type").into(mImageView);
       } 
   ```
 
 ### 异步加载 不需要imageview去显示 只是获取图片的bitmap
      和上面加载到imageview的方式几乎相同 只是into(null)活着into():
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
        //或者   .into(null);
      }
   ```
     
### 2.网络请求
   有下面的加载方式 JsonobjectRequest JsonArrayRequest StringRequest FileRequest
    
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
    
   ```Java 
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
   ```

