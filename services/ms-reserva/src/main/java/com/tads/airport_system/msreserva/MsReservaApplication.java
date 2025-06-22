package com.tads.airport_system.msreserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the MS-Reserva service.
 * This service implements CQRS pattern with separate command and query databases.
 */
@SpringBootApplication
@EnableTransactionManagement
public class MsReservaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReservaApplication.class, args);
        System.out.println("MS-Reserva service started with CQRS pattern");
        System.out.println("- Command DB: reserva_command_db");
        System.out.println("- Query DB: reserva_query_db");
        System.out.println("- Event Queue: reserva.eventos");
    }
} 