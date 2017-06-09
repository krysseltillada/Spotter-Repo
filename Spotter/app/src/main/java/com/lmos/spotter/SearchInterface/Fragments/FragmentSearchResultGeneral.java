package com.lmos.spotter.SearchInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.GeneralResultsAdapter;

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
