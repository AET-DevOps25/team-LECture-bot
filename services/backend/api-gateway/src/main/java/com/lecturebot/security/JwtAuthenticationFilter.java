package com.lecturebot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.User;
import java.util.List;

public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwt = getJwtFromRequest(exchange.getRequest());

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromJWT(jwt);

            // Reactive user details loading
            UserDetails userDetails = new User(username, "", List.of());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Set authentication in the reactive context
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        return chain.filter(exchange);
    }

    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // @Override
    // protected void doFilterInternal(HttpServletRequest request,
    // HttpServletResponse response, FilterChain filterChain)
    // throws ServletException, IOException {
    // try {
    // String jwt = getJwtFromRequest(request);

    // if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
    // String username = tokenProvider.getUsernameFromJWT(jwt);

    // // Load user details from the database
    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    // UsernamePasswordAuthenticationToken authentication = new
    // UsernamePasswordAuthenticationToken(
    // userDetails, null, userDetails.getAuthorities());
    // authentication.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request));

    // // Set the authentication in the security context
    // SecurityContextHolder.getContext().setAuthentication(authentication);
    // }
    // } catch (Exception ex) {
    // logger.error("Could not set user authentication in security context", ex);
    // }

    // filterChain.doFilter(request, response);
    // }

    // private String getJwtFromRequest(HttpServletRequest request) {
    // String bearerToken = request.getHeader("Authorization");
    // if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
    // return bearerToken.substring(7);
    // }
    // return null;
    // }

    // @Override
    // protected boolean shouldNotFilter(@org.springframework.lang.NonNull
    // HttpServletRequest request) throws ServletException {
    // String path = request.getRequestURI();
    // boolean skip = path.equals("/api/v1/courses") ||
    // path.equals("/api/v1/courses/") || path.startsWith("/api/v1/courses/");
    // System.out.println("[JwtAuthenticationFilter] shouldNotFilter path: " + path
    // + " skip: " + skip);
    // return skip;
    // }
}
