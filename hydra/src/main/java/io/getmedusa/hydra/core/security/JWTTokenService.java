package io.getmedusa.hydra.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import io.getmedusa.hydra.core.domain.HydraUser;
import io.getmedusa.hydra.core.routing.DynamicRouteProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JWTTokenService {

    private final String communicationPublicKey;

    private final String communicationPrivateKey;

    private Algorithm algorithm;

    private static String publicKeyAsString;
    private RSAPublicKey publicKey;

    private final DynamicRouteProvider dynamicRouteProvider;
    private final WebClient client;

    public JWTTokenService(@Value("${medusa.hydra.secret.public}") String communicationPublicKey,
                           @Value("${medusa.hydra.secret.private}") String communicationPrivateKey,
                           DynamicRouteProvider dynamicRouteProvider,
                           WebClient client) {
        this.dynamicRouteProvider = dynamicRouteProvider;
        this.client = client;
        this.communicationPrivateKey = communicationPrivateKey;
        this.communicationPublicKey = communicationPublicKey;

        cycleKeys();
    }

    /**
     * Renews the internal RSA keypair. Can be called at any time.
     * But be aware - every time you cycle, users need to re-login.
     */
    public void cycleKeys() {
        System.out.println("Cycling keypair ...");

        final KeyPair pair = generateKeyPair();
        final RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        setPublicKey(getKey(publicKey.getEncoded()));

        System.out.println("Keypair generated");

        sendPublicKeyToAllConnected();
    }

    private static void setPublicKey(String key) {
        publicKeyAsString = key;
    }

    private void sendPublicKeyToAllConnected() {
        for(ActiveService activeService : dynamicRouteProvider.getActiveServices()) {
            sendPublicKeyToService(activeService);
        }
    }

    public void sendPublicKeyToService(ActiveService activeService) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.post();
        String uri = activeService.getWebProtocol() + "://" + activeService.getHost() + ":" + activeService.getPort() + "/h/public-key-update/_publicKey_"
                .replace("_publicKey_", communicationPublicKey);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(Map.of("k", publicKeyAsString));

        headersSpec.exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(String.class);
            } else {
                return response.createException().flatMap(Mono::error);
            }
        }).subscribe();
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not generate RSA keypair", e);
        }
    }

    private String getKey(byte[] encoded) {
        return Base64.getEncoder().encodeToString(encoded);
    }

    public String generateToken(HydraUser user) {
        try {
            final JWTCreator.Builder builder = JWT.create()
                    .withIssuer("hydra")
                    .withExpiresAt(Date.from(ZonedDateTime.now().plusHours(1).toInstant()))
                    .withClaim("username", user.getUsername())
                    .withClaim("userId", user.getId())
                    .withArrayClaim("roles", user.getRoles().toArray(new String[0]));

            if(null != user.getAdditionalMetadata()) {
                for(Map.Entry<String, String> entrySet : user.getAdditionalMetadata().entrySet()) {
                    builder.withClaim(entrySet.getKey(), entrySet.getValue());
                }
            }

            return builder.sign(algorithm);
        } catch (JWTCreationException e){
            throw new IllegalStateException("Could not create JWT token from KeyPair", e);
        }
    }

    public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null))
                    .withIssuer("hydra")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if(jwt != null) return true;
        } catch (Exception exception){
            return false;
        }
        return false;
    }

}