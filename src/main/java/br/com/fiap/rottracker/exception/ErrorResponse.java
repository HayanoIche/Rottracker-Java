package br.com.fiap.rottracker.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,      //Momento exato do erro
        int status,             //codigo HTTP
        String message,         //Titulo generico do erro
        String path             // A url que foi chamada
) {}