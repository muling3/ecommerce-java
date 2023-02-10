package com.muling3.ecommerce.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling3.ecommerce.service.CustomerDetailsService;
import com.muling3.ecommerce.service.utils.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired private CustomerDetailsService customerDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // get the authorization header from request
        String authHeader = request.getHeader("Authorization");

        String name = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // split the header and get the jwt token
            token = authHeader.substring("Bearer ".length());

            //extract name from the header
            name = jwtService.extractUsername(token);
        }

        //get the user details if name is not null and if there is any authenticated user
        if(name != null && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails customerDetails = customerDetailsService.loadUserByUsername(name);

            //validate the token
            if (jwtService.validateToken(token, customerDetails)) {
                //update security context holder
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customerDetails, null, customerDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
