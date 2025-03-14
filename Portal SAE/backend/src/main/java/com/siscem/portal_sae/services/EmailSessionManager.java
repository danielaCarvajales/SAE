package com.siscem.portal_sae.services;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmailSessionManager {
    private static final Map<String, String> userTokens = new ConcurrentHashMap<>();

    public static void storeSession(String email, String token) {
        userTokens.put(email, token);
    }

    public static String getSession(String email) {
        return userTokens.get(email);
    }

    public static void removeSession(String email) {
        userTokens.remove(email);
    }
}
