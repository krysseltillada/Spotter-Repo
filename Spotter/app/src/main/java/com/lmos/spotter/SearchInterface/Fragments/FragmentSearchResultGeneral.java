package com.lmos.spotter.SearchInterface.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.GeneralResultsAdapter;
import com.lmos.spotter.Utilities.Utilities;

/**
 * Created by linker on 02/06/2017.
 */

public class FragmentSearchResultGeneral extends Fragment {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    GeneralResultsAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.search_result_general, container, false);

        tabLayout = (TabLayout) thisView.findViewById(R.id.search_tab_layout);
        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        mAdapter = new GeneralResultsAdapter();
        layoutManager = new LinearLayoutManager(getContext());

        // Set RecyclerView onClickListener
        mAdapter.setOnItemClickListener(new GeneralResultsAdapter.OnClickListener() {
            @Override
            public void OnItemClick(int pos, View view, String... params) {
                Toast.makeText(getContext(), String.valueOf(pos) +
                        " " + params[0] + " " + params[1] + params[2]
                        , Toast.LENGTH_SHORT).show();
                Log.d("LocationHandler", new Utilities.LocationHandler(getActivity()).findLocation());
            }

            @Override
            public void OnItemLongClick(int pos, View view) {
                Toast.makeText(getContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();
            }
        });

        // Set recyclerview params
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        /**
         * This method will allow the RecyclerView to scroll
          * smoothly inside NestedScrollView
         **/
        recyclerView.setNestedScrollingEnabled(false);

        return thisView;
    }

}
