package com.jcaa.usersmanagement.domain.exception;

public final class InvalidVueloIdException extends DomainException {

  private static final String MESSAGE_EMPTY = "The flight id must not be empty.";

  private InvalidVueloIdException(final String message) {
    super(message);
  }

  public static InvalidVueloIdException becauseValueIsEmpty() {
    return new InvalidVueloIdException(MESSAGE_EMPTY);
  }
}
