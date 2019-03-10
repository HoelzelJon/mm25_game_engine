package mech.mania;

public class SystemIO {
    private static final boolean DEBUG = true;

    public static void print(String toPrint, boolean debug) {
        if (DEBUG || !debug) {
            System.out.println(toPrint);
        }
    }
}
