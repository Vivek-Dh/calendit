package com.vivek.calendit.configuration.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {
  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_USER");
    Map<String,Object> attributes = new HashMap<>();
    attributes.put("email", "mockuser@gmail.com");
    attributes.put("name", "vivek");
    OAuth2User principal = new DefaultOAuth2User(roles,attributes,"name");
    Authentication auth = new OAuth2AuthenticationToken(principal, roles, "google");
    context.setAuthentication(auth);
    return context;
  }
}
