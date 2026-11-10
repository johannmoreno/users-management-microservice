package com.jcaa.usersmanagement.domain.model;

import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.exception.InvalidAeropuertoException;
import com.jcaa.usersmanagement.domain.exception.InvalidFechaVueloException;
import com.jcaa.usersmanagement.domain.exception.InvalidVueloFieldException;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Value;

@Value
public class VueloModel {

  private static final int AGENCIA_VIAJES_MAX_LENGTH = 100;
  private static final int AEROLINEA_MAX_LENGTH = 100;
  private static final int CLIENTE_MAX_LENGTH = 100;
  private static final int AVION_MAX_LENGTH = 50;
  private static final int PILOTO_MAX_LENGTH = 100;

  VueloId id;
  LocalDateTime fechaCompra;
  LocalDateTime fechaSalida;
  LocalDateTime fechaLlegada;
  String agenciaViajes;
  String aerolinea;
  NumeroVuelo numero;
  EstadoVuelo estado;
  ValorVuelo valor;
  String cliente;
  PuestoVuelo puesto;
  String avion;
  Aeropuerto aeropuertoSalida;
  Aeropuerto aeropuertoLlegada;
  String piloto;

  public VueloModel(
      final VueloId id,
      final LocalDateTime fechaCompra,
      final LocalDateTime fechaSalida,
      final LocalDateTime fechaLlegada,
      final String agenciaViajes,
      final String aerolinea,
      final NumeroVuelo numero,
      final EstadoVuelo estado,
      final ValorVuelo valor,
      final String cliente,
      final PuestoVuelo puesto,
      final String avion,
      final Aeropuerto aeropuertoSalida,
      final Aeropuerto aeropuertoLlegada,
      final String piloto) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.fechaCompra = requireDate("fechaCompra", fechaCompra);
    this.fechaSalida = requireDate("fechaSalida", fechaSalida);
    this.fechaLlegada = requireDate("fechaLlegada", fechaLlegada);
    validateDates(this.fechaCompra, this.fechaSalida, this.fechaLlegada);
    this.agenciaViajes = requireText("agenciaViajes", agenciaViajes, AGENCIA_VIAJES_MAX_LENGTH);
    this.aerolinea = requireText("aerolinea", aerolinea, AEROLINEA_MAX_LENGTH);
    this.numero = Objects.requireNonNull(numero, "numero cannot be null");
    this.estado = Objects.requireNonNull(estado, "estado cannot be null");
    this.valor = Objects.requireNonNull(valor, "valor cannot be null");
    this.cliente = requireText("cliente", cliente, CLIENTE_MAX_LENGTH);
    this.puesto = Objects.requireNonNull(puesto, "puesto cannot be null");
    this.avion = requireText("avion", avion, AVION_MAX_LENGTH);
    this.aeropuertoSalida =
        Objects.requireNonNull(aeropuertoSalida, "aeropuertoSalida cannot be null");
    this.aeropuertoLlegada =
        Objects.requireNonNull(aeropuertoLlegada, "aeropuertoLlegada cannot be null");
    validateAirports(this.aeropuertoSalida, this.aeropuertoLlegada);
    this.piloto = requireText("piloto", piloto, PILOTO_MAX_LENGTH);
  }

  public static VueloModel create(
      final VueloId id,
      final LocalDateTime fechaCompra,
      final LocalDateTime fechaSalida,
      final LocalDateTime fechaLlegada,
      final String agenciaViajes,
      final String aerolinea,
      final NumeroVuelo numero,
      final ValorVuelo valor,
      final String cliente,
      final PuestoVuelo puesto,
      final String avion,
      final Aeropuerto aeropuertoSalida,
      final Aeropuerto aeropuertoLlegada,
      final String piloto) {
    return new VueloModel(
        id,
        fechaCompra,
        fechaSalida,
        fechaLlegada,
        agenciaViajes,
        aerolinea,
        numero,
        EstadoVuelo.PROGRAMADO,
        valor,
        cliente,
        puesto,
        avion,
        aeropuertoSalida,
        aeropuertoLlegada,
        piloto);
  }

  public VueloModel confirmar() {
    return withEstado(EstadoVuelo.CONFIRMADO);
  }

  public VueloModel cancelar() {
    return withEstado(EstadoVuelo.CANCELADO);
  }

  public VueloModel despegar() {
    return withEstado(EstadoVuelo.EN_VUELO);
  }

  public VueloModel aterrizar() {
    return withEstado(EstadoVuelo.ATERRIZADO);
  }

  public VueloModel retrasar() {
    return withEstado(EstadoVuelo.RETRASADO);
  }

  private VueloModel withEstado(final EstadoVuelo nuevoEstado) {
    return new VueloModel(
        id,
        fechaCompra,
        fechaSalida,
        fechaLlegada,
        agenciaViajes,
        aerolinea,
        numero,
        nuevoEstado,
        valor,
        cliente,
        puesto,
        avion,
        aeropuertoSalida,
        aeropuertoLlegada,
        piloto);
  }

  private static LocalDateTime requireDate(final String field, final LocalDateTime value) {
    if (Objects.isNull(value)) {
      throw InvalidFechaVueloException.becauseValueIsNull(field);
    }
    return value;
  }

  private static void validateDates(
      final LocalDateTime fechaCompra,
      final LocalDateTime fechaSalida,
      final LocalDateTime fechaLlegada) {
    if (fechaCompra.isAfter(fechaSalida)) {
      throw InvalidFechaVueloException.becauseFechaCompraIsAfterFechaSalida();
    }
    if (!fechaLlegada.isAfter(fechaSalida)) {
      throw InvalidFechaVueloException.becauseFechaLlegadaIsNotAfterFechaSalida();
    }
  }

  private static String requireText(
      final String field, final String value, final int maxLength) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw InvalidVueloFieldException.becauseValueIsEmpty(field);
    }
    final String normalizedValue = value.trim();
    if (normalizedValue.length() > maxLength) {
      throw InvalidVueloFieldException.becauseLengthIsTooLong(field, maxLength);
    }
    return normalizedValue;
  }

  private static void validateAirports(
      final Aeropuerto aeropuertoSalida, final Aeropuerto aeropuertoLlegada) {
    if (aeropuertoSalida.value().equalsIgnoreCase(aeropuertoLlegada.value())) {
      throw InvalidAeropuertoException.becauseAirportsAreEqual();
    }
  }
}
