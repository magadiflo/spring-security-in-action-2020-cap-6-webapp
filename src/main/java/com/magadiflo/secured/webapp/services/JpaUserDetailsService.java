package com.magadiflo.secured.webapp.services;

import com.magadiflo.secured.webapp.entities.User;
import com.magadiflo.secured.webapp.model.CustomUserDetails;
import com.magadiflo.secured.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> usernameNotFoundExceptionSupplier = () -> new UsernameNotFoundException("Problemas durante la autenticación!");
        User user = this.userRepository.findUserByUsername(username).orElseThrow(usernameNotFoundExceptionSupplier);
        return new CustomUserDetails(user);
    }
}
