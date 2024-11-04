package com.client.utils.auth;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionUtils {
    public static boolean checkInternetConnection() {
        // TODO: Код для проверки подключения к сайту
        // Раньше тут было подключение к Гуглу, однако это ошибочно
        // Все связано с тем, что подключение к гуглу могло существовать, а как только чит пытался подключиться к сайту клиента, выдавало ошибку
        // Из-за этого лучше сразу проверять подключение напрямую к сайту софта
        boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("https://bloodyhvh.site").openConnection();
            con.addRequestProperty("User-Agent", "Bloody_Client");
            con.setRequestMethod("HEAD");
            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            new LoggingUtils("Не удалось подключиться к сайту!", false);
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ignored) {
                }
            }
        }

        return result;
    }

    public static String getIP() {
        return ConnectionManager.get("https://bloodyhvh.site/php/getIp.php").sendString();
    }
}
