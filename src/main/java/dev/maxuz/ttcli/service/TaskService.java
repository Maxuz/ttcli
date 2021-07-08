package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.model.Task;

import java.util.List;

/**
 * Service for handling tasks actions
 */
public interface TaskService {
    /**
     * Adding the task to the current list
     * @param task to add
     */
    void addTask(Task task);

    /**
     * Find the current task and stop it (change status to WAITING). If there is no a task with state IN_PROGRESS, just
     * do nothing.
     */
    void stopCurrent();

    /**
     * Stop the task
     * @param task to stop
     */
    void stop(Task task);

    /**
     * Find a task by code
     * @param code of the task
     * @return task, or null
     */
    Task getTask(String code);

    /**
     * Stops all the task with state IN_PROGRESS
     */
    void stopAll();

    /**
     * Starts the task
     * @param task to start
     */
    void start(Task task);

    /**
     * Get all tasks
     * @return list of all tasks
     */
    List<Task> getTasks();
}
