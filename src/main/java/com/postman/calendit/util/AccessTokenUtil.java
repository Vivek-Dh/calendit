package com.postman.calendit.util;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenUtil {
  public String getAccessToken(OAuth2AuthorizedClientService clientService, OAuth2AuthenticationToken oauthToken) {
    OAuth2AuthorizedClient client = clientService
        .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

    String accessToken = client.getAccessToken().getTokenValue();
    return accessToken;
  }
}
