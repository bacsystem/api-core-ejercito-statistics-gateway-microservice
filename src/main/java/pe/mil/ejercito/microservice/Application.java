package pe.mil.ejercito.microservice;


import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.mil.ejercito.microservice.components.security.JWTSecurity;

/**
 * Application
 * <p>
 * Application class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BXCODE APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 *
 * @author cbaciliod
 * @author bacsystem.sac@gmail.com
 * @since 10/03/2024
 */
@Log4j2
@SpringBootApplication(scanBasePackages = {
        "pe.mil.ejercito.lib.utils",
        "pe.mil.ejercito.microservice",
})
public class Application implements CommandLineRunner {

    private final JWTSecurity jwtSecurity;

    public Application(JWTSecurity jwtSecurity) {
        this.jwtSecurity = jwtSecurity;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("generate token {}", jwtSecurity.generate("cbaciliod"));
    }
}