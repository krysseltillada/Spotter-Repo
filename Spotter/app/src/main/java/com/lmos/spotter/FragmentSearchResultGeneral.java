package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by linker on 02/06/2017.
 */

public class FragmentSearchResultGeneral extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.search_result_general, container, false);

        return thisView;
    }
}
