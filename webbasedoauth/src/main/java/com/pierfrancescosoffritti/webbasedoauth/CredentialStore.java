package com.pierfrancescosoffritti.webbasedoauth;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * This class contains the access token, the refresh token and some utilities to know if the access token has expired.
 * This class uses an {@link CredentialPersister} to persist the user data.
 */
public class CredentialStore {
    public final static int NOT_AUTHENTICATED = 0;
    public final static int TOKEN_EXPIRED = 1;
    public final static int AUTHENTICATED = 2;

    @IntDef({NOT_AUTHENTICATED, TOKEN_EXPIRED, AUTHENTICATED})
    @Retention(RetentionPolicy.SOURCE)
    @interface AuthStatus {}

    private @AuthStatus int authStatus = NOT_AUTHENTICATED;

    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private Date tokenAcquisitionTime;

    private CredentialPersister persister;

    CredentialStore(CredentialPersister persister) {
        this.persister = persister;

        this.persister.loadUser(this);
    }

    /**
     * Use this method to initialize the state of this {@link CredentialStore}.
     * <br/><br/>
     * eg. Useful in {@link CredentialPersister#loadUser(CredentialStore)}
     *
     * @param accessToken the access token
     * @param refreshToken the refresh token
     * @param expiresIn duration of the access token
     * @param tokenAcquisitionTime date object representing the time of acquisition of the access token
     * @param authStatus the status of the authentication
     *
     * @throws IllegalArgumentException if accessToken == null || refreshToken == null || tokenAcquisitionTime == null and authStatus != NOT_AUTHENTICATED
     */
    public void init(String accessToken, String refreshToken, int expiresIn, Date tokenAcquisitionTime, @AuthStatus int authStatus) {
        if((accessToken == null || refreshToken == null || tokenAcquisitionTime == null) && authStatus != NOT_AUTHENTICATED)
            throw new IllegalArgumentException("(accessToken == null || refreshToken == null || tokenAcquireTime == null) && authStatus != NOT_AUTHENTICATED");

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenAcquisitionTime = tokenAcquisitionTime;
        this.authStatus = authStatus;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @return the expiration time of the current access token, in seconds.
     */
    public int getExpiresIn() {
        return expiresIn;
    }

    /**
     * @return the time of acquisition of the current access token
     */
    public Date getTokenAcquisitionTime() {
        return tokenAcquisitionTime;
    }

    /**
     * Other than returning the auth status, this method is responsible for changing the status to expired if expiresIn - 600 seconds are passed
     * @return the status of the authentication. A value from {@link AuthStatus}
     */
    synchronized @AuthStatus int getAuthStatus() {
        if(authStatus == NOT_AUTHENTICATED)
            return authStatus;

        // the access token is refreshed offset seconds before expiring
        long currentTimeMillisec = new Date().getTime() / 1000;
        long tokenAcquisitionTimeMillisec = this.tokenAcquisitionTime.getTime() / 1000;
        int offsetMillisec = 600; // 10 minutes

        if(currentTimeMillisec - tokenAcquisitionTimeMillisec >= expiresIn - offsetMillisec)
            authStatus = TOKEN_EXPIRED;

        return authStatus;
    }

    /**
     * Use this method to store new credentials.
     */
    synchronized void authenticate(@NonNull String accessToken, @NonNull String refreshToken, int expiresIn, @NonNull Date tokenAcquisitionTime) {
        if(accessToken.length() == 0 || refreshToken.length() == 0)
            throw new IllegalArgumentException("accessToken.isEmpty() || refreshToken.isEmpty()");

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = tokenAcquisitionTime;

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    /**
     * Use this method to store new credentials.
     */
    synchronized void authenticate(String accessToken, String refreshToken, int expiresIn) {
        authenticate(accessToken, refreshToken, expiresIn, new Date());
    }

    /**
     * Use this method to set a new access token.
     */
    synchronized void setNewAccessToken(@NonNull String accessToken, int expiresIn) {
        if(accessToken.length() == 0)
            throw new IllegalArgumentException("accessToken.isEmpty()");

        this.accessToken = accessToken;
        this.expiresIn = expiresIn;

        this.tokenAcquisitionTime = new Date();

        authStatus = AUTHENTICATED;

        persister.persistUser(this);
    }

    /**
     * Use this method to delete all the stored credentials.
     */
    synchronized void clear() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = -1;
        this.tokenAcquisitionTime = new Date(0);

        this.authStatus = NOT_AUTHENTICATED;

        persister.persistUser(this);
    }
}
