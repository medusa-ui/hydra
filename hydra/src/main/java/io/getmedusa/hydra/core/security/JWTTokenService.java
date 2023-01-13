package io.getmedusa.hydra.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    private Algorithm algorithm;

    public static String publicKeyAsString = null;
    private RSAPublicKey publicKey;

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenService.class);

    public JWTTokenService() {
        cycleKeys();
    }

    /**
     * Renews the internal RSA keypair. Can be called at any time.
     * But be aware - every time you cycle, users need to re-login.
     */
    public void cycleKeys() {
        logger.info("Cycling keypair ...");
        final KeyPair pair = generateKeyPair();
        final RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        publicKeyAsString = getKey(publicKey.getEncoded());
        logger.info("Keypair generated: {}", publicKeyAsString);
        //TODO send public key?
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

    public String getPublicKey() {
        return publicKeyAsString;
    }
}
