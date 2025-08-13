package pe.mil.ejercito.microservice.components.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * SpringCustomSecurity
 * <p>
 * SpringCustomSecurity class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BACSYSTEM APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 *
 * @author Bacsystem
 * @author bacsystem.sac@gmail.com
 * @since 24/03/2024
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SpringCustomSecurity {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http,
                                                         final @Qualifier(value = "jwtAuthenticationFilterSecurity") JwtAuthenticationFilterSecurity jwtAuthFilter) {

        http.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("PUT","GET","POST","DELETE","OPTIONS"));
            configuration.setAllowedHeaders(List.of("X-Request-Id","Authorization","Accept","Content-Type","credential","Content-Encoding"));
            return configuration;
        });

        http.anonymous(ServerHttpSecurity.AnonymousSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(auth -> {
            auth.pathMatchers("/api/security/api-core-ejercito-statistics-security-microservice/v1/auth/login").permitAll();
            auth.pathMatchers("/api/security/api-core-ejercito-statistics-security-microservice/v1/actuator/**").permitAll();
            auth.pathMatchers("/api/security/api-core-ejercito-statistics-security-microservice/v1/persons/**").authenticated();
            auth.pathMatchers("/api/security/api-core-ejercito-statistics-security-microservice/v1/profiles/**").authenticated();
            auth.pathMatchers("/api/security/api-core-ejercito-statistics-security-microservice/v1/users/**").authenticated();
            auth.pathMatchers("/api/dependencies/api-core-ejercito-statistics-dependencies-microservice/v1/**").authenticated();
            auth.anyExchange().authenticated();
        });
        http.addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        return http.build();
    }
}


