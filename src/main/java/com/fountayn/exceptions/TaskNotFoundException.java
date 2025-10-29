package com.fountayn.exceptions;

/** Custom exception thrown when a task with a specific ID cannot be found. */
public class TaskNotFoundException extends Exception {

  /**
   * Constructs a new TaskNotFoundException with the specified detail message.
   *
   * @param message the detail message.
   */
  public TaskNotFoundException(String message) {
    super(message);
  }
}
