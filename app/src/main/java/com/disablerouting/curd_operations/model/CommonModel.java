package com.disablerouting.curd_operations.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CommonModel implements Parcelable {

    private List<ListWayData> mListWayDataList;

    private List<NodeReference> mNodeReferenceList;


    public CommonModel() {
    }

    public CommonModel(Parcel in) {
        mListWayDataList = in.createTypedArrayList(ListWayData.CREATOR);
        mNodeReferenceList = in.createTypedArrayList(NodeReference.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mListWayDataList);
        dest.writeTypedList(mNodeReferenceList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonModel> CREATOR = new Creator<CommonModel>() {
        @Override
        public CommonModel createFromParcel(Parcel in) {
            return new CommonModel(in);
        }

        @Override
        public CommonModel[] newArray(int size) {
            return new CommonModel[size];
        }
    };

    public List<ListWayData> getListWayDataList() {
        return mListWayDataList;
    }

    public void setListWayDataList(List<ListWayData> listWayDataList) {
        mListWayDataList = listWayDataList;
    }

    public List<NodeReference> getNodeReferenceList() {
        return mNodeReferenceList;
    }

    public void setNodeReferenceList(List<NodeReference> nodeReferenceList) {
        mNodeReferenceList = nodeReferenceList;
    }
}
