package com.pierfrancescosoffritti.webbasedoauth;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AuthenticatedUserTest {

    private AuthenticatedUser authenticatedUser;

    private String accessToken = "mock_access_token";
    private String refreshToken = "mock_refresh_token";
    private int expiresIn = 1600;
    private Date acquisitionTime = new Date();

    public AuthenticatedUserTest()  {
        authenticatedUser = new AuthenticatedUser(new AuthenticatedUserPersister() {
            @Override
            public void persistUser(AuthenticatedUser authenticatedUser) {
            }

            @Override
            public void loadUser(AuthenticatedUser authenticatedUser) {
            }
        });
    }

    @Test
    public void testInit() throws Exception {
        authenticatedUser.init(accessToken, refreshToken, expiresIn, acquisitionTime, AuthenticatedUser.AUTHENTICATED);

        assertEquals(authenticatedUser.getAccessToken(), accessToken);
        assertEquals(authenticatedUser.getRefreshToken(), refreshToken);
        assertEquals(authenticatedUser.getExpiresIn(), expiresIn);
        assertEquals(authenticatedUser.getTokenAcquisitionTime(), acquisitionTime);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.AUTHENTICATED);

        try {
            authenticatedUser.init(null, refreshToken, expiresIn, acquisitionTime, AuthenticatedUser.AUTHENTICATED);
            assertTrue(false);
        } catch (Exception ignored) {
        }

        authenticatedUser.init(null, refreshToken, expiresIn, acquisitionTime, AuthenticatedUser.NOT_AUTHENTICATED);
    }

    @Test
    public void testGetAuthStatus() throws Exception {
        authenticatedUser.init(accessToken, refreshToken, expiresIn, acquisitionTime, AuthenticatedUser.AUTHENTICATED);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.AUTHENTICATED);

        authenticatedUser.init(accessToken, refreshToken, 0, acquisitionTime, AuthenticatedUser.NOT_AUTHENTICATED);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.NOT_AUTHENTICATED);

        authenticatedUser.init(accessToken, refreshToken, 0, acquisitionTime, AuthenticatedUser.AUTHENTICATED);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.TOKEN_EXPIRED);

        authenticatedUser.init(accessToken, refreshToken, 601, acquisitionTime, AuthenticatedUser.AUTHENTICATED);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.AUTHENTICATED);

        authenticatedUser.init(accessToken, refreshToken, 500, acquisitionTime, AuthenticatedUser.AUTHENTICATED);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.TOKEN_EXPIRED);
        assertNull(authenticatedUser.getAccessToken());
        assertTrue(authenticatedUser.getExpiresIn() < 0);
        assertNull(authenticatedUser.getTokenAcquisitionTime());
        assertNotNull(authenticatedUser.getRefreshToken());
    }

    @Test
    public void testAuthenticate() throws Exception {
        authenticatedUser.authenticate(accessToken, refreshToken, expiresIn, acquisitionTime);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.AUTHENTICATED);

        try {
            authenticatedUser.authenticate("", refreshToken, expiresIn, acquisitionTime);
            assertTrue(false);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testSetNewAccessToken() throws Exception {
        authenticatedUser.authenticate(accessToken, refreshToken, expiresIn, new Date(1000));
        authenticatedUser.setNewAccessToken(accessToken, expiresIn);
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.AUTHENTICATED);
        assertTrue(authenticatedUser.getTokenAcquisitionTime().getTime() != new Date(1000).getTime());

        try {
            authenticatedUser.setNewAccessToken("", expiresIn);
            assertTrue(false);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        authenticatedUser.init(accessToken, refreshToken, expiresIn, acquisitionTime, AuthenticatedUser.AUTHENTICATED);

        authenticatedUser.remove();
        assertEquals(authenticatedUser.getAuthStatus(), AuthenticatedUser.NOT_AUTHENTICATED);
        assertEquals(authenticatedUser.getExpiresIn(), -1);
        assertNull(authenticatedUser.getAccessToken());
        assertNull(authenticatedUser.getRefreshToken());
        assertNull(authenticatedUser.getTokenAcquisitionTime());
    }
}