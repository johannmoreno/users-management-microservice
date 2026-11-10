package com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VueloPersistenceDto(
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
    String piloto,
    String createdAt,
    String updatedAt) {}
