package com.college.admission.exception;

public class AccountLockedException extends AuthenticationException {

    public AccountLockedException(String message) {
        super(message);
    }
}