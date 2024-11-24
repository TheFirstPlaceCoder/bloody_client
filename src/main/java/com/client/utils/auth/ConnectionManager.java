package com.client.utils.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConnectionManager {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private enum Method {
        GET,
        POST
    }

    public static class Request {
        private HttpRequest.Builder builder;
        private Method method;

        public Request(Method method, String url) {
            try {
                // User-Agent Bloody_Client
                this.builder = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", "Bloody_Client");
                this.method = method;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        private <T> T _send(String accept, HttpResponse.BodyHandler<T> responseBodyHandler) {
            builder.header("Accept", accept);
            if (method != null) builder.method(method.name(), HttpRequest.BodyPublishers.noBody());

            try {
                var res = CLIENT.send(builder.build(), responseBodyHandler);
                return res.statusCode() == 200 ? res.body() : null;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        public InputStream sendInputStream() {
            return _send("*/*", HttpResponse.BodyHandlers.ofInputStream());
        }

        public void send() {
            _send("*/*", HttpResponse.BodyHandlers.discarding());
        }

        public String sendString() {
            return _send("*/*", HttpResponse.BodyHandlers.ofString());
        }
    }

    public static Request get(String url) {
        return new Request(Method.GET, url);
    }

    public static Request post(String url) {
        return new Request(Method.POST, url);
    }
}