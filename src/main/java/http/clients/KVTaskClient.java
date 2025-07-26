package main.java.http.clients;

import main.java.exceptions.RegisterClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class KVTaskClient {
    private HttpClient httpClient;
    private String url;
    private String API_KEY;

    public KVTaskClient(String url) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.url = url;
        API_KEY = register();
    }

    private String register() {
        URI uri = URI.create(url + "/register");
        HttpRequest httpRequest = HttpRequest.
                newBuilder().
                uri(uri).
                GET().
                build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();

            if (statusCode != 200)
                throw new RegisterClientException();
            return httpResponse.body();
        }
        catch (Exception e) {
            throw new RegisterClientException();
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API-KEY=" + API_KEY);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, handler);
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode <= 299)
                System.out.println("Успех. Статус код: " + statusCode);
            else if (statusCode >= 400 && statusCode <= 499)
                System.out.println("Проблема с запросом. Статус код: " + statusCode);
            else if (statusCode >= 500 && statusCode <= 599)
                System.out.println("Проблема с сервером. Статус код: " + statusCode);
            return response.body();
        }
        catch (Exception e)
        {
            System.out.println("Во время запроса произошла ошибка, проверьте url");
            return null;
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API-KEY=" + API_KEY);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, handler);
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode <= 299)
                System.out.println("Успех. Статус код: " + statusCode);
            else if (statusCode >= 400 && statusCode <= 499)
                System.out.println("Проблема с запросом. Статус код: " + statusCode);
            else if (statusCode >= 500 && statusCode <= 599)
                System.out.println("Проблема с сервером. Статус код: " + statusCode);
        }
        catch (Exception e)
        {
            System.out.println("Во время запроса произошла ошибка, проверьте url");
        }
    }
}
