package org.example.tool;

import java.time.LocalDateTime;

public class TimeTool implements Tool {

    @Override
    public String name() {
        return "current_time";
    }

    @Override
    public String execute(String input) {
        return LocalDateTime.now().toString();
    }
}
