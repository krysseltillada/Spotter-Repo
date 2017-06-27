package com.lmos.spotter.SearchInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;

/**
 * Created by linker on 06/06/2017.
 */

public class FragmentSearchResult extends Fragment{

    private RecyclerView placeImageList;

    public static FragmentSearchResult newInstance(String... params){

        FragmentSearchResult fragmentSearchResult = new FragmentSearchResult();

        return fragmentSearchResult;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View thisView = inflater.inflate(R.layout.search_result_hrt, container, false);



         return thisView;
    }
}
