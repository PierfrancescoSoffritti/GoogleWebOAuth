package com.pierfrancescosoffritti.webbasedoauth;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CredentialStoreTest {

    private CredentialStore credentialStore;

    private String accessToken = "mock_access_token";
    private String refreshToken = "mock_refresh_token";
    private int expiresIn = 1600;
    private Date acquisitionTime = new Date();

    public CredentialStoreTest()  {
        credentialStore = new CredentialStore(new CredentialPersister() {
            @Override
            public void persistUser(CredentialStore credentialStore) {
            }

            @Override
            public void loadUser(CredentialStore credentialStore) {
            }
        });
    }

    @Test
    public void testInit() throws Exception {
        credentialStore.init(accessToken, refreshToken, expiresIn, acquisitionTime, CredentialStore.AUTHENTICATED);

        assertEquals(credentialStore.getAccessToken(), accessToken);
        assertEquals(credentialStore.getRefreshToken(), refreshToken);
        assertEquals(credentialStore.getExpiresIn(), expiresIn);
        assertEquals(credentialStore.getTokenAcquisitionTime(), acquisitionTime);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.AUTHENTICATED);

        try {
            credentialStore.init(null, refreshToken, expiresIn, acquisitionTime, CredentialStore.AUTHENTICATED);
            assertTrue(false);
        } catch (Exception ignored) {
        }

        credentialStore.init(null, refreshToken, expiresIn, acquisitionTime, CredentialStore.NOT_AUTHENTICATED);
    }

    @Test
    public void testGetAuthStatus() throws Exception {
        credentialStore.init(accessToken, refreshToken, expiresIn, acquisitionTime, CredentialStore.AUTHENTICATED);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.AUTHENTICATED);

        credentialStore.init(accessToken, refreshToken, 0, acquisitionTime, CredentialStore.NOT_AUTHENTICATED);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.NOT_AUTHENTICATED);

        credentialStore.init(accessToken, refreshToken, 0, acquisitionTime, CredentialStore.AUTHENTICATED);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.TOKEN_EXPIRED);

        credentialStore.init(accessToken, refreshToken, 601, acquisitionTime, CredentialStore.AUTHENTICATED);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.AUTHENTICATED);

        credentialStore.init(accessToken, refreshToken, 500, acquisitionTime, CredentialStore.AUTHENTICATED);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.TOKEN_EXPIRED);
        assertNull(credentialStore.getAccessToken());
        assertTrue(credentialStore.getExpiresIn() < 0);
        assertNull(credentialStore.getTokenAcquisitionTime());
        assertNotNull(credentialStore.getRefreshToken());
    }

    @Test
    public void testAuthenticate() throws Exception {
        credentialStore.authenticate(accessToken, refreshToken, expiresIn, acquisitionTime);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.AUTHENTICATED);

        try {
            credentialStore.authenticate("", refreshToken, expiresIn, acquisitionTime);
            assertTrue(false);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testSetNewAccessToken() throws Exception {
        credentialStore.authenticate(accessToken, refreshToken, expiresIn, new Date(1000));
        credentialStore.setNewAccessToken(accessToken, expiresIn);
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.AUTHENTICATED);
        assertTrue(credentialStore.getTokenAcquisitionTime().getTime() != new Date(1000).getTime());

        try {
            credentialStore.setNewAccessToken("", expiresIn);
            assertTrue(false);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testClear() throws Exception {
        credentialStore.init(accessToken, refreshToken, expiresIn, acquisitionTime, CredentialStore.AUTHENTICATED);

        credentialStore.clear();
        assertEquals(credentialStore.getAuthStatus(), CredentialStore.NOT_AUTHENTICATED);
        assertEquals(credentialStore.getExpiresIn(), -1);
        assertNull(credentialStore.getAccessToken());
        assertNull(credentialStore.getRefreshToken());
        assertNull(credentialStore.getTokenAcquisitionTime());
    }
}