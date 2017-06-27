package com.lmos.spotter.AccountInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by linker on 01/06/2017.
 *
 *  This class will handle the Log in Interface
 *  and Background Task of Login Activity.
 *
 */

public class FragmentSignIn extends Fragment {

    TextInputEditText username, password;
    Button sign_in;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View signInView = inflater.inflate(R.layout.sign_in_fragment, container, false);

        username = (TextInputEditText) signInView.findViewById(R.id.login_username);
        password = (TextInputEditText) signInView.findViewById(R.id.login_password);

        sign_in = (Button) signInView.findViewById(R.id.sign_in);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().length() > 6 ||
                        password.getText().toString().length() > 6) {

                    final Map<String, Object> map_data = new HashMap<String, Object>() {{

                        put("username", username.getText().toString());
                        put("password", password.getText().toString());

                    }};

                    LoginActivity.set_login_prefs.putString("username",username.getText().toString());
                    LoginActivity.set_login_prefs.putString("password",password.getText().toString());
                    LoginActivity.set_login_prefs.apply();


                    ((LoginActivity) getContext()).runAccountHandler(
                            "login.php",
                            map_data
                    );
                }
                else
                    Toast.makeText(
                            getContext(),
                            "Your credentials must consist of at least 6 characters in length.",
                            Toast.LENGTH_SHORT).show();

            }
        });

        return signInView;
    }

}
