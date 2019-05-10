package com.android.guillaume.go4launch;

import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.UserLunch;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;

import static org.junit.Assert.*;

public class UserTest {
    private User user = new User("6Hdnd683_63Gdgsjhd7", "MARTIN", "martin@gmail.com",null, null, null);
    @Test
    public void setAndGetUid() {
        user.setUid("UJNHJNBH?JKKKLL87556");
        assertEquals("UJNHJNBH?JKKKLL87556",user.getUid());
    }

    @Test
    public void setAndGetUserName() {
        user.setUserName("UserNameTest");
        assertEquals("UserNameTest",user.getUserName());
    }

    @Test
    public void setAndGetEmail() {
        user.setUserName("testemail@gmail.com");
        assertEquals("testemail@gmail.com",user.getUserName());
    }

    @Test
    public void setAndGetUrlPicture() {
        user.setUserName("testemail@gmail.com");
        assertEquals("testemail@gmail.com",user.getUserName());
    }

    @Test
    public void setAngGetLunch() {
        UserLunch userLunch = new UserLunch(Calendar.getInstance().getTime().toString(), "YHBBEKdb763Jjdh", "Restaurant Test", "Address Test");
        user.setLunch(userLunch);
        assertEquals(userLunch,user.getLunch());
    }

    @Test
    public void setAndGetLike() {
        List<String> restaurantLikeID = new ArrayList<>();
        restaurantLikeID.add("ARTE6bsf46h89_ksh");
        restaurantLikeID.add("sdkjhgfskdh77hRRTE630");

        user.setLike(restaurantLikeID);
        assertEquals("ARTE6bsf46h89_ksh",user.getLike().get(0));
        assertEquals("sdkjhgfskdh77hRRTE630",user.getLike().get(1));
    }
}