package com.distributed.miraeasset.security;

import com.distributed.miraeasset.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    private final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/accounts/authenticate",
            "/api/accounts/register",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
//        if (isPublicPath(request)) {
//            chain.doFilter(request, response);
//            return;
//        }

        final String requestTokenHeader = request.getHeader(AUTHORIZATION);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith(PREFIX_TOKEN)) {
            jwtToken = requestTokenHeader.substring(PREFIX_TOKEN.length());
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("[ERROR][JwtRequestFilter] [doFilterInternal] Unable to get JWT Token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            } catch (ExpiredJwtException e) {
                log.error("[ERROR][JwtRequestFilter] [doFilterInternal] JWT Token has expired", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
            } catch (Exception e) {
                log.error("[ERROR][JwtRequestFilter] Error processing JWT token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error processing JWT token");
                return;
            }
        } else {
            log.warn("[WARN][JwtRequestFilter] [doFilterInternal] JWT Token does not begin start with Bearer string");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                String role = jwtTokenUtil.extractRole(jwtToken);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("[INFO][JwtRequestFilter] Authentication set for user: {}", username);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        return PUBLIC_PATHS.stream().anyMatch(path -> request.getRequestURI().contains(path));
    }
}
