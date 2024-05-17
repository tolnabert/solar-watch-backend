package com.codecool.solarwatch.security;

import com.codecool.solarwatch.model.entity.Client;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // checks for our B if we have a user for that forexample login request
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Autowired
    public UserDetailsServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        for (Role role : client.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.name()));
        }

        return new User(client.getUsername(), client.getPassword(), roles);
    }
}

