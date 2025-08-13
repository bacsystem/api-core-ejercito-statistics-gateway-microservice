# api-core-ejercito-statistics-gateway-microservice

Microservicio que actúa como **puerta de entrada (API Gateway)** para el dominio de estadísticas del Ejército del
Perú.  
Se encarga de enrutar, filtrar y proteger las peticiones hacia los microservicios internos del ecosistema **Core
Ejército**.

## 📋 Características

- Enrutamiento dinámico de peticiones a microservicios registrados en **Eureka**.
- Integración con el microservicio de seguridad (`api-core-ejercito-statistics-security-microservice`).
- Filtrado y validación de solicitudes a nivel de gateway.
- Configuración centralizada de endpoints externos.
- Balanceo de carga y reintentos automáticos.
- Compatible con **Spring Cloud Gateway** y **WebFlux**.

## 🛠 Requisitos

- **Java**: 11+
- **Spring Boot**: 2.x o superior
- **Spring Cloud Gateway**
- **Maven**: 3.9.4

Dependencias clave:

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
