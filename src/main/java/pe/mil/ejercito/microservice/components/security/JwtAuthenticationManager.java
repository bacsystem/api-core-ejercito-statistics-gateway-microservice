package pe.mil.ejercito.microservice.components.security;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pe.mil.ejercito.lib.utils.componets.enums.ResponseEnum;
import pe.mil.ejercito.lib.utils.componets.exceptions.CommonException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtAuthenticationManager
 * <p>
 * JwtAuthenticationManager class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BACSYSTEM APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 * <p>
 * TO-DO List<String> roles = claims.get("authorities", List.Class);
 * TO-DO  Collection<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
 *
 * @author Bacsystem
 * @author bacsystem.sac@gmail.com
 * @since 24/03/2024
 */
@Log4j2
@Component
@Configuration
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTSecurity jwtSecurity;

    public JwtAuthenticationManager(final JWTSecurity jwtSecurity) {
        this.jwtSecurity = jwtSecurity;
    }

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        return Mono.just(authentication)
                .filter(authToken -> this.jwtSecurity.validate(authToken.getCredentials().toString()))
                .map(authToken -> this.jwtSecurity.getClaims(authToken.getCredentials().toString()))
                .onErrorResume(e -> Mono.error(new CommonException("Invalid token or JWT expired", ResponseEnum.ERROR_INVALID_JWT, new ArrayList<>())))
                .map(claims -> {
                    log.debug("claims {}", claims);
                    return new UsernamePasswordAuthenticationToken(claims.getSubject(), authentication.getCredentials().toString(), grantedAuthorities(claims));
                });
    }

    private List<GrantedAuthority> grantedAuthorities(Claims jws) {
        Object authorities = jws.get("authorities");
        if (authorities == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(authorities.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}


