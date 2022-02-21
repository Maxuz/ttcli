package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.question.InteractiveQuestionnaire;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "start", description = "Setting the start time and change status to the IN_PROGRESS")
public class StartCommand extends AbstractCommand implements SubCommand, Runnable {

    private final TaskDayService taskDayService;
    private final TaskService taskService;
    private final Printer printer;
    private final InteractiveQuestionnaire questionnaire;

    public StartCommand(TaskDayService taskDayService, TaskService taskService, Printer printer, InteractiveQuestionnaire questionnaire) {
        super(taskDayService, taskService, printer);
        this.taskDayService = taskDayService;
        this.taskService = taskService;
        this.printer = printer;
        this.questionnaire = questionnaire;
    }

    // parameters
    private String name;

    @Parameters(index = "0", description = "Name of a task to start")
    public void setName(String name) {
        this.name = name;
    }

    // options
    private boolean stopOthers = false;

    @Option(names = {"-s", "--stop-others"}, description = "Stops all other tasks")
    public void setStopOthers(boolean stopOthers) {
        this.stopOthers = stopOthers;
    }

    @Override
    public void run() {
        TaskDay taskDay = taskDayService.getCurrentDay();
        if (taskDay == null) {
            boolean createNewDay = questionnaire.askStartNewDay();
            if (createNewDay) {
                taskDay = startDay();
            } else {
                throw new TtRuntimeException("Day is not started, so there is no tasks. Exiting.");
            }
        }
        Task task = taskService.getTask(taskDay, name);
        if (task == null) {
            boolean answer = questionnaire.createTask(name);
            if (answer) {
                task = createTask(taskDay, name);
            } else {
                throw new TtRuntimeException("Task with name [" + name + "] is not found");
            }
        }

        if (stopOthers) {
            taskService.stop(taskDay);
            printer.info("All tasks stopped");
        }
        taskService.start(task);

        taskDayService.save(taskDay);
        printer.info("Task {} started", task.getName());
    }
}
