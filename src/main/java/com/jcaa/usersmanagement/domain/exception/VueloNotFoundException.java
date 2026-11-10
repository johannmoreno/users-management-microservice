package com.jcaa.usersmanagement.domain.exception;

public final class VueloNotFoundException extends DomainException {

  private static final String MESSAGE_BY_ID = "The flight with id '%s' was not found.";

  private VueloNotFoundException(final String message) {
    super(message);
  }

  public static VueloNotFoundException becauseIdWasNotFound(final String vueloId) {
    return new VueloNotFoundException(String.format(MESSAGE_BY_ID, vueloId));
  }
}
