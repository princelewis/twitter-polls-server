package com.twitter.polls.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//The OncePerRequestFilter this class is extending is majorly the filter used in security
//since it is executed once per request. This filter is the first pipeline to pass before
//the servlet serves the request to the app.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            String jwt = getJwtFromHeader(httpServletRequest);

            boolean jwtIsAuthentic = jwtTokenProvider.validateToken(jwt);

            if(StringUtils.hasText(jwt) && jwtIsAuthentic){
                Long id = jwtTokenProvider.getUserIdFromJwt(jwt);


                //Note that, the database hit in this filter is optional.
                // You could also encode the user’s username and roles inside JWT claims
                // and create the UserDetails object by parsing those claims from the JWT.
                // That would avoid the database hit.
                UserDetails userDetails = customUserDetailsService.loadUserById(id);

                //At this point you need to to set the user details and authorities
                //in the Spring Security context to authenticate user.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch(Exception e){
            logger.error("Could not set user authentication in security context - {}", e);
        }

        //Pass on the request and response to the next filter to do it's job
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
