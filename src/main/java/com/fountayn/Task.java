package com.fountayn;

import com.fountayn.exceptions.InvalidTaskException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single task in the task management system. Uses a Builder pattern for construction
 * and thread-safe ID generation. This class is immutable to help ensure invariance.
 */
public class Task {

  /**
   * Thread-safe counter to generate unique IDs, starting from 1.
   * This is a almost a *pure* data class but I am choosing to have this small amount of static state here which
   * is typically not desirable.  Moving ID generation out of here would be fine but this allows us
   * to not have any public setters for the ID, ensuring that we never have to worry about an invalid
   * task id being generatated.
   * */
  private static final AtomicInteger nextIdCounter = new AtomicInteger(1);

  private final int id;
  private final String description;
  private final Priority priority;
  private final LocalDate dueDate;
  private final boolean completed;

  /**
   * Private constructor to be called by the TaskBuilder.
   *
   * @param builder The builder instance containing all task data.
   */
  private Task(Builder builder) {
    this.id = builder.id;
    this.description = builder.description;
    this.priority = builder.priority;
    this.dueDate = builder.dueDate;
    this.completed = builder.completed;
  }

  /**
   * Provides a new TaskBuilder instance.
   *
   * @return A new TaskBuilder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a new TaskBuilder instance initialized with the current Task's description, priority,
   * and due date. Since a Task built from this builder will receive a new ID.
   *
   * @return A TaskBuilder pre-populated with the current task's core data.
   */
  public Builder toBuilder() {
    return new Builder()
        .description(this.description)
        .priority(this.priority)
        .dueDate(this.dueDate)
        .completed(this.completed);
  }

  /** Represents the priority levels for a Task. */
  public enum Priority {
    HIGH,
    MEDIUM,
    LOW
  }

  /** Static inner Builder class for creating Task instances. */
  public static class Builder {
    private int id;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private boolean completed = false; // Explicitly default to false.

    /**
     * Sets the description for the task.
     *
     * @param description The task description.
     * @return this builder instance for chaining.
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the priority for the task.
     *
     * @param priority The task priority.
     * @return this builder instance for chaining.
     */
    public Builder priority(Priority priority) {
      this.priority = priority;
      return this;
    }

    /**
     * Sets the due date for the task.
     *
     * @param dueDate The task due date.
     * @return this builder instance for chaining.
     */
    public Builder dueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    /**
     * Sets the completion status for the task.
     *
     * @param completed The task completion status.
     * @return this builder instance for chaining.
     */
    public Builder completed(boolean completed) {
      this.completed = completed;
      return this;
    }

    /**
     * Builds and returns a new Task instance after validation.
     *
     * @return A new, validated Task object.
     * @throws InvalidTaskException if any validation fails (e.g., null or empty description, null
     *     priority, or null due date).
     */
    public Task build() throws InvalidTaskException {
      id = nextIdCounter.getAndIncrement();
      // Validate inputs
      if (description == null || description.trim().isEmpty()) {
        throw new InvalidTaskException("Description cannot be null or empty.");
      }
      if (priority == null) {
        throw new InvalidTaskException("Priority cannot be null.");
      }
      if (dueDate == null) {
        throw new InvalidTaskException("Due date cannot be null.");
      }

      // Trim description before creating the object
      this.description = this.description.trim();

      return new Task(this);
    }
  }

  /**
   * @return The unique identifier for the task.
   */
  public int getId() {
    return id;
  }

  /**
   * @return The task description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return The task priority (LOW, MEDIUM, or HIGH).
   */
  public Priority getPriority() {
    return priority;
  }

  /**
   * @return The date the task is due.
   */
  public LocalDate getDueDate() {
    return dueDate;
  }

  /**
   * @return true if the task is completed, false otherwise.
   */
  public boolean isCompleted() {
    return completed;
  }

  /**
   * Provides a clear string representation of the task.
   *
   * @return A string summary of the task.
   */
  @Override
  public String toString() {
    String status = completed ? "Completed" : "Pending";
    return String.format(
        "Task[ID: %d, Desc: '%s', Priority: %s, Due: %s, Status: %s]",
        id, description, priority, dueDate, status);
  }

  /** Defines task equality based on the unique ID. */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return id == task.id;
  }

  /** Generates a hash code based on the unique ID. */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
