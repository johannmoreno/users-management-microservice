package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidVueloIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VueloIdTest {

  @ParameterizedTest
  @ValueSource(strings = {" v-001 ", "  v-001  ", "v-001\t"})
  @DisplayName("Should create VueloId with trimmed value")
  void shouldCreateVueloIdWithTrimmedValue(final String input) {
    assertEquals("v-001", new VueloId(input).value());
  }

  @Test
  @DisplayName("Should throw NullPointerException when VueloId is null")
  void shouldThrowNullPointerExceptionWhenVueloIdIsNull() {
    assertThrows(NullPointerException.class, () -> new VueloId(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r", "\f", "\b"})
  @DisplayName("Should throw InvalidVueloIdException when VueloId is empty")
  void shouldThrowWhenVueloIdIsEmpty(final String input) {
    assertThrows(InvalidVueloIdException.class, () -> new VueloId(input));
  }

  @Test
  @DisplayName("toString() should return the normalized value")
  void shouldReturnNormalizedValueOnToString() {
    assertEquals("v-001", new VueloId(" v-001 ").toString());
  }
}
