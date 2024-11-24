package com.client.utils.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryptor {
    // TODO: Данный класс представляет собой дешифратор хешей с помощью AES шифрования
    // Для чайников: Это симметричный алгоритм шифрования. лочный шифр
    // Это значит, что, в отличие от потоковых шифров, которые обрабатывают данные бит за битом, блочные шифры работают на фиксированных блоках данных

    public static String decryptKey(String encryptedText, String key) {
        try {
            // Ключ дешифровки TODO: fKjMnblKs936Hd8Y
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedText) {
        try {
            // Ключ дешифровки TODO: fKjMnblKs936Hd8Y
            SecretKeySpec secretKey = new SecretKeySpec(decryptKey(decryptKey("5Fv/jlqTpk6tl+9QJWymwOO/o3MIQvVWjbR3uNbk/AMVS+GFfx47SQfl3gprop0l", "fhsy63JNBldj8930"), decryptKey("BQuPgTvrUsygm17wS/mlfRPioNX/yUvEkseq3A8HpqE=", "dgbDks8iend3q0kL")).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}