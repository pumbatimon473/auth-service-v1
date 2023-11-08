package com.lld.authservicev1.exceptions;

public class InvalidLoginCredentials extends Exception {
    public InvalidLoginCredentials(String msg) {
        super(msg);
    }
}
