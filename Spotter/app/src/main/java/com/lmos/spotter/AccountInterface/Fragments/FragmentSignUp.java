package com.lmos.spotter.AccountInterface.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linker on 01/06/2017.
 * This class will hold the views
 * displaying user interface for registration.
 */

public class FragmentSignUp extends Fragment {

    ImageButton register_img;
    TextInputEditText name, email, username, password;
    Button sign_up;

    private final int TAKE_PHOTO_REQUEST = 1800;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View registerView = inflater.inflate(R.layout.sign_up_fragment, container, false);

        register_img = (ImageButton) registerView.findViewById(R.id.reg_set_img);
        name = (TextInputEditText) registerView.findViewById(R.id.reg_fname);
        email = (TextInputEditText) registerView.findViewById(R.id.reg_email);
        username = (TextInputEditText) registerView.findViewById(R.id.register_username);
        password = (TextInputEditText) registerView.findViewById(R.id.register_password);
        sign_up = (Button) registerView.findViewById(R.id.register_sign_up);

        register_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePic.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(takePic, TAKE_PHOTO_REQUEST);
                }

            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().length() > 6 ||
                        password.getText().toString().length() > 6) {

                    final Map<String, String> map_data = new HashMap<String, String>() {{

                        put("username", username.getText().toString());
                        put("password", password.getText().toString());
                        put("name", name.getText().toString());
                        put("email", email.getText().toString());

                    }};

                    LoginActivity.set_login_prefs.putString("username",username.getText().toString());
                    LoginActivity.set_login_prefs.putString("password",password.getText().toString());
                    LoginActivity.set_login_prefs.putString("name",name.getText().toString());
                    LoginActivity.set_login_prefs.putString("email",email.getText().toString());
                    LoginActivity.set_login_prefs.apply();

                    ((LoginActivity) getContext()).runAccountHandler(
                            "register.php",
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

        return registerView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK){
            Bundle getPic = data.getExtras();
            Bitmap bitmap = (Bitmap) getPic.get("data");
            register_img.setImageBitmap(bitmap);
        }

    }
}
