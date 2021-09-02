package dev.maxuz.ttcli.datatprovider;

import dev.maxuz.ttcli.model.Task;

import java.util.List;

public interface TaskDataProvider {
    /**
     * Saves the task into a storage
     * @param task to save
     */
    void saveTask(Task task);

    /**
     * Find a task with state IN_PROGRESS
     * @return task, or null
     */
    Task getTaskInProgress();

    /**
     * Get all tasks
     * @return all tasks
     */
    List<Task> getTasks();

    /**
     * Remove all tasks
     */
    void clean();
}
