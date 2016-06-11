package com.pierfrancescosoffritti.webbasedoauth;

/**
 * Created by  Pierfrancesco on 11/06/2016.
 */
public interface AuthenticatedUserPersister {
    void persistUser(AuthenticatedUser authenticatedUser);
    void loadUser(AuthenticatedUser authenticatedUser);
}
