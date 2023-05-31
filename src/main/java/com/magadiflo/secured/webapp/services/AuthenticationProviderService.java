package com.magadiflo.secured.webapp.services;

import com.magadiflo.secured.webapp.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    private JpaUserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SCryptPasswordEncoder sCryptPasswordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        CustomUserDetails customUserDetails = this.userDetailsService.loadUserByUsername(username);

        return switch (customUserDetails.getUser().getAlgorithm()) {
            case BCRYPT -> this.checkPassword(customUserDetails, password, this.bCryptPasswordEncoder);
            case SCRYPT -> this.checkPassword(customUserDetails, password, this.sCryptPasswordEncoder);
        };
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication checkPassword(CustomUserDetails userDetails, String rawPassword, PasswordEncoder encoder) {
        if (encoder.matches(rawPassword, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Credenciales incorrectos! (el password no hace match)");
        }
    }
}
