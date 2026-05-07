/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 * 						http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 */
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
