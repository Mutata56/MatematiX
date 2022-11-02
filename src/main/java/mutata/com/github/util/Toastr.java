package mutata.com.github.util;

import java.util.Arrays;

public class Toastr {

    public static String showError(String header,String message,String... options) {
        return parse(Action.ERROR,header,message,options);
    }

    public static String showWarning(String header,String message,String... options) {
        return parse(Action.WARNING,header,message,options);
    }

    public static String showSuccess(String header,String message,String... options) {
        return parse(Action.SUCCESS,header,message,options);
    }

    public static String showInfo(String header,String message,String... options) {
        return parse(Action.INFO,header,message,options);
    }

    private static String parseOptions(String... options) {
        final String preString = "toastr.options.";
        StringBuilder builder = new StringBuilder();
        Arrays.stream(options).forEach(str -> builder.append(preString).append(str).append("\n"));
        return builder.toString();
    }

    private static String parse(Action action,String header,String message,String... options) {
        return "$(function(){\n" +
                parseOptions(options) + "\n" +
                "toastr." + action.action + "(\"" +message + "," + header + "\")"  ;
    }
    private enum Action {
        SUCCESS("success"),WARNING("warning"),ERROR("error"),INFO("info");
        private final String action;
        Action(String action) {
            this.action = action;
        }
    }
}
