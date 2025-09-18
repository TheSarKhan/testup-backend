package com.exam.examapp.security.oauth2;

import com.exam.examapp.model.Pack;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record CustomOidcUser(OidcUser delegate, UUID id, String username, String email,
                             String role, Pack pack) implements OidcUser {

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>(delegate.getClaims());
        claims.put("dbUserId", id);
        claims.put("role", role);
        claims.put("username", username);
        claims.put("email", email);
        claims.put("pack", pack);
        return claims;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }
}
