package com.pierfrancescosoffritti.webbasedoauth;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An AsyncTask that uses the refresh token to get a valid access token
 */
class RefreshTokenTask extends AsyncTask<String, String, JSONObject> {
    private CredentialStore credentialStore;
    private Authenticator authenticator;

    public RefreshTokenTask(Authenticator authenticator, CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
        this.authenticator = authenticator;
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        JSONObject json = null;
        try {
            json = AuthorizationIO.refreshAccessToken(args[0], args[1], args[2], credentialStore.getRefreshToken(), args[3]);
        } catch (RuntimeException e) {
            e.printStackTrace();
            credentialStore.clear();
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        if (json != null){
            try {
                String accessToken = json.getString("access_token");
                String expireIn = json.getString("expires_in");

                credentialStore.setNewAccessToken(accessToken, Integer.parseInt(expireIn));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        authenticator.unlock();
    }
}
