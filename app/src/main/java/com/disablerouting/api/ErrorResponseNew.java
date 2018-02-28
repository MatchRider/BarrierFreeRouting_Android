package com.disablerouting.api;

public class ErrorResponseNew {

    private final String mErrorMessage;
    private String error;

    /**
     * Creates an instance of the error.
     * @param errorMessage the error message equivalent.
     */
    public ErrorResponseNew(String errorMessage) {
        mErrorMessage = errorMessage;
    }
    
    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
