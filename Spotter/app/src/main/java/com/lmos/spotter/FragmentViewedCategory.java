package com.lmos.spotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FragmentViewedCategory extends Fragment {

    ListView mostViewedList;
    ProgressBar progressBar;
    View view;

    public FragmentViewedCategory() {
        // Required empty public constructor
    }

    public static FragmentViewedCategory newInstance(String param1, String param2) {
        return new FragmentViewedCategory();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_viewed_category, container, false);
       // mostViewedList = (ListView) view.findViewById(R.id.hotelMostViewedList);
        progressBar = (ProgressBar) view.findViewById(R.id.hotelLoadingBar);

        new ListLoader().execute();

        return view;

    }


    private class ListLoader extends AsyncTask<Void, Void, Void> {


        View view;

        public ListLoader() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

          //  mostViewedList.setAdapter(new ListHotelAdapter(getContext(), R.layout.place_item_list, new String[100]));



        }
    }

}
