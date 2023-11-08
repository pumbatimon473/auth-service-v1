package com.lld.authservicev1.exceptions;

public class InvalidToken extends Exception {
    public InvalidToken(String msg) {
        super(msg);
    }
}
