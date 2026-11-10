package com.jcaa.usersmanagement.domain.exception;

public final class InvalidAeropuertoException extends DomainException {

  private static final String MESSAGE_EMPTY = "The airport must not be empty.";
  private static final String MESSAGE_TOO_LONG =
      "The airport must have at most %d characters.";
  private static final String MESSAGE_SAME_AIRPORTS =
      "The departure and arrival airports must be different.";

  private InvalidAeropuertoException(final String message) {
    super(message);
  }

  public static InvalidAeropuertoException becauseValueIsEmpty() {
    return new InvalidAeropuertoException(MESSAGE_EMPTY);
  }

  public static InvalidAeropuertoException becauseLengthIsTooLong(final int maxLength) {
    return new InvalidAeropuertoException(String.format(MESSAGE_TOO_LONG, maxLength));
  }

  public static InvalidAeropuertoException becauseAirportsAreEqual() {
    return new InvalidAeropuertoException(MESSAGE_SAME_AIRPORTS);
  }
}
