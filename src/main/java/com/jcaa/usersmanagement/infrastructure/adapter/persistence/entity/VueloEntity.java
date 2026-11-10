package com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VueloEntity(
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
