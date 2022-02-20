package com.shilin.gulimall.member.exception;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-19 19:02:44
 */
public class UsernameExistException extends RuntimeException {
    public UsernameExistException(String message) {
        super(message);
    }
}
