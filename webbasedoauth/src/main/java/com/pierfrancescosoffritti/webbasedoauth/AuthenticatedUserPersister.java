package com.pierfrancescosoffritti.webbasedoauth;

/**
 * Every class that want to implement the persistence of the tokens must implement this interface.
 */
public interface AuthenticatedUserPersister {
    void persistUser(AuthenticatedUser authenticatedUser);
    void loadUser(AuthenticatedUser authenticatedUser);
}
