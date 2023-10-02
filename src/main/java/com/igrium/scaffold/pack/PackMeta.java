package com.igrium.scaffold.pack;

import com.google.gson.annotations.SerializedName;

public class PackMeta {
    public static final int DEFAULT_PACK_FORMAT = 15;

    public static class Pack {
        @SerializedName("pack_format")
        public int packFormat = DEFAULT_PACK_FORMAT;

        @SerializedName("description")
        public String description = "";
    }

    public Pack pack = new Pack();
}
