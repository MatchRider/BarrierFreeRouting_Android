package com.disablerouting.curd_operations.model;

import java.util.List;

public enum DataHolder  {
    INSTANCE;

    public List<ListWayData> mObjectListValidate;
    public List<ListWayData> mObjectListNotValidate;

    public static boolean hasDataValidate() {
        return INSTANCE.mObjectListValidate != null;
    }

    public static void setDataValidate(List<ListWayData> objectList) {
        INSTANCE.mObjectListValidate = objectList;
    }

    public static List<ListWayData> getDataValidate() {
        return INSTANCE.mObjectListValidate;
    }

    public static boolean hasDataNotValidate() {
        return INSTANCE.mObjectListNotValidate != null;
    }

    public static void setDataNotValidate(List<ListWayData> objectList) {
        INSTANCE.mObjectListNotValidate = objectList;
    }

    public static List<ListWayData> getDataNotValidate() {
        return INSTANCE.mObjectListNotValidate;
    }
}