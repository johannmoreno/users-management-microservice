package com.jcaa.usersmanagement.domain.exception;

public final class InvalidEstadoVueloException extends DomainException {

  private static final String MESSAGE_INVALID = "The flight status '%s' is not valid.";

  private InvalidEstadoVueloException(final String message) {
    super(message);
  }

  public static InvalidEstadoVueloException becauseValueIsInvalid(final String estado) {
    return new InvalidEstadoVueloException(String.format(MESSAGE_INVALID, estado));
  }
}
