package dev.maxuz.ttcli.datatprovider.dto;

/**
 * Task state
 */
public enum TaskStateTO {
    /**
     * This state indicates that we are working on the task
     */
    IN_PROGRESS,

    /**
     * This state indicates that the task is waiting for an action
     */
    WAITING
}
