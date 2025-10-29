package com.fountayn;

import com.fountayn.exceptions.InvalidTaskException;
import com.fountayn.exceptions.TaskNotFoundException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** Manages a collection of Tasks, providing methods to add, remove, update, and query tasks. */
public class TaskManager {

  private final Map<Integer, Task> tasks;
  private final Clock clock;

  public TaskManager() {
    this(new ConcurrentHashMap<>(), Clock.systemDefaultZone());
  }

  /** Visible for testing only. */
  TaskManager(Map<Integer, Task> tasks, Clock clock) {
    this.tasks = tasks;
    this.clock = clock;
  }

  /**
   * Adds a new task to the manager. Validates input data via the Task.Builder.
   *
   * @param description The non-empty description of the task.
   * @param priority The priority level (LOW, MEDIUM, HIGH).
   * @param dueDate The date the task is due.
   * @return The unique, auto-generated ID of the newly added task.
   * @throws InvalidTaskException if the description, priority, or dueDate are invalid (e.g., null
   *     or empty description).
   */
  public int addTask(String description, Task.Priority priority, LocalDate dueDate)
      throws InvalidTaskException {
    // Use the Task builder, which handles its own validation
    Task task = Task.builder().description(description).priority(priority).dueDate(dueDate).build();

    return addTask(task).getId();
  }

  /**
   * Marks an existing task as completed.
   *
   * @param id The ID of the task to mark as completed.
   * @return The new unique ID of the updated task.
   * @throws TaskNotFoundException if no task with the given ID is found.
   */
  public int markTaskCompleted(int id) throws TaskNotFoundException, InvalidTaskException {
    Task task = removeTask(id);
    Task completedTask = task.toBuilder().completed(true).build();
    return addTask(completedTask).getId();
  }

  private Task addTask(Task task) throws InvalidTaskException {
    if (tasks.putIfAbsent(task.getId(), task) != null) {
      throw new InvalidTaskException(
          "Task with id: " + task.getId() + " already exists! Please try again.");
    }
    return task;
  }

  /**
   * Retrieves a list of tasks filtered by a specific priority, sorted by due date in ascending
   * order (earliest first).
   *
   * @param priority The priority level to filter by.
   * @return A sorted list of tasks matching the priority.
   */
  public List<Task> getTasksByPriority(Task.Priority priority) {
    return tasks.values().stream()
        .filter(task -> task.getPriority() == priority)
        .sorted(Comparator.comparing(Task::getDueDate))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a list of all incomplete tasks that are past their due date. The list is sorted by
   * priority from HIGH to LOW.
   *
   * @return A sorted list of overdue, incomplete tasks.
   */
  public List<Task> getOverdueTasks() {
    LocalDate now = LocalDate.now(clock);
    Comparator<Task> byPriorityHighToLow = Comparator.comparing(Task::getPriority);

    return tasks.values().stream()
        .filter(task -> !task.isCompleted())
        .filter(task -> task.getDueDate().isBefore(now))
        .sorted(byPriorityHighToLow)
        .collect(Collectors.toList());
  }

  /**
   * Removes a task from the manager by its ID.
   *
   * @param id The ID of the task to remove.
   * @throws TaskNotFoundException if no task with the given ID is found.
   */
  public Task removeTask(int id) throws TaskNotFoundException {
    Task removedTask = tasks.remove(id);
    if (removedTask == null) {
      throw new TaskNotFoundException("Task with ID " + id + " not found.");
    }

    return removedTask;
  }
}
