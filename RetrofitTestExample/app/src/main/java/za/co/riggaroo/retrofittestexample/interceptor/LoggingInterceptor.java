package za.co.riggaroo.retrofittestexample.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


/**
 * @author rebeccafranks
 * @since 15/10/23.
 */
public class LoggingInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Timber.tag("Retrofit").d("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers());

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Timber.tag("Retrofit").d("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        return response;
    }
}
