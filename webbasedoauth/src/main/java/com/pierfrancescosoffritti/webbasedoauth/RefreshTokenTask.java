package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A Thread that uses the refresh token to get a valid access token
 * <br/><br/>
 * This functionality isn't implemented with an AsyncTask because this task is called from {@link Authenticator#getAccessToken()}, that blocks the calling thread.
 * <br/>
 * If both this class and the thread calling {@link Authenticator#getAccessToken()} where to be AsyncTasks, the calling AsyncTask would be blocked, waiting for this task to terminate,
 * but due to the sequential execution of multiple AsyncTasks, this task would have to wait the termination of the first one.
 */
class RefreshTokenTask extends Thread {
    private CredentialStore credentialStore;
    private Authenticator authenticator;

    private String[] url;

    RefreshTokenTask(@NonNull Authenticator authenticator, @NonNull CredentialStore credentialStore,
                     @NonNull String tokenURL, @NonNull String clientID, @Nullable String clientSecret, @NonNull String grantType) {
        this.credentialStore = credentialStore;
        this.authenticator = authenticator;

        this.url = new String[] {tokenURL, clientID, clientSecret, grantType};
    }

    @Override
    public void run() {
        try {
            JSONObject json = AuthorizationIO.refreshAccessToken(url[0], url[1], url[2], credentialStore.getRefreshToken(), url[3]);

            String accessToken = json.getString("access_token");
            String expireIn = json.getString("expires_in");

            credentialStore.setNewAccessToken(accessToken, Integer.parseInt(expireIn));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            authenticator.unlock();
        }
    }
}
