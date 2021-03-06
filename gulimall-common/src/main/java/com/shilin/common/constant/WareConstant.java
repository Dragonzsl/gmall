package com.shilin.common.constant;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-22 12:05:08
 */
public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"),
        FINISHED(3, "已完成"),
        HAS_ERROR(4, "有异常");

        private final int code;
        private final String msg;

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }


        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISHED(3, "已完成"),
        HAS_ERROR(4, "采购失败");

        private final int code;
        private final String msg;

        PurchaseDetailEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }


        public String getMsg() {
            return msg;
        }
    }
}
