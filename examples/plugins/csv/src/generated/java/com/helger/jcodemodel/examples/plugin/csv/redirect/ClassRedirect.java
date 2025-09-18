package com.helger.jcodemodel.examples.plugin.csv.redirect;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ClassRedirect {
    public ArrayIndexOutOfBoundsException e;

    /**
     * set the {@link #e}
     */
    public void setE(ArrayIndexOutOfBoundsException e) {
        this.e = e;
    }

    public void addSuppressed(Throwable arg0) {
        e.addSuppressed(arg0);
    }

    public Throwable fillInStackTrace() {
        return e.fillInStackTrace();
    }

    public Throwable getCause() {
        return e.getCause();
    }

    public String getLocalizedMessage() {
        return e.getLocalizedMessage();
    }

    public String getMessage() {
        return e.getMessage();
    }

    public StackTraceElement[] getStackTrace() {
        return e.getStackTrace();
    }

    public Throwable[] getSuppressed() {
        return e.getSuppressed();
    }

    public Throwable initCause(Throwable arg0) {
        return e.initCause(arg0);
    }

    public void printStackTrace() {
        e.printStackTrace();
    }

    public void printStackTrace(PrintStream arg0) {
        e.printStackTrace(arg0);
    }

    public void printStackTrace(PrintWriter arg0) {
        e.printStackTrace(arg0);
    }

    public void setStackTrace(StackTraceElement[] arg0) {
        e.setStackTrace(arg0);
    }
}
