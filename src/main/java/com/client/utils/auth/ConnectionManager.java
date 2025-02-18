package com.client.utils.auth;

import com.client.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionManager {
    public static String cookie = "";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private enum Method {
        GET,
        POST
    }

    public static class Request {
        private HttpRequest.Builder builder;
        private Method method;

        public Request(Method method, String url) {
            String userAgent = "Bloody_Client_Agent";
            try {
                try {
                    URL urlConnection = new URL(url);
                    java.net.URLConnection connection = urlConnection.openConnection();
                    connection.setRequestProperty("User-Agent", userAgent);
                    if (!cookie.isEmpty()) connection.setRequestProperty("Cookie", cookie);
                    String version = new String(connection.getInputStream().readAllBytes());

                    if (version.contains("toNumbers")) {
                        Pattern pattern = Pattern.compile("toNumbers\\(\"([^\"]+)\"\\)");
                        Matcher matcher = pattern.matcher(version);

                        String a = "", b = "", c = "";
                        int count = 0;

                        while (matcher.find() && count < 3) {
                            if (count == 0) {
                                a = matcher.group(1);
                            } else if (count == 1) {
                                b = matcher.group(1);
                            } else if (count == 2) {
                                c = matcher.group(1);
                            }
                            count++;
                        }

                        byte[] aNums = new byte[16];
                        byte[] bNums = new byte[16];
                        byte[] cNums = new byte[16];
                        SlowAES.toNumbers(a, aNums);
                        SlowAES.toNumbers(b, bNums);
                        SlowAES.toNumbers(c, cNums);

                        byte[] finalCookie = new byte[33];
                        byte[] resNums = new byte[16];
                        SlowAES.decrypt(cNums, aNums, bNums,resNums);
                        SlowAES.toHex(resNums, finalCookie);

                        String str = new String(finalCookie);
                        str = Utils.getStringIgnoreLastChar(str);

                        ConnectionManager.cookie = "__test=" + str;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.builder = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", userAgent);
                if (!cookie.isEmpty()) this.builder.header("Cookie", cookie);

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