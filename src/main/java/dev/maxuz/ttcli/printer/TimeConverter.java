package dev.maxuz.ttcli.printer;

import org.springframework.stereotype.Service;

@Service
public class TimeConverter {

    public String convert(long millis) {
        long hours = 0;
        long minutes = 0;
        long seconds = millis / 1000;
        if (seconds / 60 > 0) {
            minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
        }
        if (minutes / 60 > 0) {
            hours = minutes / 60;
            minutes = minutes - (hours * 60);
        }

        return String.format("%01dh %01dm %01ds", hours, minutes, seconds);
    }
}
