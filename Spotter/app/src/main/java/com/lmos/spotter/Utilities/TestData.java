package com.lmos.spotter.Utilities;

import com.lmos.spotter.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kryssel on 6/18/2017.
 */

public class TestData {

    public static class PlaceData {

        public static Object[][] hotel = {

                {R.drawable.ht1, "The Farm at San Benito", "Lipa City, Batangas", 3.4, 180, 9394.00, 23967.00},
                {R.drawable.ht2, "Canyon Cove Resort", "Nasugpu Batangas", 5.0, 264, 3953.00, 7480.00},
                {R.drawable.ht3, "La Chevrerie Resort And Spa", "Mabini, Batangas", 3.1, 120, 11088.00, 11088.00},
                {R.drawable.ht4, "Club Balai Isabel", "Talisay, Batangas", 2.2, 51, 4435.00, 5335.00},
                {R.drawable.ht5, "Canyon Woods Resort Club", "Laurel Batangas", 1.0, 10, 2750.00, 6800.00}

        };

        public static Object[][]testImages = {

                {R.drawable.ht1_90x90, "Boracay", "Aklan", 5.0, 568, 500.00, 5000.00},
                {R.drawable.ht2_90x90, "Batanes Highlands", "Batanes", 4.3, 213, 600.00, 2300.00},
                {R.drawable.ht3_90x90, "Camiguin Island", "Camiguin", 3.2, 501, 500.00, 3400.00},
                {R.drawable.ht4_90x90, "Malapascua Island", "Cebu", 2.0, 10, 1000.00, 3000.00},
                {R.drawable.ht5_90x90, "Kalanggaman Island", "Leyte", 1.4, 1, 300.00, 6000.00}

        };

        public static Object[][] touristSpots = {

                {R.drawable.tt1, "Boracay", "Aklan", 5.0, 568, 500.00, 5000.00},
                {R.drawable.tt2, "Batanes Highlands", "Batanes", 4.3, 213, 600.00, 2300.00},
                {R.drawable.tt3, "Camiguin Island", "Camiguin", 3.2, 501, 500.00, 3400.00},
                {R.drawable.tt4, "Malapascua Island", "Cebu", 2.0, 10, 1000.00, 3000.00},
                {R.drawable.tt5, "Kalanggaman Island", "Leyte", 1.4, 1, 300.00, 6000.00}

        };

        public static Object[][] restaurants = {

                {R.drawable.rt1, "Lili Restaurant", "Manila", 4.5, 43, 600.00, 2000.00},
                {R.drawable.rt2, "Nobu Manila Restaurant", "Paranaque City", 4.0, 118, 500.00, 5000.00},
                {R.drawable.rt3, "Yurakuen Japanese Restaurant", "Manila", 4.5, 45, 900.00, 6000.00},
                {R.drawable.rt4, "Pepper Lunch R place Resto", "Manila", 4.0, 20, 100.00, 1000.00},
                {R.drawable.rt5, "Rose Restaurant", "Manila", 5.0, 2000.00, 11000.00}

        };

        public static ArrayList<Object[]> testDataMostViewed = new ArrayList<>(Arrays.asList(

                PlaceData.testImages[1],
                PlaceData.testImages[3],
                PlaceData.testImages[2],
                PlaceData.testImages[0],
                PlaceData.testImages[0],
                PlaceData.testImages[1],
                PlaceData.testImages[3],
                PlaceData.testImages[4],
                PlaceData.testImages[4],
                PlaceData.testImages[1]

        ));

        public static ArrayList<Object[]> testDataMostRated = new ArrayList<>(Arrays.asList(

                PlaceData.testImages[0],
                PlaceData.testImages[3],
                PlaceData.testImages[4],
                PlaceData.testImages[4],
                PlaceData.testImages[1],
                PlaceData.testImages[0],
                PlaceData.testImages[1],
                PlaceData.testImages[2],
                PlaceData.testImages[3],
                PlaceData.testImages[1]

        ));

        public static ArrayList<Object[]> testDataRecommend = new ArrayList<>(Arrays.asList(

                PlaceData.testImages[0],
                PlaceData.testImages[1],
                PlaceData.testImages[3],
                PlaceData.testImages[1],
                PlaceData.testImages[2],
                PlaceData.testImages[4],
                PlaceData.testImages[1],
                PlaceData.testImages[2],
                PlaceData.testImages[3],
                PlaceData.testImages[1]

        ));

    }

}
