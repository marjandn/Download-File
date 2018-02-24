package dnejad.marjan.downloadfile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Marjan.Dnejad
 * on 2/8/2018.
 */

public class ServiceGenerator {

    private static final String URL = "http://10.0.2.2:23947/";
    private TaskService apiService;

    public ServiceGenerator()
    {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();
                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder()
                                        //cues it want to run as local in local server
                                        .header("Host", "localhost")
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .connectTimeout(5, TimeUnit.MINUTES) // for handle timeout exception
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setLenient()
                .create();

        retrofit2.Retrofit restAdapter = new retrofit2.Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okClient)
                .build();

        apiService = restAdapter.create(TaskService.class);
    }

    public TaskService getService()
    {
        return apiService;
    }

}
