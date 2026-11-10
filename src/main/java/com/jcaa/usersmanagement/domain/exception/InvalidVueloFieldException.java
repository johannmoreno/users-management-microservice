package com.jcaa.usersmanagement.domain.exception;

public final class InvalidVueloFieldException extends DomainException {

  private static final String MESSAGE_EMPTY = "The flight field '%s' must not be empty.";
  private static final String MESSAGE_TOO_LONG =
      "The flight field '%s' must have at most %d characters.";

  private InvalidVueloFieldException(final String message) {
    super(message);
  }

  public static InvalidVueloFieldException becauseValueIsEmpty(final String field) {
    return new InvalidVueloFieldException(String.format(MESSAGE_EMPTY, field));
  }

  public static InvalidVueloFieldException becauseLengthIsTooLong(
      final String field, final int maxLength) {
    return new InvalidVueloFieldException(String.format(MESSAGE_TOO_LONG, field, maxLength));
  }
}
