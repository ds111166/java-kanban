package manager;

import entities.Epic;
import entities.Status;
import entities.Subtask;
import entities.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    protected abstract void createManagerForTest();
    @AfterEach
    protected abstract void clear();
}
