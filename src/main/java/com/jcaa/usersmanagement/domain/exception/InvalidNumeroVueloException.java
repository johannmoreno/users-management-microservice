package com.jcaa.usersmanagement.domain.exception;

public final class InvalidNumeroVueloException extends DomainException {

  private static final String MESSAGE_EMPTY = "The flight number must not be empty.";
  private static final String MESSAGE_INVALID_FORMAT =
      "The flight number format is invalid: '%s'. It must have 2 to 10 alphanumeric characters.";

  private InvalidNumeroVueloException(final String message) {
    super(message);
  }

  public static InvalidNumeroVueloException becauseValueIsEmpty() {
    return new InvalidNumeroVueloException(MESSAGE_EMPTY);
  }

  public static InvalidNumeroVueloException becauseFormatIsInvalid(final String numero) {
    return new InvalidNumeroVueloException(String.format(MESSAGE_INVALID_FORMAT, numero));
  }
}
