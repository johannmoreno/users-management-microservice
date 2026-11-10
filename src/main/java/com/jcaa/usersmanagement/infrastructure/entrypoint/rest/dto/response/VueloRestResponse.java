package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VueloRestResponse(
    String id,
    LocalDateTime fechaCompra,
    LocalDateTime fechaSalida,
    LocalDateTime fechaLlegada,
    String agenciaViajes,
    String aerolinea,
    String numero,
    String estado,
    BigDecimal valor,
    String cliente,
    String puesto,
    String avion,
    String aeropuertoSalida,
    String aeropuertoLlegada,
    String piloto) {}
