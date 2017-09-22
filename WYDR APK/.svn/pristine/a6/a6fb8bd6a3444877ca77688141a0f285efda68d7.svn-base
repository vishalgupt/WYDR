package wydr.sellers.network;

import com.google.gson.JsonElement;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import wydr.sellers.activities.AppUtil;
import wydr.sellers.gson.MessageRequest;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by surya on 23/2/16.
 */
public class RestClientOpenfire {
    private static GitApiInterface gitApiInterface;
    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    public static GitApiInterface getClient() {
        if (gitApiInterface == null) {

            OkHttpClient okClient = new OkHttpClient();
            okClient.setReadTimeout(60, TimeUnit.SECONDS);
            okClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            okClient.interceptors().add(httpLoggingInterceptor);

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppUtil.URL_XMPP)
                    .addConverter(String.class, new ToStringConverter())
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            gitApiInterface = client.create(GitApiInterface.class);
        }
        return gitApiInterface;
    }

    public interface GitApiInterface {

        @POST("index.php")
        Call<JsonElement> getMessage(@Body MessageRequest request);
    }

}
