package com.pierfrancescosoffritti.webbasedoauth;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * A Thread that exchanges the authorization code for the access token and the refresh token.
 * <br/><br/>
 * This functionality isn't implemented with an AsyncTask because this task is called from {@link Authenticator#getAccessToken()}, that blocks the calling thread.
 * <br/>
 * If both this class and the thread calling {@link Authenticator#getAccessToken()} where to be AsyncTasks, the calling AsyncTask would be blocked, waiting for this task to terminate,
 * but due to the sequentiality of AsyncTasks, this task would have to wait the termination of the first one.
 */
class GetTokensTask extends Thread {
    private WeakReference<Context> context;

    private ProgressDialog progressDialog;

    private CredentialStore credentialStore;
    private Authenticator authenticator;

    private String[] url;

    private Handler handler;

    GetTokensTask(@NonNull Context context, @NonNull Authenticator authenticator, @NonNull CredentialStore credentialStore,
                  @NonNull String tokenURL, @NonNull String authorizationCode, @NonNull String clientID, @Nullable String clientSecret, @NonNull String redirectURL, @NonNull String grantType) {
        this.context = new WeakReference<>(context);
        this.credentialStore = credentialStore;

        this.authenticator = authenticator;
        
        this.url = new String[] {tokenURL, authorizationCode, clientID, clientSecret, redirectURL, grantType};

        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        // show progress dialog
        if(context.get() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(context.get());
                    progressDialog.setMessage(context.get().getString(R.string.loading));
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
        }

        try {
            JSONObject json = AuthorizationIO.exchangeAuthorizationCode(url[0], url[1], url[2], url[3], url[4], url[5]);

            if (json != null){
                try {

                    String accessToken = json.getString("access_token");
                    String expireIn = json.getString("expires_in");
                    String refreshToken = json.getString("refresh_token");

                    credentialStore.authenticate(accessToken, refreshToken, Integer.parseInt(expireIn));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException("can't login", e);
        } finally {
            if(progressDialog != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });

            authenticator.unlock();
        }
    }
}