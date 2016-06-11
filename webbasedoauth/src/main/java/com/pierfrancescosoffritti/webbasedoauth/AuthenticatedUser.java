package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * This class contains the access token, the refresh token and some utilities to know if the access token has expired.
 * This class uses an {@link AuthenticatedUserPersister} to persist the user data.
 */
class AuthenticatedUser {
    final static int NOT_AUTHENTICATED = 0;
    final static int TOKEN_EXPIRED = 1;
    final static int AUTHENTICATED = 2;

    @IntDef({NOT_AUTHENTICATED, TOKEN_EXPIRED, AUTHENTICATED})
    @Retention(RetentionPolicy.SOURCE)
    @interface AuthStatus {}

    private @AuthStatus int authStatus = NOT_AUTHENTICATED;

    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private Date tokenAcquisitionTime;

    private AuthenticatedUserPersister persister;

    AuthenticatedUser(AuthenticatedUserPersister persister) {
        this.persister = persister;

        this.persister.loadUser(this);
    }

    public void init(String accessToken, String refreshToken, int expiresIn, Date tokenAcquireTime, @AuthStatus int authStatus) {
        if((accessToken == null || refreshToken == null || tokenAcquireTime == null) && authStatus != NOT_AUTHENTICATED)
            throw new IllegalArgumentException("(accessToken == null || refreshToken == null || tokenAcquireTime == null) && authStatus != NOT_AUTHENTICATED");

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenAcquisitionTime = tokenAcquireTime;
        this.authStatus = authStatus;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public Date getTokenAcquisitionTime() {
        return tokenAcquisitionTime;
    }

    /**
     * Other than returning the auth status, this method is responsible for changing the status to expired if expiresIn - 600 seconds are passed
     * @return the status of the authentication. A value from {@link AuthStatus}
     */
    @AuthStatus int getAuthStatus() {
        if(authStatus == NOT_AUTHENTICATED)
            return authStatus;

        // the access token is refreshed offset seconds before expiring
        long currentTime = new Date().getTime() / 1000;
        long tokenAcquiredTime = tokenAcquisitionTime.getTime() / 1000;
        int offset = 600; // 10 minutes

        if(currentTime - tokenAcquiredTime >= expiresIn - offset) {
            authStatus = TOKEN_EXPIRED;

            accessToken = null;
            expiresIn = -1;
            tokenAcquisitionTime = null;
        }

        return authStatus;
    }

    void authenticate(@NonNull String accessToken, @NonNull String refreshToken, int expiresIn, @NonNull Date tokenAcquisitionTime) {
        if(accessToken.isEmpty() || refreshToken.isEmpty())
            throw new IllegalArgumentException("accessToken.isEmpty() || refreshToken.isEmpty()");

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = tokenAcquisitionTime;

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    void authenticate(String accessToken, String refreshToken, int expiresIn) {
        authenticate(accessToken, refreshToken, expiresIn, new Date());
    }

    void setNewAccessToken(@NonNull String accessToken, int expiresIn) {
        if(accessToken.isEmpty())
            throw new IllegalArgumentException("accessToken.isEmpty()");

        this.accessToken = accessToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = new Date();

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    void remove() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = -1;
        this.tokenAcquisitionTime = null;

        this.authStatus = NOT_AUTHENTICATED;

        persister.persistUser(this);
    }
}
