package com.android.guillaume.go4launch;

import com.android.guillaume.go4launch.model.UserLunch;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class UserLunchTest {



    @Test
    public void setAndGetDate() {
        UserLunch userLunch = new UserLunch();
        userLunch.setDate(Calendar.getInstance().getTime().toString());
        Assert.assertEquals(Calendar.getInstance().getTime().toString(), userLunch.getDate());
    }

    @Test
    public void setAndGetPlaceID() {
        String placeID = "JHBhjgf8k434D79-sjd_KG6h";
        UserLunch userLunch  = new UserLunch(null,placeID,null,null);
        Assert.assertEquals(placeID, userLunch.getPlaceID());
    }


    @Test
    public void setAndGetName() {
        String name = "Le ZINC";
        UserLunch userLunch  = new UserLunch(null,null,name,null);
        Assert.assertEquals(name,userLunch.getName());
    }

    @Test
    public void setAndGetAddress() {
        String address = "546d, rue Junit";
        UserLunch userLunch  = new UserLunch(null,null,null,address);
        Assert.assertEquals(address,userLunch.getAddress());
    }

}