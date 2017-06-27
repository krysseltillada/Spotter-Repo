package com.lmos.spotter.AccountInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linker on 02/06/2017.
 * sadsadas
 */

public class FragmentRecover extends Fragment {

    TextInputEditText username, email;
    Button recover_acc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.recover_account_fragment, container, false);

        username = (TextInputEditText) thisView.findViewById(R.id.recover_username);
        email = (TextInputEditText) thisView.findViewById(R.id.recover_email);
        recover_acc = (Button) thisView.findViewById(R.id.recover_account_btn);
        recover_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, String> map_data = new HashMap<String, String>() {{

                    put("username", username.getText().toString());
                    put("email", email.getText().toString());

                }};

                ((LoginActivity) getContext()).runAccountHandler(
                        "send-mail-recovery.php",
                        map_data
                );
            }

        });

        return thisView;
    }
}
