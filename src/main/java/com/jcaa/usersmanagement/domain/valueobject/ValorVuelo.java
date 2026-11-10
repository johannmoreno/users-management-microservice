package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidValorVueloException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ValorVuelo(BigDecimal value) {

  private static final int SCALE = 2;

  public ValorVuelo {
    Objects.requireNonNull(value, "ValorVuelo cannot be null");
    validatePositive(value);
    validateScale(value);
    value = value.setScale(SCALE, RoundingMode.UNNECESSARY);
  }

  private static void validatePositive(final BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw InvalidValorVueloException.becauseValueIsNotPositive();
    }
  }

  private static void validateScale(final BigDecimal value) {
    if (value.scale() > SCALE) {
      throw InvalidValorVueloException.becauseScaleIsInvalid();
    }
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }
}
