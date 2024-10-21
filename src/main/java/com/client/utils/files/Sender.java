package com.client.utils.files;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Sender {
    public static double message(String url, String content) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        JSONObject object = new JSONObject();
        try {
            object.put("content", content);
        } catch (JSONException ignore) {
        }
        RequestBody requestBody = RequestBody.create(object.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return 0;
                }
                if (response.code() == 429) {
                    String rateLimitResetAfterStr = response.header("X-RateLimit-Reset-After");
                    if (rateLimitResetAfterStr == null) {
                        return 1;
                    }
                    try {
                        return Double.parseDouble(rateLimitResetAfterStr);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                }
            }
        } catch (IOException ignore) {
        }
        return 1;
    }
}
