package ke.co.jodam.insurance.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    JwtAuthenticationFilter.class
            );

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String requestUri =
                request.getRequestURI();

        /*
         * The M-Pesa callback comes from Safaricom and does not
         * contain a JWT. Explicitly exclude it from JWT processing.
         */
        return HttpMethod.POST.matches(
                request.getMethod()
        )
                && "/api/payments/mpesa/callback"
                .equals(
                        requestUri
                );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader(
                        "Authorization"
                );

        /*
         * No JWT was supplied.
         *
         * Continue without authentication and allow Spring Security
         * to apply the authorization rules for the endpoint.
         */
        if (authHeader == null
                || !authHeader.startsWith(
                "Bearer "
        )) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        try {

            final String jwt =
                    authHeader.substring(7);

            final String username =
                    jwtService.extractUsername(
                            jwt
                    );

            if (username == null) {

                logger.warn(
                        "JWT authentication failed: no username found for {} {}",
                        request.getMethod(),
                        request.getRequestURI()
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(
                                    username
                            );

            if (!jwtService.isTokenValid(
                    jwt,
                    userDetails
            )) {

                logger.warn(
                        "JWT authentication failed: token validation failed for user {}",
                        username
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(
                                    request
                            )
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

            logger.debug(
                    "JWT authentication successful for user {} with authorities {}",
                    username,
                    userDetails.getAuthorities()
            );

        } catch (Exception exception) {

            logger.error(
                    "JWT authentication failed for {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    exception.getMessage(),
                    exception
            );
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}