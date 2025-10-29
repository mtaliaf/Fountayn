package com.fountayn;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fountayn.Task.Priority;
import com.fountayn.exceptions.InvalidTaskException;
import com.fountayn.exceptions.TaskNotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class TaskTest {

  @Test
  void buildShouldProduceATaskWithAnID() throws Exception {
    Task task =
        Task.builder()
            .priority(Priority.HIGH)
            .dueDate(LocalDate.of(10,10,10))
            .completed(true)
            .description("desc")
            .build();

    assertEquals(Priority.HIGH, task.getPriority());
    assertTrue(task.isCompleted());
    assertEquals("desc", task.getDescription());
    assertTrue(task.getId() > 0);
    assertEquals(LocalDate.of(10,10,10), task.getDueDate());
  }

  @Test
  void buildShouldProduceATaskWithDefaultNotCompleted() throws Exception {
    Task task =
        Task.builder()
            .priority(Priority.HIGH)
            .dueDate(LocalDate.of(10,10,10))
            .description("desc")
            .build();

    assertEquals(Priority.HIGH, task.getPriority());
    assertFalse(task.isCompleted());
    assertEquals("desc", task.getDescription());
    assertTrue(task.getId() > 0);
    assertEquals(LocalDate.of(10,10,10), task.getDueDate());
  }

  @Test
  void buildShouldThrowIfDescIsEmpty() throws Exception {
    assertThrows(
        InvalidTaskException.class,
        () -> {
          Task.builder()
              .priority(Priority.HIGH)
              .dueDate(LocalDate.of(10,10,10))
              .completed(true)
              .description("")
              .build();
        });
  }

  @Test
  void buildShouldThrowIfDescIsNull() throws Exception {
    assertThrows(
        InvalidTaskException.class,
        () -> {
          Task.builder()
              .priority(Priority.HIGH)
              .dueDate(LocalDate.of(10,10,10))
              .completed(true)
              .description(null)
              .build();
        });
  }

  @Test
  void buildShouldThrowIfPriorityIsNull() throws Exception {
    assertThrows(
        InvalidTaskException.class,
        () -> {
          Task.builder()
              .priority(null)
              .dueDate(LocalDate.of(10,10,10))
              .completed(true)
              .description("desc")
              .build();
        });
  }

  @Test
  void buildShouldThrowIfDueDateIsNull() throws Exception {
    assertThrows(
        InvalidTaskException.class,
        () -> {
          Task.builder()
              .priority(Priority.HIGH)
              .dueDate(null)
              .completed(true)
              .description("desc")
              .build();
        });
  }
}
