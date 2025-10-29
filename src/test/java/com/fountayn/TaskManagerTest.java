package com.fountayn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fountayn.Task.Priority;
import com.fountayn.exceptions.TaskNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskManagerTest {

  Map<Integer, Task> store;
  Clock clock;
  private TaskManager manager;

  private static final LocalDate PAST_DUE_DATE = LocalDate.of(2011, 11, 11);
  private static final String GENERIC_DESCRIPTION = "DESC";

  @BeforeEach
  void initManager() {
    store = new HashMap<>();
    clock = Clock.fixed(Instant.parse("2023-10-27T10:30:00Z"), ZoneId.of("America/New_York"));
    manager = new TaskManager(store, clock);
  }

  @Test
  void addTaskShouldStoreATask() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();

    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());

    Task storedTask = getOnlyTask();
    assertEquals(task.getDescription(), storedTask.getDescription());
    assertEquals(task.getPriority(), storedTask.getPriority());
    assertEquals(task.getDueDate(), storedTask.getDueDate());
    assertEquals(task.isCompleted(), storedTask.isCompleted());
    assertTrue(storedTask.getId() >= 1);
  }

  @Test
  void addTaskShouldIncrementId() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();

    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());
    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());

    assertEquals(2, store.size());
  }

  @Test
  void markTaskCompletedShouldUpdateTask() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();

    int storedId = manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());

    Task storedTask = getOnlyTask();
    assertFalse(storedTask.isCompleted());
    int completedId = manager.markTaskCompleted(storedId);
    assertTrue(storedId != completedId);
    storedTask = getOnlyTask();
    assertTrue(storedTask.isCompleted());
  }

  @Test
  void markTaskCompletedShouldThrowIfIdDoesntExist() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();

    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());

    assertThrows(
        TaskNotFoundException.class,
        () -> {
          manager.markTaskCompleted(-1);
        });
  }

  @Test
  void getTasksByPriorityShouldEmptyListIfNoneOfThatPriority() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());

    List<Task> tasks = manager.getTasksByPriority(Priority.HIGH);

    assertEquals(0, tasks.size());
  }

  @Test
  void getTasksByPriorityShouldFilterWrongPriority() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());
    manager.addTask(task.getDescription(), task.getPriority(), task.getDueDate());
    manager.addTask(task.getDescription(), Priority.HIGH, task.getDueDate());

    List<Task> tasks = manager.getTasksByPriority(Priority.LOW);

    assertEquals(2, tasks.size());
    tasks.forEach((element) -> assertEquals(Priority.LOW, element.getPriority()));
  }

  @Test
  void getTasksByPriorityShouldSortByDueDate() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), task.getPriority(), LocalDate.of(2026, 11, 11));
    manager.addTask(task.getDescription(), task.getPriority(), LocalDate.of(2025, 11, 11));
    manager.addTask(task.getDescription(), task.getPriority(), LocalDate.of(2028, 11, 11));

    List<Task> tasks = manager.getTasksByPriority(Priority.LOW);

    assertEquals(3, tasks.size());
    assertEquals(2025, tasks.getFirst().getDueDate().getYear());
    assertEquals(2028, tasks.getLast().getDueDate().getYear());
  }

  @Test
  void getOverdueTasksShouldOnlyReturnPastDue() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), task.getPriority(), LocalDate.of(2040, 11, 11));
    manager.addTask(task.getDescription(), task.getPriority(), PAST_DUE_DATE);

    List<Task> tasks = manager.getOverdueTasks();

    assertEquals(1, tasks.size());
    assertEquals(PAST_DUE_DATE.getYear(), tasks.getFirst().getDueDate().getYear());
  }

  @Test
  void getOverdueTasksShouldEmptyListIfNoneOverDue() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), task.getPriority(), LocalDate.of(2025, 10, 26));

    List<Task> tasks = manager.getOverdueTasks();

    assertTrue(tasks.isEmpty());
  }

  @Test
  void getOverdueTasksShouldEmptyListIfMarkedComplete() throws Exception {
    Task task =
        Task.builder()
            .description(GENERIC_DESCRIPTION)
            .dueDate(PAST_DUE_DATE)
            .priority(Priority.LOW)
            .build();
    int id = manager.addTask(task.getDescription(), task.getPriority(), PAST_DUE_DATE);
    manager.markTaskCompleted(id);
    List<Task> tasks = manager.getOverdueTasks();

    assertTrue(tasks.isEmpty());
  }

  @Test
  void getOverdueTasksShouldSortListOnPriority() throws Exception {
    Task task =
        Task.builder()
            .description("task")
            .dueDate(LocalDate.of(2011, 11, 11))
            .priority(Priority.LOW)
            .build();
    manager.addTask(task.getDescription(), Priority.MEDIUM, PAST_DUE_DATE);
    manager.addTask(task.getDescription(), Priority.LOW, PAST_DUE_DATE);
    manager.addTask(task.getDescription(), Priority.HIGH, PAST_DUE_DATE);

    List<Task> tasks = manager.getOverdueTasks();

    assertEquals(3, tasks.size());
    assertEquals(Priority.HIGH, tasks.getFirst().getPriority());
    assertEquals(Priority.LOW, tasks.getLast().getPriority());
  }

  @Test
  void removeTaskShouldRemoveTaskIfFound() throws Exception {
    int id = manager.addTask("TEST", Priority.MEDIUM, LocalDate.of(2020, 10, 26));
    Task task = getOnlyTask();
    assertEquals(id, task.getId());

    Task deletedTask = manager.removeTask(id);
    assertEquals("TEST", deletedTask.getDescription());
    assertEquals(id, deletedTask.getId());
    assertEquals(LocalDate.of(2020, 10, 26), deletedTask.getDueDate());
    assertEquals(0, store.size());
  }

  private Task getOnlyTask() {
    assertEquals(1, store.size());
    return store.values().stream().findFirst().get();
  }
}
