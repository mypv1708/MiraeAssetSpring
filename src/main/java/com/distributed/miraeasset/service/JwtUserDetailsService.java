package com.distributed.miraeasset.service;

import com.distributed.miraeasset.entity.Account;
import com.distributed.miraeasset.repository.AccountRepositoryServer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final AccountRepositoryServer repository;

    public JwtUserDetailsService(AccountRepositoryServer repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByEmail(username);
        if (account != null && account.getEmail().equals(username)) {
            return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("[ERROR] [JwtUserDetailsService] loadUserByUsername: Email: " + username + " not found!");
        }
    }
}
