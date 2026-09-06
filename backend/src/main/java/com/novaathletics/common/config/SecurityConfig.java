package com.novaathletics.common.config;
import com.novaathletics.common.security.JwtAuthFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtFilter;
  public SecurityConfig(JwtAuthFilter f){this.jwtFilter=f;}
  @Bean SecurityFilterChain filter(HttpSecurity http, @Value("${app.cors.allowed-origins:http://localhost:3000}") String origins) throws Exception{
    CorsConfiguration cors=new CorsConfiguration();
    cors.setAllowedOrigins(List.of(origins.split(",")));
    cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cors.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key","X-Request-Id","X-Guest-Id"));
    cors.setAllowCredentials(true);
    http.csrf(c->c.disable()).cors(c->c.configurationSource(r->{ return cors; }))
      .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a->a
        .requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/refresh","/api/v1/auth/forgot-password","/api/v1/auth/reset-password").permitAll()
        .requestMatchers("/actuator/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/products/**","/api/v1/categories/**","/api/v1/collections/**","/api/v1/search").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/products/*/reviews").permitAll()
        .requestMatchers("/api/v1/cart/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE")
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"Unauthorized\"}}");})
        .accessDeniedHandler((req,res,ex)->{res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"Forbidden\"}}");}));
    return http.build();
  }
  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(12); }
}
