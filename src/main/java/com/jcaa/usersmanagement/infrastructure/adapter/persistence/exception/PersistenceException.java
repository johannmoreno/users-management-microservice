package com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception;

public final class PersistenceException extends RuntimeException {

  private static final String MESSAGE_SAVE = "Failed to save user with ID: '%s'.";
  private static final String MESSAGE_UPDATE = "Failed to update user with ID: '%s'.";
  private static final String MESSAGE_FIND = "Failed to find user with ID: '%s'.";
  private static final String MESSAGE_EMAIL = "Failed to find user with email: '%s'.";
  private static final String MESSAGE_ALL = "Failed to retrieve all users.";
  private static final String MESSAGE_DELETE = "Failed to delete user with ID: '%s'.";
  private static final String MESSAGE_CONNECTION = "Could not establish database connection.";
  private static final String MESSAGE_VUELO_SAVE = "Failed to save flight with ID: '%s'.";
  private static final String MESSAGE_VUELO_UPDATE = "Failed to update flight with ID: '%s'.";
  private static final String MESSAGE_VUELO_FIND = "Failed to find flight with ID: '%s'.";
  private static final String MESSAGE_VUELO_UNIQUE_KEY = "Failed to find flight with number: '%s'.";
  private static final String MESSAGE_VUELO_ALL = "Failed to retrieve all flights.";
  private static final String MESSAGE_VUELO_DELETE = "Failed to delete flight with ID: '%s'.";

  private PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static PersistenceException becauseSaveFailed(final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_SAVE, userId), cause);
  }

  public static PersistenceException becauseUpdateFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_UPDATE, userId), cause);
  }

  public static PersistenceException becauseFindByIdFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_FIND, userId), cause);
  }

  public static PersistenceException becauseFindByEmailFailed(
      final String email, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_EMAIL, email), cause);
  }

  public static PersistenceException becauseFindAllFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_ALL, cause);
  }

  public static PersistenceException becauseDeleteFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_DELETE, userId), cause);
  }

  public static PersistenceException becauseConnectionFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_CONNECTION, cause);
  }

  public static PersistenceException becauseVueloSaveFailed(
      final String vueloId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_VUELO_SAVE, vueloId), cause);
  }

  public static PersistenceException becauseVueloUpdateFailed(
      final String vueloId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_VUELO_UPDATE, vueloId), cause);
  }

  public static PersistenceException becauseVueloFindByIdFailed(
      final String vueloId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_VUELO_FIND, vueloId), cause);
  }

  public static PersistenceException becauseVueloFindByUniqueKeyFailed(
      final String numero, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_VUELO_UNIQUE_KEY, numero), cause);
  }

  public static PersistenceException becauseVueloFindAllFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_VUELO_ALL, cause);
  }

  public static PersistenceException becauseVueloDeleteFailed(
      final String vueloId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_VUELO_DELETE, vueloId), cause);
  }
}
