package com.disablerouting.api;

public class ErrorResponse {

    private final int mErrorMessage;
    private int mErrorCode;
    private String error;

    /**
     * Creates an instance of the error.
     * @param errorMessage the error message equivalent.
     * @param errorCode the error code
     */
    public ErrorResponse(int errorCode , int errorMessage) {
        mErrorMessage = errorMessage;
        mErrorCode = errorCode;
    }
    
    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    public int getErrorMessage() {
        return mErrorMessage;
    }
    
    /** Get error code */
    public int getErrorCode() {
        return mErrorCode;
    }
    
    /** Set error code */
    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
