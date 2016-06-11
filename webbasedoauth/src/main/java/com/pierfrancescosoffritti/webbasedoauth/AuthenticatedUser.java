package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.IntDef;

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
    private String expiresIn;
    private Date tokenAcquisitionTime;

    private AuthenticatedUserPersister persister;

    AuthenticatedUser(AuthenticatedUserPersister persister) {
        this.persister = persister;

        this.persister.loadUser(this);
    }

    public void init(String accessToken, String refreshToken, String expiresIn, Date tokenAcquireTime, @AuthStatus int authStatus) {
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

    public String getExpiresIn() {
        return expiresIn;
    }

    public Date getTokenAcquisitionTime() {
        return tokenAcquisitionTime;
    }

    @AuthStatus int getAuthStatus() {
        if(authStatus == NOT_AUTHENTICATED)
            return authStatus;

        // the access token is refreshed offset seconds before expiring
        long currentTime = new Date().getTime() / 1000;
        long tokenAcquiredTime = tokenAcquisitionTime.getTime() / 1000;
        int expireTime = Integer.parseInt(expiresIn);
        int offset = 600; // 10 minutes

        if(currentTime - tokenAcquiredTime >= expireTime - offset) {
            authStatus = TOKEN_EXPIRED;

            accessToken = null;
            expiresIn = null;
            tokenAcquisitionTime = null;
        }

        return authStatus;
    }

    void authenticate(String accessToken, String refreshToken, String expiresIn, Date tokenAcquireTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = tokenAcquireTime;

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    void authenticate(String accessToken, String refreshToken, String expiresIn) {
        authenticate(accessToken, refreshToken, expiresIn, new Date());
    }

    void setNewAccessToken(String accessToken, String expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = new Date();

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    void remove() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = null;

        this.authStatus = NOT_AUTHENTICATED;

        persister.persistUser(this);
    }
}
