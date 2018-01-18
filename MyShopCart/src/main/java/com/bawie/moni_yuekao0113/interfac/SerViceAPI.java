package com.bawie.moni_yuekao0113.interfac;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;



public interface SerViceAPI {
    //post方式无参
    @FormUrlEncoded
    @POST()
    Observable<String> getDataByPost(@Url String url);
    //post方式带参数--map
    @FormUrlEncoded
    @POST()
    Observable<String> getDataByPost(@Url String url,@FieldMap Map<String,String> map);
    //get方式无参
    @GET()
    Observable<String> getDataByGet(@Url String url);
    //get方式带参数--map
    @GET()
    Observable<String> getDataByGet(@Url String url,@QueryMap Map<String,String> map);
}
