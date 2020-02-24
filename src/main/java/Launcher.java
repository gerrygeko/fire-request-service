import lombok.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Launcher {

    private final static HttpClient client = HttpClient.newBuilder().build();
    public static final int NUMBER_OF_REQUESTS_TO_FIRE = 5000;
    private static AtomicInteger completedRequets = new AtomicInteger(0);

    public static void main(String[] args) {
        URI uri = URI.create("http://localhost:8080/v2/roles");

        for (int i = 0; i < NUMBER_OF_REQUESTS_TO_FIRE; i ++) {
            //Thread.sleep(10);
            System.out.println("Firing request N.: " + i);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("XTopdeskAuthToken", "bla")
                    .build();
            RequestThread thread = new RequestThread(i, request);
            thread.start();
        }
        while (completedRequets.get() != NUMBER_OF_REQUESTS_TO_FIRE) {

        }
        System.err.println("Request completed: " + completedRequets.get());
    }

    private static void fireRequest(int i, HttpRequest request) {
        CompletableFuture<HttpResponse<String>> completableResponse = null;
        HttpResponse httpResponse = null;
        try {
            completableResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            httpResponse = completableResponse.get();
        } catch (InterruptedException e) {
            System.err.println("Error During the request");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.err.println("Error During the execution of the request");
            e.printStackTrace();
        } finally {
            if (httpResponse.statusCode() == 200) {
                System.out.println("Request successfull N.: " + i);
//                String body = (String) httpResponse.body();
//                System.out.println("Body of the request: " + body);
                completedRequets.incrementAndGet();
            } else {
                System.err.println("Request status code is:" + httpResponse.statusCode());
                completedRequets.incrementAndGet();
            }
        }
    }

    @Value
    private static class RequestThread extends Thread {

        private int i;
        private HttpRequest request;

        @Override
        public void run() {
            fireRequest(i, request);
        }
    }
}
