package com.pierfrancescosoffritti.webbasedoauth;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An AsyncTask that uses the refresh token to get a valid access token
 */
class RefreshTokenTask extends AsyncTask<String, String, JSONObject> {
    private AuthenticatedUser authenticatedUser;
    private Authenticator authenticator;

    public RefreshTokenTask(Authenticator authenticator, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.authenticator = authenticator;
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        JSONObject json = null;
        try {
            json = AuthorizationIO.refreshAccessToken(args[0], args[1], args[2], authenticatedUser.getRefreshToken(), args[3]);
        } catch (RuntimeException e) {
            e.printStackTrace();
            authenticatedUser.remove();
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        if (json != null){
            try {
                String accessToken = json.getString("access_token");
                String expireIn = json.getString("expires_in");

                authenticatedUser.setNewAccessToken(accessToken, Integer.parseInt(expireIn));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        authenticator.unlock();
    }
}
