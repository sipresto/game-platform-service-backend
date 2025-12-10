package be.kdg.ipj3.shared.infrastructure.security;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
public class ServiceClientCredentials {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public ServiceClientCredentials(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessTokenForService() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("platform-backend")
                .principal("service-account")
                .build();

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null) {
            return authorizedClient.getAccessToken().getTokenValue();
        }
        throw new IllegalStateException("Failed to obtain access token for service");
    }
}