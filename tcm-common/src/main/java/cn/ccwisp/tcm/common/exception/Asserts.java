package cn.ccwisp.tcm.common.exception;

import cn.ccwisp.tcm.common.api.IStatusCode;

public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IStatusCode statusCode) {
        throw new ApiException(statusCode);
    }
}
