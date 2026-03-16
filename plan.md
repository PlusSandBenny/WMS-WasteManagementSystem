# Upgrade Plan

## Technology Stack

- **Java Version**: 17
- **Spring Boot Version**: 3.2.0
- **Dependencies**:
  - org.springframework.boot:spring-boot-starter-web (managed by parent)
  - org.springframework.boot:spring-boot-starter-websocket (managed by parent)
  - org.springframework.boot:spring-boot-starter-data-jpa (managed by parent)
  - org.springframework.boot:spring-boot-starter-security (managed by parent)
  - com.mysql:mysql-connector-j:8.4.0
  - com.github.librepdf:openpdf:1.3.39
  - org.yaml:snakeyaml:2.3
  - org.springframework.boot:spring-boot-starter-validation (managed by parent)
  - org.springframework.boot:spring-boot-configuration-processor (managed by parent)
  - io.jsonwebtoken:jjwt-api:0.12.3
  - io.jsonwebtoken:jjwt-impl:0.12.3
  - io.jsonwebtoken:jjwt-jackson:0.12.3
  - org.springframework.boot:spring-boot-starter-test (managed by parent)
  - org.springframework.security:spring-security-test (managed by parent)
  - com.h2database:h2 (managed by parent)

## Derived Upgrades

All dependencies are compatible with Java 21. No EOL dependencies identified. No CVEs found in the explicitly versioned dependencies. No upgrades required for Java 21 compatibility.