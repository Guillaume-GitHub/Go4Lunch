package com.android.guillaume.go4launch;

import com.android.guillaume.go4launch.model.DatabaseRestaurantDoc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseRestaurantDocTest {

    @Test
    public void setAndGetDate() {
        DatabaseRestaurantDoc restaurantDoc = new DatabaseRestaurantDoc();
        restaurantDoc.setDate(Calendar.getInstance().getTime().toString());
        assertEquals(Calendar.getInstance().getTime().toString(),restaurantDoc.getDate());
    }


    @Test
    public void setAndGetPlaceID() {
        DatabaseRestaurantDoc restaurantDoc = new DatabaseRestaurantDoc();
        restaurantDoc.setPlaceID("azeRFG356_HKD9746y");
        assertEquals("azeRFG356_HKD9746y",restaurantDoc.getPlaceID());
    }

    @Test
    public void setAndGetUsers() {
        List<String> usersID = new ArrayList<>();
        usersID.add("ARTE6bsf46h89_ksh");
        usersID.add("sdkjhgfskdh77hRRTE630");
        DatabaseRestaurantDoc restaurantDoc = new DatabaseRestaurantDoc();
        restaurantDoc.setUsers(usersID);
        assertEquals("sdkjhgfskdh77hRRTE630",restaurantDoc.getUsers().get(1));

    }

}