package com.jcaa.usersmanagement.domain.exception;

public final class InvalidValorVueloException extends DomainException {

  private static final String MESSAGE_NOT_POSITIVE = "The flight price must be greater than zero.";
  private static final String MESSAGE_SCALE_INVALID =
      "The flight price must have at most 2 decimal places.";

  private InvalidValorVueloException(final String message) {
    super(message);
  }

  public static InvalidValorVueloException becauseValueIsNotPositive() {
    return new InvalidValorVueloException(MESSAGE_NOT_POSITIVE);
  }

  public static InvalidValorVueloException becauseScaleIsInvalid() {
    return new InvalidValorVueloException(MESSAGE_SCALE_INVALID);
  }
}
