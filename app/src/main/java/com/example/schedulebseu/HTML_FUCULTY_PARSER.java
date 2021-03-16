package com.example.schedulebseu;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class HTML_FUCULTY_PARSER {
    static public Map<String, String> mapFaculties;
    static public List<String> listFaculties;
    static public SpinnerAdapter test;

    public HTML_FUCULTY_PARSER(Map<String, String> mapFaculties, List<String> listFaculties, SpinnerAdapter test) {
        this.mapFaculties = mapFaculties;
        this.listFaculties = listFaculties;
        this.test = test;
        start();
        test.notifyDataSetChanged();
    }

    private void start() {
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(20));
        dispatcher.setMaxRequests(20);
        dispatcher.setMaxRequestsPerHost(1);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(new FixEncodingInterceptor())
                .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://bseu.by/schedule/")
                .client(okHttpClient)
                .addConverterFactory(PageAdapter.FACTORY)
                .build();

        PageService requestAddress = retrofit.create(PageService.class);
        Call<Page> pageCall = requestAddress.get(HttpUrl.parse("http://bseu.by/schedule/"));
        pageCall.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                start();
            }
        });
    }


    static class Page {
        public static String content;

        Page(String content) {
            this.content = content;
        }
    }

    static final class PageAdapter implements Converter<ResponseBody, HTML_FUCULTY_PARSER.Page> {
        static final Converter.Factory FACTORY = new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                if (type == HTML_FUCULTY_PARSER.Page.class)
                    return new HTML_FUCULTY_PARSER.PageAdapter();
                return null;
            }
        };

        @Override
        public HTML_FUCULTY_PARSER.Page convert(ResponseBody responseBody) throws IOException {
            Document document = Jsoup.parse(responseBody.string());
            Element options = document.select("#faculty").get(0);
            for (Element i : options.children()) {
                if (!i.attributes().get("value").toString().equals("-1")) {
                    HTML_FUCULTY_PARSER.listFaculties.add(i.text());
                    HTML_FUCULTY_PARSER.mapFaculties.put(i.text(), i.attributes().get("value").toString());
                }
            }
            //for (Element i : options.children())
            //    Log.i("Hah", i.text());
            return new HTML_FUCULTY_PARSER.Page(responseBody.string());
        }
    }

    interface PageService {
        @GET
        Call<HTML_FUCULTY_PARSER.Page> get(@Url HttpUrl url);
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
