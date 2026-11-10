package com.jcaa.usersmanagement.domain.enums;

import com.jcaa.usersmanagement.domain.exception.InvalidEstadoVueloException;

public enum EstadoVuelo {
  PROGRAMADO,
  CONFIRMADO,
  EN_VUELO,
  ATERRIZADO,
  CANCELADO,
  RETRASADO;

  public static EstadoVuelo fromString(final String value) {
    for (final EstadoVuelo estado : values()) {
      if (estado.name().equalsIgnoreCase(value)) {
        return estado;
      }
    }
    throw InvalidEstadoVueloException.becauseValueIsInvalid(value);
  }
}
