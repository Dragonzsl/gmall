package com.shilin.gulimall.member.exception;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-19 19:03:04
 */
public class MobileExistException extends RuntimeException {
    public MobileExistException(String message) {
        super(message);
    }
}
