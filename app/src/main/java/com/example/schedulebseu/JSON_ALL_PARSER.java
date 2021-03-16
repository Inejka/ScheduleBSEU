package com.example.schedulebseu;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

public class JSON_ALL_PARSER {

    private static final String BASE_URL = "http://bseu.by/schedule/";
    private Retrofit mRetrofit;

    private Map<String, String> map;
    private List<String> list;
    private SpinnerAdapter testA;

    public JSON_ALL_PARSER(Map<String, String> map, List<String> list, SpinnerAdapter test) {
        this.list = list;
        this.map = map;
        this.testA = test;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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

    private int strFuck(String toWork, String Generate, int Current) {
        Current++;
        StringBuilder GenerateBuilder = new StringBuilder(Generate);
        while (toWork.charAt(Current) != '"') {
            GenerateBuilder.append(toWork.charAt(Current));
            Current++;
        }
        Generate = GenerateBuilder.toString();
        Current++;
        return Current;
    }


    public void sendPost(String faculty, String form, String course, String group, String __act) {
        mRetrofit.create(JSONPlaceHolderApi.class).
                postData(faculty, form, course, group, "", "3", __act).enqueue(new Callback<PostANS>() {
            @Override
            public void onResponse(Call<PostANS> call, Response<PostANS> response) {
                if (response.isSuccessful()) {
                    int test = 0;
                    String str = response.body().responseBody;
                    while (test != str.length()) {
                        if (str.charAt(test) == '"') {
                            test++;
                            while (str.charAt(test) != '"') test++;
                            test++;
                            test++;
                            test++;
                            String value = "", text = "";
                            while (str.charAt(test) != '"') {
                                value += str.charAt(test);
                                test++;
                            }
                            test++;
                            test++;
                            test++;
                            while (str.charAt(test) != '"') test++;
                            test++;
                            test++;
                            test++;
                            while (str.charAt(test) != '"') {
                                text += str.charAt(test);
                                test++;
                            }
                            map.put(text, value);
                            list.add(text);
                        }
                        test++;
                    }
                    testA.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PostANS> call, Throwable t) {
                sendPost(faculty, form, course, group, __act);
            }
        });
    }


    private class PostANS {
        public String responseBody;

        public PostANS(String responseBody) throws IOException {
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
            return new PostANS(responseBody.string());
        }
    }
}
