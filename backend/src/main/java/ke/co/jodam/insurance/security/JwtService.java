package ke.co.jodam.insurance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;

    private final long jwtExpiration;

    private volatile SecretKey signingKey;

    public JwtService(
            @Value("${security.jwt.secret:}")
            String secretKey,
            @Value("${security.jwt.expiration-ms:3600000}")
            long jwtExpiration
    ) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;

        validateConfiguration();

        this.signingKey = buildSigningKey();
    }

    public String generateToken(
            UserDetails userDetails
    ) {
        Date issuedAt =
                new Date();

        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + jwtExpiration
                );

        return Jwts.builder()
                .subject(
                        userDetails.getUsername()
                )
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(
            String token
    ) {
        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        try {

            final String username =
                    extractUsername(token);

            return username.equals(
                    userDetails.getUsername()
            )
                    && !isTokenExpired(token);

        } catch (RuntimeException exception) {

            return false;
        }
    }

    private boolean isTokenExpired(
            String token
    ) {
        return extractExpiration(token)
                .before(new Date());
    }

    private Date extractExpiration(
            String token
    ) {
        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims =
                Jwts.parser()
                        .verifyWith(
                                getSigningKey()
                        )
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return claimsResolver.apply(
                claims
        );
    }

    private SecretKey getSigningKey() {
        if (signingKey == null) {
            synchronized (this) {
                if (signingKey == null) {
                    signingKey =
                            buildSigningKey();
                }
            }
        }

        return signingKey;
    }

    private SecretKey buildSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        secretKey
                );

        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }

    private void validateConfiguration() {

        if (secretKey == null
                || secretKey.isBlank()) {

            throw new IllegalStateException(
                    "JWT_SECRET is not configured. "
                            + "Set the JWT_SECRET environment variable "
                            + "to a Base64-encoded signing key."
            );
        }

        if (jwtExpiration <= 0) {

            throw new IllegalStateException(
                    "JWT_EXPIRATION_MS must be greater than zero."
            );
        }

        try {

            byte[] keyBytes =
                    Decoders.BASE64.decode(
                            secretKey
                    );

            if (keyBytes.length < 32) {

                throw new IllegalStateException(
                        "JWT_SECRET must decode to at least "
                                + "32 bytes for HS256 signing."
                );
            }

        } catch (IllegalArgumentException exception) {

            throw new IllegalStateException(
                    "JWT_SECRET must be a valid Base64-encoded key.",
                    exception
            );
        }
    }
}