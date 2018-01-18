package com.bawie.moni_yuekao0113.util;

import android.os.Environment;
import android.util.Log;

import com.bawie.moni_yuekao0113.interfac.SerViceAPI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitHepler {
    public static OkHttpClient okHttpClient;
    public static SerViceAPI apiService;


    static {
        getOkHttpClient();
    }


    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            synchronized (OkHttpClient.class) {
                if(okHttpClient == null){
                    File fileDir = new File(Environment.getExternalStorageDirectory(), "cache");
                    long fileSize = 10 * 1024 * 1024;
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .addInterceptor(new CommonParamsInterceptor())
                            .cache(new Cache(fileDir, fileSize))
                            .build();
                }
            }
        }
        return okHttpClient;
    }


    public static SerViceAPI getSerViceAPI(String url){
        if(apiService == null){
            synchronized (OkHttpClient.class) {
                apiService = createApiService(SerViceAPI.class,url);
            }
        }
        return apiService;
    }



    private static <T>T createApiService(Class<T> tClass, String url) {
        T t = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(tClass);
        return t;
    }


    private static class CommonParamsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String method = request.method();
            String oldUrl = request.url().toString();
            Log.e("---拦截器",request.url()+"---"+request.method()+"--"+request.header("User-agent"));
            Map<String,String> map = new HashMap<>();
            map.put("source","android");

            if ("GET".equals(method)){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(oldUrl);
                if (oldUrl.contains("?")){
                    if (oldUrl.indexOf("?") == oldUrl.length()-1){
                    }else {
                        stringBuilder.append("&");
                    }
                }else {
                    stringBuilder.append("?");
                }
                for (Map.Entry<String,String> entry: map.entrySet()) {
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }
                if (stringBuilder.indexOf("&") != -1){
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
                }
                String newUrl = stringBuilder.toString();
                request = request.newBuilder()
                        .url(newUrl)
                        .build();
            }else if ("POST".equals(method)){
                RequestBody oldRequestBody = request.body();
                if (oldRequestBody instanceof FormBody){
                    FormBody oldBody = (FormBody) oldRequestBody;
                    FormBody.Builder builder = new FormBody.Builder();
                    for (int i=0;i<oldBody.size();i++){
                        builder.add(oldBody.name(i),oldBody.value(i));
                        Log.d("CommonParamsInterceptor", oldBody.name(i)+"===="+oldBody.value(i));

                    }
                    for (Map.Entry<String,String> entry:map.entrySet()) {
                        builder.add(entry.getKey(),entry.getValue());
                    }
                    FormBody newBody = builder.build();
                    request = request.newBuilder()
                            .url(oldUrl)
                            .post(newBody)
                            .build();
                }
            }
            Response response = chain.proceed(request);
            return response;
        }
    }
}
