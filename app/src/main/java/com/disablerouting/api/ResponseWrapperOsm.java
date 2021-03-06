package com.disablerouting.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
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
public class ResponseWrapperOsm<T> implements Callback<T> {

    private final ResponseCallback<T> mResponseCallback;
    private final Context mContext;
    private ProgressDialog progressDoalog;


    private String OPS_SOMETHING_WENT_WRONG ="Ops Something went wrong. Please try again after sometime.";
    /**
     * Creates an instance without the error mapper,
     * in case of all errors we would get the default response.
     * @param responseCallback implementation of the response callback.
     */
    public ResponseWrapperOsm(ResponseCallback<T> responseCallback , Context context) {
        mResponseCallback = responseCallback;
        mContext=context;
      /*  // Set up progress before call
        progressDoalog = new ProgressDialog(context);
        progressDoalog.setMax(100);
        progressDoalog.setIndeterminate(true);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // show it
        progressDoalog.show();*/
    }

    /** {@inheritDoc} */
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {


        /*if(progressDoalog.isShowing()) {
            progressDoalog.dismiss();
        }*/
        if (response.isSuccessful() && response.body()!= null) {
            mResponseCallback.onSuccess(response.body());
        } else {
            String errorBodyPayload = null;
            try {
                if(response.errorBody()!=null) {
                    errorBodyPayload = response.errorBody().string();
                }
                if (errorBodyPayload != null) {
                    mResponseCallback.onFailure(parseErrorOSM(errorBodyPayload));
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
       /* if(progressDoalog.isShowing()) {
            progressDoalog.dismiss();
        }*/
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

    /**
     * Parse error OSM
     * @param errorBodyPayload string error
     * @return string error
     */
    private ErrorResponse parseErrorOSM(String errorBodyPayload) {

            if (errorBodyPayload != null) {
                return new ErrorResponse(errorBodyPayload);
            } else {
                errorBodyPayload = OPS_SOMETHING_WENT_WRONG; // Unable to process request at this time, please try again in sometime.
            }
            return new ErrorResponse(errorBodyPayload);
    }

}
