package com.pierfrancescosoffritti.webbasedoauth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Implementation of {@link CredentialPersister} based on {@link SharedPreferences}
 */
public class SharedPreferencesCredentialPersister implements CredentialPersister {
    private static final String SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_AUTHENTICATED_USER_PERSISTER";
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY";
    private static final String EXPIRES_IN_TOKEN_KEY = "EXPIRES_IN_TOKEN_KEY";
    private static final String TOKEN_ACQUIRE_TIME_KEY = "TOKEN_ACQUIRE_TIME_KEY";
    private static final String AUTH_STATUS_KEY = "AUTH_STATUS_KEY";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesCredentialPersister(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void persistUser(CredentialStore credentialStore) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_KEY, credentialStore.getAccessToken())
                .putString(REFRESH_TOKEN_KEY, credentialStore.getRefreshToken())
                .putInt(EXPIRES_IN_TOKEN_KEY, credentialStore.getExpiresIn())
                .putString(TOKEN_ACQUIRE_TIME_KEY, credentialStore.getTokenAcquisitionTime().getTime()+"")
                .putInt(AUTH_STATUS_KEY, credentialStore.getAuthStatus())
                .commit();
    }

    @Override
    public void loadUser(CredentialStore credentialStore) {
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
        String refreshToken = sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
        int expiresIn = sharedPreferences.getInt(EXPIRES_IN_TOKEN_KEY, -1);
        Date tokenAcquireTime = new Date(Long.parseLong(sharedPreferences.getString(TOKEN_ACQUIRE_TIME_KEY, "0")));
        @CredentialStore.AuthStatus int authStatus = sharedPreferences.getInt(AUTH_STATUS_KEY, CredentialStore.NOT_AUTHENTICATED);

        credentialStore.init(accessToken, refreshToken, expiresIn, tokenAcquireTime, authStatus);
    }
}
