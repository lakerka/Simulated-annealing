package root;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    private final String NEWLINE = "\n";
    private final String SEP = "\t";
    private boolean isFinalised = false;
    private long time = 0;
    private PrintWriter infoWriter;
    private PrintWriter errorWriter;

    public Logger(String logFilename, String errFilename) {
        super();
        try {
            this.infoWriter = new PrintWriter(logFilename);
            if (errFilename != null) {
                this.errorWriter = new PrintWriter(errFilename);
            }
        } catch (Exception e) {
            sep("Logger failed during initialization!", e);
        }
    }

    public Logger(String logFilename) {
        this(logFilename, null);
    }

    public Logger(PrintWriter infoWriter) {
        super();
        this.infoWriter = infoWriter;
    }

    private void writeTime(PrintWriter pw) {
        if (isFinalised) {
            throw new IllegalStateException(
                    "Trying to write to finalised stream!");
        }
        time += 1;
        pw.write(String.valueOf(time) + SEP);
    }

    public void logInfo(String str) {
//        writeTime(infoWriter);
        infoWriter.write(str + NEWLINE);
    }

    public void logError(String s, Exception e) {
        if (errorWriter == null) {
            return;
        }
        writeTime(errorWriter);
        errorWriter.write("Error: " + s + NEWLINE);
        e.printStackTrace(errorWriter);
        errorWriter.write(NEWLINE);
    }

    private void sep(String s, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString(); // stack trace as a string
        System.err.println("Error: " + s);
        System.err.println("Stack trace: " + stackTrace);
    }

    public void finalise() {
        isFinalised = true;
        closePrintWriter(infoWriter);
        closePrintWriter(errorWriter);
    }

    private void closePrintWriter(PrintWriter pw) {
        if (pw != null) {
            pw.close();
        }
    }
}
