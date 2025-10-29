## Task Management System

This is a console-based application built in Java for managing personal tasks. It tests skills in object-oriented design, data structures, Java 8 streams, and basic console I/O.

-----

## Design Choices

Thread Safety was an optional requirement for this test and while I'm fairly confident this is a relatively thread safe implementation.  I have not been able to throughly readon about this due to time constraints.  It is possible that some simple object lock synchronization is still needed.
I made the decision to not have a pure update and instead delete and recreate with a new ID.
This will eat IDs faster than doing a pure update but is a simple and clean way to avoid having to lock when performing write operations.  This could also be a useful if we ever design choice if we ever implement an "Undo" feature.

### Data Structure Selection

| Task Storage | ConcurrentHashMap<Integer, Task> | Used for O(1) average time complexity for key operations like `addTask`, `removeTask`, and `markTaskCompleted` (lookup by ID). The `Concurrent` aspect addresses the requirement for thread safety, ensuring reliable concurrent access and modification.
| ID Generation | AtomicInteger | Ensures that task IDs are unique and thread-safe across the entire application, even if multiple threads attempt to create tasks simultaneously. |
| Task Creation | Immutable Builder Pattern | Provides a flexible and readable way to construct Task objects. It centralizes input validation before the object is created, helping to maintain the invariant that a Task is always valid. |


### Error Handling

Custom exceptions (`TaskNotFoundException` and `InvalidTaskException`) are used to clearly communicate specific operational failures (e.g., trying to complete a non-existent task) to the calling code and the end-user. These are not runtime exceptions because most of the exceptions should be recoverable.

-----

## Running and Testing with Gradle

This project assumes a standard Gradle build structure.

### Prerequisites

  * **Java Development Kit (JDK) 21+**
  * **Gradle** (or use the provided Gradle Wrapper: `gradlew`)

### Running the Application

1.  **Build the project:**

    ```bash
    ./gradlew build
    ```

2.  **Run the application:**

    ```bash
    ./gradlew run
    ```

    This will launch the interactive console menu.

### Running Unit Tests

1.  **Execute tests:**

    ```bash
    ./gradlew test
    ```

    Gradle will compile the test code, run all JUnit tests, and generate a report.

-----

## Assumptions and Enhancements

### Assumptions Made

  * Using the natural order of enums was enough for this task.  This probably isnt what you would do once persistance is involved.
  * Date Format: All user input for the due date is strictly expected to be in the `YYYY-MM-DD` format.
  * Case Sensitivity: Priority input (e.g., "HIGH") is case-insensitive in the console menu (converted to uppercase before parsing).

### Ideas for Future Enhancements

1.  Persistence: Adding a database to maintain task records
2.  Editing Tasks: Add a new menu option to allow users to modify a task's description, priority, or due date by ID.
3.  User Interface: Upgrade from a CLI to a GUI or web portal.
4.  Recurrence: Extend the `Task` model to include support for recurring tasks (e.g., daily, weekly).
5.  Create a REST or GRPC API around the basic functionality.
6.  Distributed Multi User:  Build it out to deployed to the cloud and expand to be multi-user.

-----