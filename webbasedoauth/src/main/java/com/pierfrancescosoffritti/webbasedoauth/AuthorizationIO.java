package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing static methods for IO.
 * @author Pierfrancesco Soffritti.
 */

class AuthorizationIO {

    /**
     * Exchanges an authorization code for an access token and a refresh token
     */
    static JSONObject exchangeAuthorizationCode(@NonNull String tokenURL, @NonNull String authorizationCode, @NonNull String clientID,
                                                       @Nullable String clientSecret, @NonNull String redirectURI, @NonNull String grantType) throws IOException, JSONException {

        HttpURLConnection conn = null;
        try {
            conn = initConnection(new URL(tokenURL));

            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<>("code", authorizationCode));
            params.add(new Pair<>("client_id", clientID));
            if(clientSecret != null && clientSecret.length() != 0)
                params.add(new Pair<>("client_secret", clientSecret));
            params.add(new Pair<>("redirect_uri", redirectURI));
            params.add(new Pair<>("grant_type", grantType));

            return httpPost(conn, params);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * Ask the server for a new access token, using a refresh token
     */
    static JSONObject refreshAccessToken(@NonNull String tokenURL, @NonNull String clientID, @Nullable String clientSecret,
                                                @NonNull String refreshToken, @NonNull String grantType) throws IOException, JSONException {

        HttpURLConnection conn = null;
        try {
            conn = initConnection(new URL(tokenURL));

            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<>("client_id", clientID));
            if (clientSecret != null && clientSecret.length() != 0)
                params.add(new Pair<>("client_secret", clientSecret));
            params.add(new Pair<>("refresh_token", refreshToken));
            params.add(new Pair<>("grant_type", grantType));

            return httpPost(conn, params);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    private static HttpURLConnection initConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        conn.setDoInput(true);
        conn.setDoOutput(true);

        return conn;
    }

    private static JSONObject httpPost(HttpURLConnection conn, List<Pair<String, String>> params) throws IOException, JSONException {
        OutputStream outputStream = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.write(writeBody(params));
        writer.flush();
        writer.close();
        outputStream.close();

        conn.connect();

        InputStream inputStream = conn.getInputStream();
        int ch;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = inputStream.read()) != -1)
            stringBuilder.append((char) ch);

        return new JSONObject(stringBuilder.toString());
    }

    private static String writeBody(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String, String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }
}
