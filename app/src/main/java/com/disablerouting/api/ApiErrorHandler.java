package com.disablerouting.api;


import android.annotation.SuppressLint;
import com.disablerouting.R;

import java.util.HashMap;
import java.util.Map;

class ApiErrorHandler {

    private static final Map<Integer, Integer> ERROR_MAP = createErrorMap();

    private static Map<Integer, Integer> createErrorMap() {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Integer> errorConstantMap = new HashMap<>();
        errorConstantMap.put(401, R.string.INTERNAL_SERVER_ERROR);
        return errorConstantMap;
    }

    /**
     * Checks whether the look up table contains the message entry for the given error code.
     * @param errorCode the error code.
     * @return true if the mapping exists false otherwise.
     */
    private static boolean isResolvable(int errorCode) {
        return ERROR_MAP.containsKey(errorCode);
    }


    /**
     * Resolves the error code to the display message.
     * @param errorCode the error code.
     * @return the translated message equivalent of the error code.
     */
    public static int resolve(int errorCode) {
        return isResolvable(errorCode) ? ERROR_MAP.get(errorCode) : R.string.INTERNAL_SERVER_ERROR;
    }
}
