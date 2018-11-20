package za.co.riggaroo.retrofittestexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;
import za.co.riggaroo.retrofittestexample.interceptor.LoggingInterceptor;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayErrorResponse;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuoteOfTheDay;
    private Button buttonRetry;

    private QuoteOfTheDayRestService service;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuoteOfTheDay = findViewById(R.id.text_view_quote);
        buttonRetry = findViewById(R.id.button_retry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuoteOfTheDay();
            }
        });

        OkHttpClient client =
                new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor())
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(BODY))
                        .build();
        // client.interceptors().add(new LoggingInterceptor());
        retrofit = new Retrofit.Builder()
                .baseUrl(QuoteOfTheDayConstants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(QuoteOfTheDayRestService.class);
        getQuoteOfTheDay();

    }


    private void getQuoteOfTheDay() {
        Call<QuoteOfTheDayResponse> call =
                service.getQuoteOfTheDay();

        call.enqueue(new Callback<QuoteOfTheDayResponse>() {

            @Override
            public void onResponse(@NonNull Call<QuoteOfTheDayResponse> call, @NonNull Response<QuoteOfTheDayResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    textViewQuoteOfTheDay.setText(response.body().getContents().getQuotes().get(0).getQuote());
                } else {
                    try {
                        Converter<ResponseBody, QuoteOfTheDayErrorResponse> errorConverter = retrofit.responseBodyConverter(QuoteOfTheDayErrorResponse.class, new Annotation[0]);
                        assert response.errorBody() != null;
                        QuoteOfTheDayErrorResponse error = errorConverter.convert(response.errorBody());
                        assert error != null;
                        showRetry(error.getError().getMessage());

                    } catch (IOException e) {
                        Timber.e(e, "IOException parsing error:");
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<QuoteOfTheDayResponse> call, @NonNull Throwable t) {
                //Transport level errors such as no internet etc.
            }
        });


    }

    private void showRetry(String error) {
        textViewQuoteOfTheDay.setText(error);
        buttonRetry.setVisibility(View.VISIBLE);

    }
}
