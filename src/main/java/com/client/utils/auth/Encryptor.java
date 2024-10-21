package com.client.utils.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryptor {
    public static String decryptKey(String encryptedText, String key) {
        try {
            // fKjMnblKs936Hd8Y
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
            // fKjMnblKs936Hd8Y
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