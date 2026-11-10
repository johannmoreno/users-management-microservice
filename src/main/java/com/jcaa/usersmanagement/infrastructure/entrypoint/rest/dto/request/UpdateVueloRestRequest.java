package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateVueloRestRequest(
    @NotNull(message = "fechaCompra must not be null") LocalDateTime fechaCompra,
    @NotNull(message = "fechaSalida must not be null") LocalDateTime fechaSalida,
    @NotNull(message = "fechaLlegada must not be null") LocalDateTime fechaLlegada,
    @NotBlank(message = "agenciaViajes must not be blank")
        @Size(max = 100, message = "agenciaViajes must have at most 100 characters")
        String agenciaViajes,
    @NotBlank(message = "aerolinea must not be blank")
        @Size(max = 100, message = "aerolinea must have at most 100 characters")
        String aerolinea,
    @NotBlank(message = "numero must not be blank") String numero,
    @NotBlank(message = "estado must not be blank") String estado,
    @NotNull(message = "valor must not be null")
        @DecimalMin(value = "0.01", message = "valor must be greater than zero")
        @Digits(integer = 12, fraction = 2, message = "valor must have at most 2 decimals")
        BigDecimal valor,
    @NotBlank(message = "cliente must not be blank")
        @Size(max = 100, message = "cliente must have at most 100 characters")
        String cliente,
    @NotBlank(message = "puesto must not be blank") String puesto,
    @NotBlank(message = "avion must not be blank")
        @Size(max = 50, message = "avion must have at most 50 characters")
        String avion,
    @NotBlank(message = "aeropuertoSalida must not be blank")
        @Size(max = 100, message = "aeropuertoSalida must have at most 100 characters")
        String aeropuertoSalida,
    @NotBlank(message = "aeropuertoLlegada must not be blank")
        @Size(max = 100, message = "aeropuertoLlegada must have at most 100 characters")
        String aeropuertoLlegada,
    @NotBlank(message = "piloto must not be blank")
        @Size(max = 100, message = "piloto must have at most 100 characters")
        String piloto) {}
