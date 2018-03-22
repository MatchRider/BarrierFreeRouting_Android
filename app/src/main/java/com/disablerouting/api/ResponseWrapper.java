package com.disablerouting.api;

import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * A wrapper layer over the retrofit callback, written for distinguishing the success
 * and failure responses.
 * @param <T> the class type of the success response expected.
 */
public class ResponseWrapper<T> implements Callback<T> {

    private final ResponseCallback<T> mResponseCallback;

    private String OPS_SOMETHING_WENT_WRONG ="Ops Something went wrong. Please try again after sometime.";
    /**
     * Creates an instance without the error mapper,
     * in case of all errors we would get the default response.
     * @param responseCallback implementation of the response callback.
     */
    public ResponseWrapper(ResponseCallback<T> responseCallback) {
        mResponseCallback = responseCallback;
    }

    /** {@inheritDoc} */
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {

        if (response.isSuccessful() && response.body()!= null) {
            mResponseCallback.onSuccess(response.body());
        } else {
            String errorBodyPayload = null;
            try {
                if(response.errorBody()!=null) {
                    errorBodyPayload = response.errorBody().string();
                }
                if (errorBodyPayload != null) {
                    mResponseCallback.onFailure(parseErrorNew(errorBodyPayload));

                } else {
                    mResponseCallback.onFailure(new ErrorResponse(OPS_SOMETHING_WENT_WRONG));

                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponseCallback.onFailure(new ErrorResponse(OPS_SOMETHING_WENT_WRONG));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
        ErrorResponse errorResponse;
        if (throwable instanceof ConnectException
                || throwable instanceof UnknownHostException) {
            errorResponse = new ErrorResponse(OPS_SOMETHING_WENT_WRONG);
        } else {
            // some more complex error occurred like conversion etc.
            errorResponse = new ErrorResponse(OPS_SOMETHING_WENT_WRONG);

        }
        mResponseCallback.onFailure(errorResponse);
    }

    private ErrorResponse parseErrorNew(String errorBodyPayload) {
        try {
            JSONObject jsonObject = new JSONObject(errorBodyPayload);
            JSONObject error=null;
            if(jsonObject.has("error")) {
                error = jsonObject.optJSONObject("error");
            }
            String errorMessage= null;
            if(error!=null && error.has("message")) {
                errorMessage = error.optString("message");
            }
            if (errorMessage != null) {
                return new ErrorResponse(errorMessage);
            } else {
                errorMessage = OPS_SOMETHING_WENT_WRONG; // Unable to process request at this time, please try again in sometime.
            }
            return new ErrorResponse(errorMessage);
        } catch (JSONException e) {
            return new ErrorResponse(OPS_SOMETHING_WENT_WRONG); // Unable to process request at this time.
        }
    }

}
