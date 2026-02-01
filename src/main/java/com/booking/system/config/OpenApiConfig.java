package com.booking.system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Booking System API",
                version = "1.0",
                description = "RESTFUL API for managing property bookings and blocks. "
                        + "Supports creating, updating, cancelling, and rebooking bookings "
                        + "with overlap prevention using pessimistic locking.",
                contact = @Contact(name = "Booking System")
        ),
        servers = @Server(url = "/", description = "Default server")
)
public class OpenApiConfig {
}
