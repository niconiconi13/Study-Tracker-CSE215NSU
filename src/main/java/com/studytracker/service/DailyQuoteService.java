package com.studytracker.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Fetches a daily motivational quote from the ZenQuotes API.
 * Falls back to a built-in list if the network is unavailable.
 */
public class DailyQuoteService {

    private static final String API_URL = "https://zenquotes.io/api/random";

    private static final List<String[]> FALLBACK_QUOTES = List.of(
        new String[]{"The secret of getting ahead is getting started.", "Mark Twain"},
        new String[]{"It always seems impossible until it's done.", "Nelson Mandela"},
        new String[]{"Don't watch the clock; do what it does. Keep going.", "Sam Levenson"},
        new String[]{"Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"},
        new String[]{"Believe you can and you're halfway there.", "Theodore Roosevelt"},
        new String[]{"You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"},
        new String[]{"The expert in anything was once a beginner.", "Helen Hayes"},
        new String[]{"Learning never exhausts the mind.", "Leonardo da Vinci"},
        new String[]{"An investment in knowledge pays the best interest.", "Benjamin Franklin"},
        new String[]{"Education is not the filling of a pail, but the lighting of a fire.", "W.B. Yeats"},
        new String[]{"Pam, Naila and Shanto was here."}
    );

    /** Returns {quote, author}. Never throws — falls back on any error. */
    public String[] fetchQuote() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                String json = sb.toString();
                // Hey Naila try to parse it manually to avoid any dependencies
                String q = extractJsonField(json, "q");
                String a = extractJsonField(json, "a");
                if (q != null && a != null && !q.isBlank()) {
                    return new String[]{q, a};
                }
            }
        } catch (Exception ignored) { /* network not available therefore we are ignoring it*/ }
        return randomFallback();
    }

    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        return end < 0 ? null : json.substring(start, end)
            .replace("\\u0027", "'").replace("\\\"", "\"");
    }

    private String[] randomFallback() {
        return FALLBACK_QUOTES.get(new Random().nextInt(FALLBACK_QUOTES.size()));
    }
}
