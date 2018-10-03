package cn.id0755.sdk.android.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    public final static Gson INSTANCE = new GsonBuilder().serializeNulls().create();

}
