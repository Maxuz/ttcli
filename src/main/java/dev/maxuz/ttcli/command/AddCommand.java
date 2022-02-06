package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.question.InteractiveQuestionnaire;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.time.LocalDate;

@Component
@Command(name = "add", description = "Add a new task to the task list.")
public class AddCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final TaskDayService taskDayService;
    private final Printer printer;
    private final InteractiveQuestionnaire questionnaire;

    public AddCommand(TaskDayService taskDayService, TaskService taskService, Printer printer, InteractiveQuestionnaire questionnaire) {
        this.taskService = taskService;
        this.taskDayService = taskDayService;
        this.printer = printer;
        this.questionnaire = questionnaire;
    }

    // parameters
    private String name;

    @Parameters(index = "0", description = "Name of a task to create. This name you will be use to manage the task")
    public void setName(String name) {
        this.name = name;
    }

    // options
    private boolean startImmediately = false;

    @Option(names = {"-s", "--start"}, description = "Change the current task state to WAITING and set the status for this task as IN_PROGRESS")
    public void setStartImmediately(boolean startImmediately) {
        this.startImmediately = startImmediately;
    }

    private boolean startNewDay = false;

    @Option(names = {"-start-day", "--start-day"}, description = "Start a new day and add the task in this day")
    public void setStartNewDay(boolean startNewDay) {
        this.startNewDay = startNewDay;
    }

    @Override
    public void run() {
        TaskDay taskDay;
        if (startNewDay) {
            taskDay = createTaskDay();
        } else {
            taskDay = taskDayService.getCurrentDay();
            if (taskDay == null) {
                boolean answer = questionnaire.askStartNewDay();
                if (answer) {
                    taskDay = createTaskDay();
                } else {
                    throw new TtRuntimeException("New day is required. Exiting.");
                }
            }
        }
        Task task = createTask();
        taskService.addTask(taskDay, task);

        if (startImmediately) {
            taskService.stop(taskDay);
            taskService.start(task);
        }
        taskDayService.save(taskDay);
        printer.info("Task {} added", task.getName());
    }

    private TaskDay createTaskDay() {
        TaskDay taskDay;
        taskDay = new TaskDay(LocalDate.now());
        taskDayService.save(taskDay);
        return taskDay;
    }

    private Task createTask() {
        Task task = new Task();
        task.setName(name);
        task.setState(TaskState.WAITING);
        return task;
    }
}
