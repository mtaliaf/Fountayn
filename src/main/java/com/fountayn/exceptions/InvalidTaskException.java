package com.fountayn.exceptions;

/**
 * Custom exception thrown when attempting to create a task with invalid data (e.g., null or empty
 * description, null priority, or null due date).
 */
public class InvalidTaskException extends Exception {

  /**
   * Constructs a new InvalidTaskException with the specified detail message.
   *
   * @param message the detail message.
   */
  public InvalidTaskException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidTaskException with the specified detail message and cause.
   *
   * @param message the detail message.
   * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
   */
  public InvalidTaskException(String message, Throwable cause) {
    super(message, cause);
  }
}
