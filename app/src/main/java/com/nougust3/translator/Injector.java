package com.nougust3.translator;

import com.nougust3.translator.data.remote.ApiService;
import com.nougust3.translator.data.remote.DictService;
import com.nougust3.translator.utils.NetworkHelper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injector {

    private static Retrofit provideRetrofit(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(provideOkHttp())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient provideOkHttp() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideOfflineCache())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(Translator.getAppContext().getCacheDir(),
                    "http-cache"), 25 * 1024 * 1024);
        } catch (Exception ignored) { }
        return cache;
    }

    private static Interceptor provideOfflineCache() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if(!NetworkHelper.getInstance().isNetworkAvailable()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS).build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl).build();
                }

                return chain.proceed(request);
            }
        };
    }

    private static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES).build();
                return response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        };
    }

    public static DictService provideDictService() {
        return provideRetrofit(Constants.DICTIONARY_URL).create(DictService.class);
    }

    public static ApiService provideTranslService() {
        return provideRetrofit(Constants.TRANSLATOR_URL).create(ApiService.class);
    }

}
