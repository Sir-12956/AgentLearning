package org.example.tool;

import java.util.Random;

public class RandomNumberTool implements Tool {
    @Override
    public String name() { return "random_number"; }
    @Override
    public String execute(String input) {
        Random r = new Random();
        return String.valueOf(r.nextInt(100));
    }
}
