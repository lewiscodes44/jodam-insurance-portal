package ke.co.jodam.insurance.security;

import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(
                        Stream.concat(
                                        // Add the user's roles as authorities
                                        user.getRoles().stream()
                                                .map(role ->
                                                        new SimpleGrantedAuthority(
                                                                role.getName()
                                                        )
                                                ),

                                        // Add the user's permissions as authorities
                                        user.getRoles().stream()
                                                .flatMap(role ->
                                                        role.getPermissions().stream()
                                                )
                                                .map(permission ->
                                                        new SimpleGrantedAuthority(
                                                                permission.getName()
                                                        )
                                                )
                                )
                                .collect(Collectors.toSet())
                )
                .accountLocked(!user.isActive())
                .build();
    }
}