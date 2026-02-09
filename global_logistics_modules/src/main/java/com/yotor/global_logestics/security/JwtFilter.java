package com.yotor.global_logestics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final UUID userExternalId;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userExternalId = this.jwtService.extractUserExternalId(jwt);

        if (userExternalId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UUID userId = jwtService.extractUserExternalId(jwt);
            String role = jwtService.extractRole(jwt);

            if (this.jwtService.isTokenValid(jwt, userId)) {
                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(userId, role),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
