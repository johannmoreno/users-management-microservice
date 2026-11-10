package com.jcaa.usersmanagement.domain.exception;

public final class InvalidPuestoVueloException extends DomainException {

  private static final String MESSAGE_EMPTY = "The flight seat must not be empty.";
  private static final String MESSAGE_INVALID_FORMAT =
      "The flight seat format is invalid: '%s'. Expected format: 12A.";

  private InvalidPuestoVueloException(final String message) {
    super(message);
  }

  public static InvalidPuestoVueloException becauseValueIsEmpty() {
    return new InvalidPuestoVueloException(MESSAGE_EMPTY);
  }

  public static InvalidPuestoVueloException becauseFormatIsInvalid(final String puesto) {
    return new InvalidPuestoVueloException(String.format(MESSAGE_INVALID_FORMAT, puesto));
  }
}
