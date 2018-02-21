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
        errorConstantMap.put(401, R.string.AUTHORISE_FIELD_MISSING);
        errorConstantMap.put(405, R.string.HTTP_METHOD_NOT_SUPPORTED);
        errorConstantMap.put(413, R.string.REQUEST_IS_LARGER);
        errorConstantMap.put(500, R.string.INCORRECT_CANT_PROCESS);
        errorConstantMap.put(501, R.string.SERVER_DOESNT_SUPPORT);
        errorConstantMap.put(503, R.string.SERVER_NOT_UAVAILABLE);

        errorConstantMap.put(200, R.string.UNABLE_TO_PARSE);
        errorConstantMap.put(201, R.string.PARAMETER_MISSING);
        errorConstantMap.put(202, R.string.INVALID_PARAMETER_FORMAT);
        errorConstantMap.put(203, R.string.INVALID_PARAMETER_VALUE);
        errorConstantMap.put(204, R.string.PARAMETER_EXCEEDS_LIMIT);
        errorConstantMap.put(299, R.string.UNKNOWN_INTERNAL_ERROR);
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
