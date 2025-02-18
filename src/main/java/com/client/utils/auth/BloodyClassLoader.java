package com.client.utils.auth;

import com.client.system.function.Function;
import com.client.utils.Utils;
import com.client.utils.auth.enums.ClassType;
import com.client.utils.misc.ZConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodyClassLoader {
    public static String cookie = "";

    public static Object visit(String url, ClassType type) {
        AtomicReference<Object> object = new AtomicReference<>();

        new ClassLoader(Function.class.getClassLoader()) {{
            byte[] decode = new byte[0]; // байты, полученные через интернет по ссылке url
            String colorUrlString = url;
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

                        cookie = "__test=" + str;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                URL colorUrl = new URL(colorUrlString);
                HttpURLConnection huc = (HttpURLConnection)colorUrl.openConnection();
                huc.setInstanceFollowRedirects(false);
                huc.addRequestProperty("User-Agent", userAgent);
                huc.setRequestProperty("Cookie", cookie);
                int statusCode = huc.getResponseCode(); //get response code

                if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                        || statusCode == HttpURLConnection.HTTP_SEE_OTHER){ // if file is moved, then pick new URL
                    // Location
                    colorUrlString = huc.getHeaderField("Location");
                }

                // Считываем закодированные байты
                InputStream fis = ConnectionManager.get(colorUrlString).sendInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int inputed;
                int index = 0;

                // Расшифровываем байты
                while ((inputed = fis.read()) != -1) {
                    bos.write(inputed & 0xFF); // Записываем байт

                    if (index > 0 && index % getOffset(type) == 0) {
                        fis.read(); // Пропускаем случайное значение
                    }

                    index++;
                }
                decode = bos.toByteArray();

                // Загружаем класс
                Class<?> target = defineClass(null, decode, 0, decode.length);
                object.set(ZConstruct.construct(target, 0).create());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }};

        return object.get();
    }

    public static int getOffset(ClassType classType) {
        return switch (classType) {
            case Default -> 7;
            case Handler -> 5;
            default -> 3;
        };
    }
}
