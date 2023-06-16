# java-kanban
Repository for homework project.
##### Сделано по замечаеиям 1:
1. Перенес history в manager
2. protected File taskStore -> private final File taskStore;
3. создал package manager.utilities;, class CSVTaskFormat
4. создал package entities -> enum TaskType { TASK, SUBTASK, EPIC }
5. добавил в Task, Epic, Subtask -> TaskType getType() { return TaskType.TASK(EPIC, SUBTASK); }
6. в Task, Epic, Subtask вернул старые toString
7. создал в CSVTaskFormat->static String toString(Task task) и static Task taskFromString(String value) и String getHeader()
8. создал в CSVTaskFormat-> static List<Integer> historyFromString(String value) и
static String historyToString(HistoryManager historyManager)
9. FileBackedTasksManager -> убрал конструктор без параметров
