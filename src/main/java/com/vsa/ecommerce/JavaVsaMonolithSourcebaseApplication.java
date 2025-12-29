package com.vsa.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class JavaVsaMonolithSourcebaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaVsaMonolithSourcebaseApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();

        String protocol = "http";
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        String[] activeProfiles = env.getActiveProfiles();
        String profiles = activeProfiles.length == 0 ? env.getDefaultProfiles()[0] : String.join(", ", activeProfiles);

        log.info("\n----------------------------------------------------------\n" +
                "Application '{}' is running! Access URLs:\n" +
                "----------------------------------------------------------\n" +
                "  Local:      {}://localhost:{}{}\n" +
                "  External:   {}://{}:{}{}\n" +
                "  Profile(s): {}\n" +
                "----------------------------------------------------------\n" +
                "  API Docs:   {}://localhost:{}{}/swagger-ui.html (if enabled)\n" +
                "  Actuator:   {}://localhost:{}{}/actuator/health\n" +
                "----------------------------------------------------------",
                env.getProperty("spring.application.name", "Java VSA Monolith"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                profiles,
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath);

        // Log important endpoints
        log.info("\n----------------------------------------------------------\n" +
                "Available API Endpoints:\n" +
                "----------------------------------------------------------\n" +
                "  POST   /api/auth/login          - User authentication\n" +
                "  POST   /api/orders              - Create new order\n" +
                "  GET    /actuator/health         - Health check\n" +
                "----------------------------------------------------------");
    }

}
