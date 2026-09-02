package ke.co.jodam.insurance.security;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {

        return authenticationConfiguration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized"
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Allow Spring to process internal error dispatches
                         * without requiring JWT authentication.
                         */
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        )
                        .permitAll()

                        /*
                         * Public authentication endpoints.
                         */
                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()

                        /*
                         * Explicitly allow the Spring error endpoint.
                         */
                        .requestMatchers(
                                "/error"
                        )
                        .permitAll()

                        /*
                         * M-PESA callback endpoints.
                         *
                         * Safaricom callback requests do not contain
                         * a customer JWT token.
                         */
                        .requestMatchers(
                                "/api/payments/mpesa/**"
                        )
                        .permitAll()

                        /*
                         * Customer payment operations.
                         */
                        .requestMatchers(
                                "/api/payments/**"
                        )
                        .hasAuthority("CUSTOMER")

                        /*
                         * Administrator-only endpoints.
                         */
                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasAuthority("ADMIN")

                        /*
                         * Administrator-only staff management endpoints.
                         */
                        .requestMatchers(
                                "/api/staff/**"
                        )
                        .hasAuthority("ADMIN")

                        /*
                         * Agent-only endpoints.
                         */
                        .requestMatchers(
                                "/api/agents/**"
                        )
                        .hasAuthority("AGENT")

                        /*
                         * All remaining endpoints require authentication.
                         */
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
