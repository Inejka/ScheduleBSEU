package com.example.schedulebseu;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class HTML_SCHEDULE_PARSER {

    private static final String BASE_URL = "http://bseu.by/schedule/";
    private Retrofit mRetrofit;
    Context mContext;
    Fragment mFragment;

    public HTML_SCHEDULE_PARSER(Context toStr,Fragment tt) {
        mContext=toStr;
        mFragment=tt;
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(20));
        dispatcher.setMaxRequests(20);
        dispatcher.setMaxRequestsPerHost(1);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(new FixEncodingInterceptor())
                .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(new PageAdapter().FACTORY)
                .build();
    }

    public interface JSONPlaceHolderApi {
        @POST(".")
        @FormUrlEncoded
        public Call<PostANS> postData(@Field("faculty") String faculty,
                                      @Field("form") String form,
                                      @Field("course") String course,
                                      @Field("group") String group,
                                      @Field("tname") String tname,
                                      @Field("period") String period,
                                      @Field("__act") String __act);
    }


    public void sendPost(String faculty, String form, String course, String group, String __act) {
        mRetrofit.create(JSONPlaceHolderApi.class).
                postData(faculty, form, course, group, "", "3", __act).enqueue(new Callback<PostANS>() {
            @Override
            public void onResponse(Call<PostANS> call, Response<PostANS> response) {
                if (response.isSuccessful()) {
                    Parser parser = new Parser(response.body().responseBody);
                    parser.parse(mContext,mFragment);
                }
            }

            @Override
            public void onFailure(Call<PostANS> call, Throwable t) {
                sendPost(faculty, form, course, group, __act);
            }
        });
    }


    private class PostANS {
        public Document responseBody;

        public PostANS(Document responseBody) throws IOException {
            this.responseBody = responseBody;
        }
    }

    final class PageAdapter implements Converter<ResponseBody, PostANS> {
        final Converter.Factory FACTORY = new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                if (type == PostANS.class)
                    return new PageAdapter();
                return null;
            }
        };

        @Override
        public PostANS convert(ResponseBody responseBody) throws IOException {
            return new PostANS(Jsoup.parse(responseBody.string()));
        }
    }

    class FixEncodingInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response response = chain.proceed(chain.request());
            MediaType oldMediaType = MediaType.parse(response.header("Content-Type"));
            // update only charset in mediatype
            MediaType newMediaType = MediaType.parse(oldMediaType.type() + "/" + oldMediaType.subtype() + "; charset=windows-1251");
            // update body
            ResponseBody newResponseBody = ResponseBody.create(newMediaType, response.body().bytes());

            return response.newBuilder()
                    .removeHeader("Content-Type")
                    .addHeader("Content-Type", newMediaType.toString())
                    .body(newResponseBody)
                    .build();
        }


    }
}
