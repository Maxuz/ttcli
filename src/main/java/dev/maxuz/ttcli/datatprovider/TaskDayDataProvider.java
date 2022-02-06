package dev.maxuz.ttcli.datatprovider;

import dev.maxuz.ttcli.model.TaskDay;

import java.time.LocalDate;
import java.util.List;

public interface TaskDayDataProvider {
    /**
     * Save day into a storage
     *
     * @param taskDay to save
     */
    void save(TaskDay taskDay);

    /**
     * @return all days in a storage, ordered by date (newest in the beginning)
     */
    List<TaskDay> findAll();
}
