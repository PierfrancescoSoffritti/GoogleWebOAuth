package com.pierfrancescosoffritti.webbasedoauth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by  Pierfrancesco on 11/06/2016.
 */
public class SharedPreferencesAuthenticatedUserPersister implements AuthenticatedUserPersister {
    private static final String SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_AUTHENTICATED_USER_PERSISTER";
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
    private static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY";
    private static final String EXPIRES_IN_TOKEN_KEY = "EXPIRES_IN_TOKEN_KEY";
    private static final String TOKEN_ACQUIRE_TIME_KEY = "TOKEN_ACQUIRE_TIME_KEY";
    private static final String AUTH_STATUS_KEY = "AUTH_STATUS_KEY";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesAuthenticatedUserPersister(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void persistUser(AuthenticatedUser authenticatedUser) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_KEY, authenticatedUser.getAccessToken())
                .putString(REFRESH_TOKEN_KEY, authenticatedUser.getRefreshToken())
                .putString(EXPIRES_IN_TOKEN_KEY, authenticatedUser.getExpiresIn())
                .putString(TOKEN_ACQUIRE_TIME_KEY, authenticatedUser.getTokenAcquisitionTime().getTime()+"")
                .putInt(AUTH_STATUS_KEY, authenticatedUser.getAuthStatus())
                .apply();
    }

    @Override
    public void loadUser(AuthenticatedUser authenticatedUser) {
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
        String refreshToken = sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
        String expiresIn = sharedPreferences.getString(EXPIRES_IN_TOKEN_KEY, null);
        Date tokenAcquireTime = new Date(Long.parseLong(sharedPreferences.getString(TOKEN_ACQUIRE_TIME_KEY, "0")));
        @AuthenticatedUser.AuthStatus int authStatus = sharedPreferences.getInt(AUTH_STATUS_KEY, AuthenticatedUser.NOT_AUTHENTICATED);

        authenticatedUser.init(accessToken, refreshToken, expiresIn, tokenAcquireTime, authStatus);
    }
}
