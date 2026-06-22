package com.jkv.myjournal.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String jwt = parseJwt(request);
            if(jwt!=null && jwtService.validateJwt(jwt)){
                String userName = jwtService.getUserNameFromJwt(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                // 1. Create a fully authenticated token containing the user identity (UserPrincipal) and their permissions/roles.
                // By passing 'userDetails.getAuthorities()' into the constructor, Spring Security flags this token 
                // as 'authenticated = true', verifying that identity checks are fully complete.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 2. Build and attach request-specific metadata (such as the client's remote IP address and HTTP session ID) 
                // directly to the authentication token object. This provides extra telemetry for auditing and access control rules.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 3. Save the fully built authentication token inside Spring Security's thread-local storage.
                // For the remainder of this HTTP request's lifecycle, any controller or business service bean 
                // can instantly read or authorize the logged-in user context without needing another database lookup.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(Exception e){
            log.error("can not set User authentication: {}",e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth!=null && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return null;
    }

}
