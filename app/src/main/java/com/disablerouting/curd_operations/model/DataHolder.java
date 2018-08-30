package com.disablerouting.curd_operations.model;

import java.util.List;

public enum DataHolder {
    INSTANCE;

    private List<ListWayData> mObjectListValidate;
    private List<ListWayData> mObjectListNotValidate;

    public static boolean hasDataValidate() {
        return INSTANCE.mObjectListValidate != null;
    }

    public static void setDataValidate(final List<ListWayData> objectList) {
        INSTANCE.mObjectListValidate = objectList;
    }

    public static List<ListWayData> getDataValidate() {
        // INSTANCE.mObjectListValidate = null;
        return INSTANCE.mObjectListValidate;
    }

    public static boolean hasDataNotValidate() {
        return INSTANCE.mObjectListNotValidate != null;
    }

    public static void setDataNotValidate(final List<ListWayData> objectList) {
        INSTANCE.mObjectListNotValidate = objectList;
    }

    public static List<ListWayData> getDataNotValidate() {
        // INSTANCE.mObjectListNotValidate = null;
        return INSTANCE.mObjectListNotValidate;
    }
}