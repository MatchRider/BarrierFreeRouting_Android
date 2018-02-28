package com.disablerouting.api;

import android.support.annotation.NonNull;
import com.disablerouting.R;
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
                    mResponseCallback.onFailure(parseError(errorBodyPayload));
                } else {
                    mResponseCallback.onFailure(new ErrorResponse(500, R.string.INCORRECT_CANT_PROCESS));
                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponseCallback.onFailure(new ErrorResponse(500, R.string.INCORRECT_CANT_PROCESS));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
        ErrorResponse errorResponse;
        if (throwable instanceof ConnectException
                || throwable instanceof UnknownHostException) {
            errorResponse = new ErrorResponse(503, R.string.SERVER_DOESNT_SUPPORT);
        } else {
            // some more complex error occurred like conversion etc.
            errorResponse = new ErrorResponse(503, R.string.SERVER_DOESNT_SUPPORT);
        }
        mResponseCallback.onFailure(errorResponse);
    }

    /**
     * Parse error response
     * @param errorBodyPayload Error body
     * @return Return error response
     */
    private ErrorResponse parseError(String errorBodyPayload) {
        try {
            JSONObject jsonObject = new JSONObject(errorBodyPayload);
            JSONObject error = jsonObject.optJSONObject("error");
            int errorCode=0;
            if(error.has("code")) {
                errorCode = error.optInt("code");
            }
            int errorMessage;
            if (errorCode != 0) {
                errorMessage = ApiErrorHandler.resolve(errorCode);
            } else {
                errorMessage = R.string.INTERNAL_SERVER_ERROR; // Unable to process request at this time, please try again in sometime.
            }
            return new ErrorResponse(errorCode, errorMessage);
        } catch (JSONException e) {
            return new ErrorResponse(500, R.string.INTERNAL_SERVER_ERROR); // Unable to process request at this time.
        }
    }

}
