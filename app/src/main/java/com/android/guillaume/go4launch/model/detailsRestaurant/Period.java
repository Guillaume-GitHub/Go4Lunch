package com.android.guillaume.go4launch.model.detailsRestaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {
    @SerializedName("close")
    @Expose
    private CloseTime close;
    @SerializedName("open")
    @Expose
    private OpenTime open;

    public CloseTime getClose() {
        return close;
    }

    public void setClose(CloseTime close) {
        this.close = close;
    }

    public OpenTime getOpen() {
        return open;
    }

    public void setOpen(OpenTime open) {
        this.open = open;
    }
}
