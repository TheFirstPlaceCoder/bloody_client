package com.client.utils.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static boolean isSuspiciousNetworkActivity() {
        // TODO: Код ниже проверяет подключения к локальному серверу на компе. Это нужно для того, чтобы защиту нельзя было обойти с помощью подмены запросов
        List<String> suspiciousIPs = List.of("192.168.1.1", "10.0.0.1", "127.0.0.1");
        List<Integer> suspiciousPorts = List.of(80, 443);
        List<String> activeConnections = getActiveConnections();
        Iterator var3 = activeConnections.iterator();

        while(var3.hasNext()) {
            String connection = (String)var3.next();
            Iterator var5 = suspiciousIPs.iterator();

            while(var5.hasNext()) {
                String suspiciousIP = (String)var5.next();
                if (connection.contains(suspiciousIP)) {
                    new LoggingUtils("Обнаружено подозрительное соединение с IP: " + suspiciousIP, false);
                    return true;
                }
            }

            var5 = suspiciousPorts.iterator();

            while(var5.hasNext()) {
                Integer suspiciousPort = (Integer)var5.next();
                if (connection.contains(":" + suspiciousPort)) {
                    new LoggingUtils("Обнаружено подозрительное соединение на порту: " + suspiciousPort, false);
                    return true;
                }
            }
        }

        return false;
    }

    public static String getIP() {
        return ConnectionManager.get("https://bloodyhvh.site/php/getIp.php").sendString();
    }

    private static List<String> getActiveConnections() {
        List<String> connections = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec("netstat -an");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                connections.add(line);
            }

            reader.close();
        } catch (IOException var4) {
            var4.printStackTrace();
            new LoggingUtils("Ошибка получения активных соединений!", false);
        }

        return connections;
    }
}
