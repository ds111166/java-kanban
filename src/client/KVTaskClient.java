package client;

import manager.exceptions.ManagerSaveException;

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

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        send(request);
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        return send(request);
    }

    private String register() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "register/"))
                .GET()
                .build();
        return send(request);
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode());
            }
            return response.body();
        } catch (InterruptedException | IOException ex) {
            throw new ManagerSaveException("Can't do save request", ex);
        }

    }

}
