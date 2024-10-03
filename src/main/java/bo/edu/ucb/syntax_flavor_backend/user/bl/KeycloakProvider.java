package bo.edu.ucb.syntax_flavor_backend.user.bl;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;

@Configuration
public class KeycloakProvider {

    Logger LOGGER = LoggerFactory.getLogger(KeycloakProvider.class);

    @Value("${spring.keycloak.auth-server-url}")
    private String SERVER_URL;

    @Value("${spring.keycloak.realm}")
    private String REALM;

    @Value("${spring.keycloak.resource}")
    private String CLIENT_ID;

    @Value("${spring.keycloak.credentials.secret}")
    private String CLIENT_SECRET;

    private Keycloak keycloak = null;

    @PostConstruct
    public void initializeKeycloak() {
        LOGGER.info("Initializing Keycloak instance with server URL: {}", SERVER_URL);
        keycloak = KeycloakBuilder.builder()
            .serverUrl(SERVER_URL)
            .realm(REALM)
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build();
    }

    public Keycloak getInstance() {
        if (keycloak == null) {
            initializeKeycloak();
        }
        // LOGGER.info("Access Token: " + keycloak.tokenManager().getAccessTokenString());
        return keycloak;
    }

    public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        LOGGER.info("Creating Keycloak instance with username and password\n" +
                "Server URL: " + SERVER_URL + "\n" +
                "Realm: " + REALM + "\n" +
                "Client ID: " + CLIENT_ID + "\n" +
                "Client Secret: " + CLIENT_SECRET + "\n" +
                "Username: " + username + "\n" +
                "Password: [PROTECTED]\n");

        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD);
    }
}
