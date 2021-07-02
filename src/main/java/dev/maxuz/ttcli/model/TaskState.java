package dev.maxuz.ttcli.model;

/**
 * Task state
 */
public enum TaskState {
    /**
     * This state indicates that we are working on the task
     */
    IN_PROGRESS,

    /**
     * This state indicates that the task is waiting for an action
     */
    WAITING
}
