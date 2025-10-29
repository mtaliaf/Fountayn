package com.fountayn;

import com.fountayn.exceptions.InvalidTaskException;
import com.fountayn.exceptions.TaskNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/** Main application class providing a console-based interface for the TaskManager. */
public class Main {

  private final TaskManager taskManager;
  private final Scanner scanner;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /** Constructor initializes the TaskManager and Scanner. */
  public Main() {
    this.taskManager = new TaskManager();
    this.scanner = new Scanner(System.in);
  }

  /** Prints the main menu options to the console. */
  private void displayMenu() {
    System.out.println("\n===== Task Manager Menu =====");
    System.out.println("1. Add Task");
    System.out.println("2. Mark Task Completed (by ID)");
    System.out.println("3. List Tasks by Priority");
    System.out.println("4. List Overdue Tasks");
    System.out.println("5. Remove Task (by ID)");
    System.out.println("6. Exit");
    System.out.println("=============================");
    System.out.print("Enter your choice: ");
  }

  /** The main application loop. */
  public void run() {
    while (true) {
      displayMenu();

      String choiceStr = scanner.nextLine();
      int choice = -1;
      try {
        choice = Integer.parseInt(choiceStr);
      } catch (NumberFormatException e) {
        // No nothing and default back to -1;
      }

      switch (choice) {
        case 1:
          addTask();
          break;
        case 2:
          markCompleted();
          break;
        case 3:
          listByPriority();
          break;
        case 4:
          listOverdueTasks();
          break;
        case 5:
          removeTask();
          break;
        case 6:
          System.out.println("Exiting Task Manager. Goodbye!");
          return;
        default:
          System.out.println("Invalid option. Please enter a number between 1 and 6.");
      }
    }
  }

  // --- Menu Actions ---

  private void addTask() {
    System.out.println("\n--- Add New Task ---");
    System.out.print("Description: ");
    String description = scanner.nextLine();

    System.out.print("Priority (LOW, MEDIUM, HIGH): ");
    String priorityStr = scanner.nextLine().toUpperCase();

    System.out.print("Due Date (YYYY-MM-DD): ");
    String dateStr = scanner.nextLine();

    try {
      Task.Priority priority = Task.Priority.valueOf(priorityStr);
      LocalDate dueDate = LocalDate.parse(dateStr, DATE_FORMATTER);

      int taskId = taskManager.addTask(description, priority, dueDate);
      System.out.println("Task added successfully! ID: " + taskId);

    } catch (IllegalArgumentException | InvalidTaskException e) {
      // Catches Enum parsing error or validation errors from TaskManager
      System.out.println("Failed to add task: " + e.getMessage());
      if (e instanceof IllegalArgumentException) {
        System.out.println("Tip: Priority must be LOW, MEDIUM, or HIGH.");
      }
    } catch (DateTimeParseException e) {
      System.out.println("Failed to add task: Invalid date format. Please use YYYY-MM-DD.");
    }
  }

  private void markCompleted() {
    System.out.println("\n--- Mark Task Completed ---");
    try {
      System.out.print("Enter Task ID to mark completed: ");
      int id = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      taskManager.markTaskCompleted(id);
      System.out.println("Task ID " + id + " marked as completed.");

    } catch (TaskNotFoundException e) {
      System.out.println("Task not found." + e.getMessage());
    } catch (InputMismatchException e) {
      System.out.println("Invalid input. Please enter a number for the Task ID.");
      scanner.nextLine(); // Consume invalid input
    } catch (InvalidTaskException e) {
      throw new RuntimeException(e);
    }
  }

  private void listByPriority() {
    System.out.println("\n--- List by Priority ---");
    System.out.print("Enter Priority to list (LOW, MEDIUM, HIGH): ");
    String priorityStr = scanner.nextLine().toUpperCase();

    try {
      Task.Priority priority = Task.Priority.valueOf(priorityStr);
      List<Task> tasks = taskManager.getTasksByPriority(priority);

      if (tasks.isEmpty()) {
        System.out.println("No " + priority + " priority tasks found.");
        return;
      }

      System.out.println("\n" + priority + " Priority Tasks (Sorted by Due Date):");
      tasks.forEach(System.out::println);

    } catch (IllegalArgumentException e) {
      System.out.println("Invalid priority. Please enter LOW, MEDIUM, or HIGH.");
    }
  }

  private void listOverdueTasks() {
    List<Task> overdueTasks = taskManager.getOverdueTasks();

    if (overdueTasks.isEmpty()) {
      System.out.println("\nNo incomplete tasks are currently overdue!");
      return;
    }

    System.out.println("\nOverdue Tasks (Sorted High to Low Priority):");
    overdueTasks.forEach(System.out::println);
  }

  private void removeTask() {
    System.out.println("\n--- Remove Task ---");
    try {
      System.out.print("Enter Task ID to remove: ");
      int id = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      taskManager.removeTask(id);
      System.out.println("Task ID " + id + " removed successfully.");

    } catch (TaskNotFoundException e) {
      System.out.println("Error: " + e.getMessage());
    } catch (InputMismatchException e) {
      System.out.println("Invalid input. Please enter a number for the Task ID.");
      scanner.nextLine(); // Consume invalid input
    }
  }

  /**
   * The application entry point.
   *
   * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    new Main().run();
  }
}
