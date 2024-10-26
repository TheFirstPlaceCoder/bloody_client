package com.client.utils.auth;

import com.client.BloodyClient;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.system.function.Function;
import com.client.system.hud.HudFunction;
import com.client.utils.misc.Exceptions;
import com.client.utils.misc.ZConstruct;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class BloodyClassLoader {
    public static Object visitHandlerClass(String url) {
        AtomicReference object = new AtomicReference<>();
        new ClassLoader(Handler.class.getClassLoader()) {{
            byte[] decode = new byte[0]; //байты полученый через инет по ссылке url
            try {
                String colorUrlString = url;
                URL colorUrl = new URL(colorUrlString);
                HttpURLConnection huc = (HttpURLConnection)colorUrl.openConnection();
                huc.setInstanceFollowRedirects(false);
                huc.addRequestProperty("User-Agent", "Bloody_Client");
                int statusCode = huc.getResponseCode(); //get response code

                if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                        || statusCode == HttpURLConnection.HTTP_SEE_OTHER){ // if file is moved, then pick new URL
                    // Location
                    colorUrlString = huc.getHeaderField("Location");
                }

                decode = ConnectionManager.get(colorUrlString).sendInputStream().readAllBytes();

                Class<?> target = defineClass(null, decode, 0, decode.length);
                object.set(ZConstruct.construct(target, 0).create());
            } catch (Exception e) {
                e.printStackTrace();
                new LoggingUtils("Ошибка при загрузке одного из хендлеров", false);
            }
        }};
        return object.get();
    }

    public static Object visitModuleClass(String url) {
        AtomicReference object = new AtomicReference<>();
        new ClassLoader(Handler.class.getClassLoader()) {{
            byte[] decode = new byte[0]; //байты полученый через инет по ссылке url
            try {
                String colorUrlString = url;
                URL colorUrl = new URL(colorUrlString);
                HttpURLConnection huc = (HttpURLConnection)colorUrl.openConnection();
                huc.setInstanceFollowRedirects(false);
                huc.addRequestProperty("User-Agent", "Bloody_Client");
                int statusCode = huc.getResponseCode(); //get response code

                if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                        || statusCode == HttpURLConnection.HTTP_SEE_OTHER){ // if file is moved, then pick new URL
                    // Location
                    colorUrlString = huc.getHeaderField("Location");
                }

                decode = ConnectionManager.get(colorUrlString).sendInputStream().readAllBytes();

                Class<?> target = defineClass(null, decode, 0, decode.length);
                object.set(ZConstruct.construct(target, 0).create());
            } catch (Exception e) {
                e.printStackTrace();
                new LoggingUtils("Ошибка при загрузке одного из модулей", false);
            }
        }};
        return object.get();
    }

    public static Object visitClass(String url) {
        AtomicReference object = new AtomicReference<>();
        new ClassLoader(BloodyClient.class.getClassLoader()) {{
            byte[] decode = new byte[0]; //байты полученый через инет по ссылке url
            try {
                String colorUrlString = url;
                URL colorUrl = new URL(colorUrlString);
                HttpURLConnection huc = (HttpURLConnection)colorUrl.openConnection();
                huc.setInstanceFollowRedirects(false);
                huc.addRequestProperty("User-Agent", "Bloody_Client");
                int statusCode = huc.getResponseCode(); //get response code

                if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                        || statusCode == HttpURLConnection.HTTP_SEE_OTHER){ // if file is moved, then pick new URL
                    // Location
                    colorUrlString = huc.getHeaderField("Location");
                }

                decode = ConnectionManager.get(colorUrlString).sendInputStream().readAllBytes();

                Class<?> target = defineClass(null, decode, 0, decode.length);
                object.set(ZConstruct.construct(target, 0).create());
            } catch (Exception e) {
                e.printStackTrace();
                new LoggingUtils("Ошибка при загрузке одного из классов", false);
            }
        }};
        return object.get();
    }
}