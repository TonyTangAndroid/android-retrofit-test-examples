package za.co.riggaroo.retrofittestexample;

import android.app.Application;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;


public class App extends Application {


    private MockWebServer server;


    @Override
    public void onCreate() {
        super.onCreate();
        server = new MockWebServer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        }).start();

    }

    private void startServer() {
        try {
            server.start();
            String baseUrl = server.url("/").toString();
            QuoteOfTheDayConstants.BASE_URL = baseUrl;
            stubResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void stubResponse() throws Exception {
        {
            String fileName = "quote_200_ok_response.json";
            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(RestServiceTestHelper.getStringFromFile(this, fileName)));
        }
//        {
//            String fileName = "quote_404_not_found.json";
//            server.enqueue(new MockResponse()
//                    .setResponseCode(404)
//                    .setBody(RestServiceTestHelper.getStringFromFile(this, fileName)));
//        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            server.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
