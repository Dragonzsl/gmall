package com.shilin.common.exception;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-02 15:03:26
 */
public class NoStockException extends RuntimeException {
    public NoStockException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
