package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.model.Task;

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
}
