package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidValorVueloException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValorVueloTest {

  @Test
  @DisplayName("Should create ValorVuelo with a positive amount")
  void shouldCreateValorVueloWithPositiveAmount() {
    assertEquals(new BigDecimal("150000.50"), new ValorVuelo(new BigDecimal("150000.50")).value());
  }

  @Test
  @DisplayName("Should normalize the amount to two decimal places")
  void shouldNormalizeAmountToTwoDecimals() {
    assertEquals(new BigDecimal("150000.00"), new ValorVuelo(new BigDecimal("150000")).value());
    assertEquals(new BigDecimal("150000.50"), new ValorVuelo(new BigDecimal("150000.5")).value());
  }

  @Test
  @DisplayName("Values with the same amount should be equal after normalization")
  void shouldConsiderNormalizedValuesEqual() {
    assertEquals(
        new ValorVuelo(new BigDecimal("100")), new ValorVuelo(new BigDecimal("100.00")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "-1", "-0.01"})
  @DisplayName("Should throw InvalidValorVueloException when amount is not positive")
  void shouldThrowWhenAmountIsNotPositive(final String input) {
    assertThrows(InvalidValorVueloException.class, () -> new ValorVuelo(new BigDecimal(input)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"10.001", "0.001"})
  @DisplayName("Should throw InvalidValorVueloException when amount has more than two decimals")
  void shouldThrowWhenAmountHasTooManyDecimals(final String input) {
    assertThrows(InvalidValorVueloException.class, () -> new ValorVuelo(new BigDecimal(input)));
  }

  @Test
  @DisplayName("Should throw NullPointerException when amount is null")
  void shouldThrowNullPointerExceptionWhenAmountIsNull() {
    assertThrows(NullPointerException.class, () -> new ValorVuelo(null));
  }

  @Test
  @DisplayName("toString() should return the amount with two decimals")
  void shouldReturnAmountOnToString() {
    assertEquals("150000.50", new ValorVuelo(new BigDecimal("150000.5")).toString());
  }
}
