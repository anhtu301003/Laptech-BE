package com.project.LaptechBE.untils;

import java.security.SecureRandom;
import java.util.Base64;

public class generateRandomToken {

    public static String randomToken(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24]; // 24 bytes = 32 ký tự base64
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }
}
