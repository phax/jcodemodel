package com.helger.jcodemodel.tests.tryresource;

public class BasicTry {

    public void close(ConciseTryTestGen.NoErrorCloseable p) {
        try(p) {
        }
    }
}
