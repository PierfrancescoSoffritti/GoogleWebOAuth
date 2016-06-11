package com.pierfrancescosoffritti.webbasedoauth;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * An AsyncTask that exchanges the authorization code for the access token and the refresh token
 */
class GetTokensTask extends AsyncTask<String, String, JSONObject> {
    private WeakReference<Context> context;

    private ProgressDialog progressDialog;

    private AuthenticatedUser authenticatedUser;
    private Authenticator authenticator;

    public GetTokensTask(Context context, Authenticator authenticator , AuthenticatedUser authenticatedUser) {
        this.context = new WeakReference<>(context);
        this.authenticatedUser = authenticatedUser;

        this.authenticator = authenticator;
    }

    @Override
    protected void onPreExecute() {
        if(context.get() == null)
            return;

        progressDialog = new ProgressDialog(context.get());
        progressDialog.setMessage(context.get().getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        JSONObject json = null;
        try {
            json = AuthorizationIO.exchangeAuthorizationCode(args[0], args[1], args[2], args[3], args[4], args[5]);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        if(progressDialog != null)
            progressDialog.dismiss();

        if (json != null){
            try {

                String accessToken = json.getString("access_token");
                String expireIn = json.getString("expires_in");
                String refreshToken = json.getString("refresh_token");

                authenticatedUser.authenticate(accessToken, refreshToken, Integer.parseInt(expireIn));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        authenticator.unlock();
    }
}