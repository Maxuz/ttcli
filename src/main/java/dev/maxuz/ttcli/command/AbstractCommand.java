package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;

import java.time.LocalDate;

class AbstractCommand {
    private final TaskDayService taskDayService;
    private final TaskService taskService;
    private final Printer printer;

    AbstractCommand(TaskDayService taskDayService, TaskService taskService, Printer printer) {
        this.taskDayService = taskDayService;
        this.taskService = taskService;
        this.printer = printer;
    }

    protected TaskDay startDay() {
        TaskDay taskDay;
        taskDay = new TaskDay(LocalDate.now());
        taskDayService.save(taskDay);
        printer.info("Day is started");
        return taskDay;
    }

    protected Task createTask(TaskDay taskDay, String name) {
        Task task = new Task();
        task.setName(name);
        task.setState(TaskState.WAITING);

        taskService.addTask(taskDay, task);
        taskDayService.save(taskDay);
        printer.info("Task [" + name + "] is created");

        return task;
    }
}
