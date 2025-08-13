package pe.mil.ejercito.microservice.components.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pe.mil.ejercito.lib.utils.componets.enums.ResponseEnum;
import pe.mil.ejercito.lib.utils.componets.exceptions.CommonException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

/**
 * public class JwtAuthenticationFilterSecurity
 * <p>
 * public class JwtAuthenticationFilterSecurity class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BACSYSTEM APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 *
 * @author Bacsystem
 * @author bacsystem.sac@gmail.com
 * @since 24/03/2024
 */
@Log4j2
@Component
public class JwtAuthenticationFilterSecurity implements WebFilter {
    @Value("${microservice.bearer-key}")
    private String bearer;
    private final ReactiveAuthenticationManager authenticationManager;

    public JwtAuthenticationFilterSecurity(final ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private Optional<String> getAuthorization(ServerWebExchange exchange) {

        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    private String getAuthorizationPath(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        final String path = request.getPath().value();

        if (path.contains("/auth/login")) {
            return chain.filter(exchange);
        }
        if (getAuthorization(exchange).isEmpty()) {
            return Mono.error(new CommonException("Error 401 unauthorized, access denied", ResponseEnum.ERROR_UNAUTHORIZED, new ArrayList<>()));
        }
        return Mono.just(getAuthorizationPath(exchange))
                .filter(filter -> filter.startsWith(bearer))
                .map(token -> token.replace(bearer, ""))
                .flatMap(authenticate -> {
                    log.debug("authenticate {}", authenticate);
                    return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticate, authenticate));
                })
                .flatMap(authentication -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))

                .doOnSuccess(success -> log.debug("success filter chain"))
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }
}