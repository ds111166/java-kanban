package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiToken;
    private final HttpClient client;
    private final String url;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newBuilder().build();
        String apiToken = register();
        this.apiToken = (apiToken == null) ? "" : apiToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        send(request);
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        return send(request);
    }

    private String register() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .GET()
                .build();
        return send(request);
    }

    private String send(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (InterruptedException | IOException ex) {
            System.out.println("При выполнении запроса возникла исключительная ситуаци:\n"
                    + ex.getMessage());
            return null;
        }

    }

}
