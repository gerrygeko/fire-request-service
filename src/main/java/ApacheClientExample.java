import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class ApacheClientExample {

    private static final String METRICS_SERVLET = "/management/metrics";
    private static final String HOSTNAME = "http://localhost";
    private static final String CREDENTIALS = "admin:admin";

    public static void main(String[] args) throws IOException {

        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpGet get = new HttpGet(HOSTNAME + METRICS_SERVLET);
            get.addHeader("Authorization", createAuthorizationHeaderValue(CREDENTIALS));

            try (CloseableHttpResponse resp = client.execute(get)) {
                //String json = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(resp.getEntity().getContent());
                System.out.println(rootNode);

                JsonNode value = rootNode.findValue("output-version");
                System.out.println(value.toString());
            }

        } finally {
            System.out.println("Closing the HTTP client");
        }
    }
    
    private static String createAuthorizationHeaderValue(String userAndPassword) {
        String encodedUserAndPassword = StringUtils.newStringUtf8(Base64.encodeBase64(userAndPassword.getBytes()));
        return "Basic " + encodedUserAndPassword;
    }

}
