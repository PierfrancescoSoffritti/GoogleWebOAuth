package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by  Pierfrancesco on 10/06/2016.
 */
public class AuthenticatedUser {
    protected final static int NOT_AUTHENTICATED = 0;
    protected final static int TOKEN_EXPIRED = 1;
    protected final static int AUTHENTICATED = 2;

    @IntDef({NOT_AUTHENTICATED, TOKEN_EXPIRED, AUTHENTICATED})
    @Retention(RetentionPolicy.SOURCE)
    @interface AuthStatus {}

    private @AuthStatus int authStatus = NOT_AUTHENTICATED;

    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private Date tokenAcquireTime;

    private AuthenticatedUserPersister persister;

    protected AuthenticatedUser(AuthenticatedUserPersister persister) {
        this.persister = persister;

        this.persister.loadUser(this);
    }

    public void init(String accessToken, String refreshToken, String expiresIn, Date tokenAcquireTime, @AuthStatus int authStatus) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenAcquireTime = tokenAcquireTime;
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

    public Date getTokenAcquireTime() {
        return tokenAcquireTime;
    }

    protected  @AuthStatus int getAuthStatus() {
        if(authStatus == NOT_AUTHENTICATED)
            return authStatus;

        long currentTime = new Date().getTime() / 1000;
        long tokenAcquiredTime = tokenAcquireTime.getTime() / 1000;
        int expireTime = Integer.parseInt(expiresIn);
        int offset = 600; // 10 minutes

        if(currentTime - tokenAcquiredTime >= expireTime - offset) {
            authStatus = TOKEN_EXPIRED;

            accessToken = null;
            expiresIn = null;
            tokenAcquireTime = null;
        }

        return authStatus;
    }

    protected void authenticate(String accessToken, String refreshToken, String expiresIn, Date tokenAcquireTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;

        this.tokenAcquireTime = tokenAcquireTime;

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    protected void authenticate(String accessToken, String refreshToken, String expiresIn) {
        authenticate(accessToken, refreshToken, expiresIn, new Date());
    }

    protected void refreshAccessToken(String accessToken, String expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;

        this.tokenAcquireTime = new Date();

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    protected void remove() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = null;

        this.authStatus = NOT_AUTHENTICATED;

        persister.persistUser(this);
    }
}
