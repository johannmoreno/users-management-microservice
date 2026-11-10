package com.jcaa.usersmanagement.domain.exception;

public final class InvalidFechaVueloException extends DomainException {

  private static final String MESSAGE_NULL = "The flight date '%s' must not be null.";
  private static final String MESSAGE_PURCHASE_AFTER_DEPARTURE =
      "The purchase date cannot be after the departure date.";
  private static final String MESSAGE_ARRIVAL_NOT_AFTER_DEPARTURE =
      "The arrival date must be after the departure date.";

  private InvalidFechaVueloException(final String message) {
    super(message);
  }

  public static InvalidFechaVueloException becauseValueIsNull(final String field) {
    return new InvalidFechaVueloException(String.format(MESSAGE_NULL, field));
  }

  public static InvalidFechaVueloException becauseFechaCompraIsAfterFechaSalida() {
    return new InvalidFechaVueloException(MESSAGE_PURCHASE_AFTER_DEPARTURE);
  }

  public static InvalidFechaVueloException becauseFechaLlegadaIsNotAfterFechaSalida() {
    return new InvalidFechaVueloException(MESSAGE_ARRIVAL_NOT_AFTER_DEPARTURE);
  }
}
