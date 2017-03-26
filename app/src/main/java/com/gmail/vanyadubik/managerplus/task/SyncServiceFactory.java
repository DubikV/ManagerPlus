package com.gmail.vanyadubik.managerplus.task;

import android.content.Context;
import android.util.Base64;

import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gmail.vanyadubik.managerplus.common.Consts.CONNECT_TIMEOUT_SECONDS_RETROFIT;
import static com.gmail.vanyadubik.managerplus.common.Consts.LOGIN;
import static com.gmail.vanyadubik.managerplus.common.Consts.PASSWORD;
import static com.gmail.vanyadubik.managerplus.common.Consts.SERVER;

public class SyncServiceFactory {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .readTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS_RETROFIT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static Retrofit.Builder builder = null;
    private static DataRepository dataRepository;

    private static JsonSerializer<Date> dateJsonSerializer = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    };

    private static JsonDeserializer<Date> dateJsonDeserializer = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            return json == null ? null : new Date(json.getAsLong());
        }
    };


    private static Retrofit.Builder getBuilder(String url) {
        //if (builder != null) return builder;

        return builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .registerTypeAdapter(Date.class, dateJsonSerializer)
                                .registerTypeAdapter(Date.class, dateJsonDeserializer)
                                .create()));
    }

    private static String getBaseURL(){
        String mServer = dataRepository.getUserSetting(SERVER);

        return "http://" + (mServer == null ? "" : mServer) + "/";
    }

    public static <S> S createService(Class<S> serviceClass, Context context) {
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        dataRepository = new DataRepositoryImpl(context.getContentResolver());

        String baseUrl = getBaseURL();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                String username = dataRepository.getUserSetting(LOGIN);
                String password = dataRepository.getUserSetting(PASSWORD);

                String credentials = (username == null ? "" : username) + ":"
                        + (password == null ? "" : password);
                final String basic =
                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
                }

        });

        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.build();

        Retrofit retrofit;
        try {
            retrofit = getBuilder(baseUrl).client(client).build();
        }catch (IllegalArgumentException e) {
            retrofit = getBuilder("http://localhost").client(client).build();
        }
        return retrofit.create(serviceClass);
    }
}
