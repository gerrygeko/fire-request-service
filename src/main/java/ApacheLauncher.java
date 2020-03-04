import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ApacheLauncher {

    private static final int NUMBER_OF_REQUESTS_TO_FIRE = 5000;
    private static final String HOSTNAME = "http://localhost";

    private static AtomicInteger completedRequets = new AtomicInteger(0);
    private static String METRICS_SERVLET = "/testendpoint";


    public static void main(String[] args) {

        try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()){
            client.start();
            HttpGet get = new HttpGet(HOSTNAME + METRICS_SERVLET);
            get.addHeader("Token", "bla");

        } catch (IOException e) {
            System.out.println("HTTP client I/O error. Stacktrace: " + e);
        }

    }

    static class ResponseThread extends Thread {
        private CloseableHttpAsyncClient client;
        private HttpContext context;
        private HttpGet request;

        public ResponseThread(CloseableHttpAsyncClient client, HttpGet req){
            this.client = client;
            context = HttpClientContext.create();
            this.request = req;
        }

        @Override
        public void run() {
            try {
                Future<HttpResponse> future = client.execute(request, context, null);
                HttpResponse response = future.get();
            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
    }

}
