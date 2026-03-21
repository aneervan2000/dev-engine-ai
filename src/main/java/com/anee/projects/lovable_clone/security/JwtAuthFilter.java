package com.anee.projects.lovable_clone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


/**
 * {@code JwtAuthFilter} is a custom Spring Security filter that intercepts HTTP requests to validate
 * and authenticate JWT (JSON Web Token) based access tokens. It ensures that only requests with valid
 * JWTs are processed as authenticated, setting the security context accordingly.
 * <p>
 * This filter is intended to be used in stateless authentication scenarios, such as RESTful APIs,
 * where user sessions are not maintained on the server side. It extracts the JWT from the
 * {@code Authorization} header, validates it, and sets the authenticated user in the
 * {@link org.springframework.security.core.context.SecurityContextHolder}.
 * </p>
 *
 * <p>
 * Typical usage involves registering this filter in the Spring Security filter chain, before
 * any endpoint that requires authentication.
 * </p>
 *
 * <pre>
 * Example:
 *   http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
 * </pre>
 *
 * @see com.anee.projects.lovable_clone.security.AuthUtil
 * @see org.springframework.web.filter.OncePerRequestFilter
 *
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Utility class for JWT operations such as token verification and user extraction.
     */
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Filters each incoming HTTP request to validate the JWT token present in the
     * {@code Authorization} header. If the token is valid, sets the authenticated user in the
     * security context.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Incoming Request: {}", request.getRequestURI());

            final String requestTokenHeader = request.getHeader("Authorization");

            // Validate the token request header received
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
            }

            String jwtToken = requestTokenHeader.split("Bearer ")[1];

            JwtUserPrincipal user = authUtil.verifyAccessToken(jwtToken);

            // Set the authentication in the security context if not already set
            if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.authorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
