package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "add", description = "Add a new task to the task list.")
public class AddCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final Printer printer;

    public AddCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    // parameters
    private String code;

    @Parameters(index = "0", description = "Code of a task to create. This code you will be use to manage the task")
    public void setCode(String code) {
        this.code = code;
    }

    // options
    private boolean startImmediately = false;

    @Option(names = { "--start" }, description = "Change the current task state to WAITING and set the status for this task as IN_PROGRESS")
    public void setStartImmediately(boolean startImmediately) {
        this.startImmediately = startImmediately;
    }

    @Override
    public void run() {
        Task task = createTask();
        if (startImmediately) {
            taskService.stopCurrent();
        }
        taskService.addTask(task);
        printer.info("Task {} added successfully", task.getCode());
    }

    private Task createTask() {
        Task task = new Task();
        task.setCode(code);
        task.setState(getTaskState());
        return task;
    }

    private TaskState getTaskState() {
        if (startImmediately) {
            return TaskState.IN_PROGRESS;
        }
        return TaskState.WAITING;
    }
}
