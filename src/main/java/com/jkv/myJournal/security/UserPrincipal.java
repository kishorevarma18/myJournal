package com.jkv.myJournal.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jkv.myJournal.entity.UserEntity;

/**
 * UserPrincipal acts as a bridge adapter between your domain's database entity (UserEntity) 
 * and Spring Security's native authentication system (UserDetails).
 * By wrapping UserEntity, it translates your custom data into a format Spring Security understands.
 */
public class UserPrincipal implements UserDetails {

    // The underlying database entity holding the raw user data.
    private final UserEntity userEntity;

    // Constructor to initialize this adapter wrapper with the logged-in user's database records.
    public UserPrincipal(UserEntity userEntity){
        this.userEntity = userEntity;
    }

    /**
     * LOGIC EXPLANATION FOR ROLES METHOD:
     * Spring Security expects permissions/roles to be returned as a Collection of GrantedAuthority objects.
     * This method converts your custom roles (usually Strings like "USER" or "ADMIN") into Spring's required format.
     * 
     * Breakdown of the pipeline:
     * 1. userEntity.getRoles().stream() -> Takes the collection of roles from the database entity and turns it into a Stream for processing.
     * 2. .map(role -> new SimpleGrantedAuthority("Role_" + role)) -> Transforms (maps) each raw string role into a Spring-compliant 
     *    'SimpleGrantedAuthority' object. It prepends "Role_" (Note: Spring standard usually uses "ROLE_" in uppercase) to fit naming conventions.
     * 3. .collect(Collectors.toList()) -> Gathers all the newly created GrantedAuthority objects back into a standard Java List.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("Role" + role))
            .collect(Collectors.toList());
    }

    /**
     * Returns the hashed password of the user fetched from the database entity.
     * Spring Security will call this method internally to compare it against the password entered at login.
     */
    @Override
    public String getPassword() {
        return userEntity.getUserPassword();
    }

    /**
     * Returns the unique username of the user fetched from the database entity.
     * This serves as the primary identifier for the user during authentication checks.
     */
    @Override
    public String getUsername() {
        return userEntity.getUserName();
    }

    /**
     * Determines if the user's account has expired due to time or inactivity.
     * Returning 'true' means the account is valid and active indefinitely.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Determines if the user's account has been locked (e.g., due to too many failed login attempts).
     * Returning 'true' means the account is open and unlocked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Determines if the user's authentication credentials (password) have expired.
     * Returning 'true' means the password remains valid and does not force an immediate reset.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Determines if the user account is enabled or disabled administratively.
     * Returning 'true' means the user is fully allowed to authenticate and log in.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}