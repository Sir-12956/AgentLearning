package org.example.tool;

public class EchoTool implements Tool {
    @Override
    public String name() { return "echo"; }
    @Override
    public String execute(String input) { return "Echo: " + input; }
}
