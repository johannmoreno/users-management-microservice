package com.jcaa.usersmanagement.domain.exception;

import java.time.LocalDateTime;

public final class VueloAlreadyExistsException extends DomainException {

  private static final String MESSAGE_FLIGHT_EXISTS =
      "A flight with number '%s', departure date '%s' and seat '%s' already exists.";

  private VueloAlreadyExistsException(final String message) {
    super(message);
  }

  public static VueloAlreadyExistsException becauseFlightAlreadyExists(
      final String numero, final LocalDateTime fechaSalida, final String puesto) {
    return new VueloAlreadyExistsException(
        String.format(MESSAGE_FLIGHT_EXISTS, numero, fechaSalida, puesto));
  }
}
