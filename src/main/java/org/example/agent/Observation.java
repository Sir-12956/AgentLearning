package org.example.agent;

public class Observation {

    private final String type = "observation";
    private final String tool;
    private final boolean success;
    private final Object output;
    private final String error;

    private Observation(String tool, boolean success, Object output, String error) {
        this.tool = tool;
        this.success = success;
        this.output = output;
        this.error = error;
    }

    public static Observation success(String tool, Object output) {
        return new Observation(tool, true, output, null);
    }

    public static Observation failure(String tool, String error) {
        return new Observation(tool, false, null, error);
    }

    public String getType() {
        return type;
    }

    public String getTool() {
        return tool;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }
}
