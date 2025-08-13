package pe.mil.ejercito.microservice.components.handlers;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import pe.mil.ejercito.lib.utils.componets.helpers.MessageHelper;
import pe.mil.ejercito.lib.utils.dto.errors.ErrorDto;
import pe.mil.ejercito.lib.utils.dto.errors.ErrorResponse;
import pe.mil.ejercito.lib.utils.dto.json.MessageDto;
import reactor.core.publisher.Mono;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandler
 * <p>
 * GlobalExceptionHandler class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BXCODE APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 *
 * @author bxcode
 * @author bacsystem.sac@gmail.com
 * @since 25/03/2024
 */

@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final MessageHelper helper;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer, MessageHelper helper) {
        super(errorAttributes, resources, applicationContext);
        this.helper = helper;
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::customErrorResponse);
    }

    private Mono<ServerResponse> customErrorResponse(ServerRequest request) {
        Map<String, Object> errorMap = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = HttpStatus.valueOf(502);
        String body;
        try (Jsonb jsonb = JsonbBuilder.create()) {
            final MessageDto message = this.helper.findCode(errorMap.get("code").toString());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(message.getValue());
            errorResponse.setMessage(message.getMessage());
            errorResponse.setDateTime(new Date());
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrors(List.of(errorMap.get("error").toString()));
            errorResponse.setData(errorDto);
            status = HttpStatus.valueOf(message.getStatus());
            body = jsonb.toJson(errorResponse);
            return ServerResponse.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body));
        } catch (Exception e) {
            log.error("error response in process {}", e.getMessage());
        }
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

}


