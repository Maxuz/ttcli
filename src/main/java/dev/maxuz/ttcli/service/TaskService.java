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
}
