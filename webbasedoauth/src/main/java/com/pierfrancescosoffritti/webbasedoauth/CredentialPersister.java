package com.pierfrancescosoffritti.webbasedoauth;

/**
 * Every class that want to implement the persistence of the tokens must implement this interface.
 */
public interface CredentialPersister {
    void persistUser(CredentialStore credentialStore);
    void loadUser(CredentialStore credentialStore);
}
