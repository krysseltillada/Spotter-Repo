package com.lmos.spotter.AccountInterface.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.UserAccount;
import com.lmos.spotter.Utilities.Utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linker on 01/06/2017.
 * This class will hold the views
 * displaying user interface for registration.
 */

public class FragmentSignUp extends Fragment {

    private final int TAKE_PHOTO_REQUEST = 1800;
    ImageButton register_img;
    Bitmap userImage;
    TextInputEditText name, email, username, password;
    Button sign_up;

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

        register_img.setImageDrawable(getResources().getDrawable(R.drawable.account));
        userImage = ((BitmapDrawable)getResources().getDrawable(R.drawable.account)).getBitmap();

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

                final UserAccount registeredUserAccount = new UserAccount();

                registeredUserAccount.userName = username.getText().toString();
                registeredUserAccount.name = name.getText().toString();
                registeredUserAccount.email = email.getText().toString();
                registeredUserAccount.password = password.getText().toString();
                registeredUserAccount.profileImage = userImage;

                if(username.getText().toString().length() >= 6 ||
                        password.getText().toString().length() >= 6 ) {

                    if (!Utilities.validateEmail(registeredUserAccount.email)) {
                        Toast.makeText(getContext(), "invalid email", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d("debug", "username: " + registeredUserAccount.userName);
                    Log.d("debug", "password: " + registeredUserAccount.password);
                    Log.d("debug", "name: " + registeredUserAccount.name);
                    Log.d("debug", "email: " + registeredUserAccount.email);
                    Log.d("debug", "image: " + Utilities.BlurImg.bitmapToString(registeredUserAccount.profileImage));

                    final Map<String, String> map_data = new HashMap<String, String>() {{

                        put("username", registeredUserAccount.userName);
                        put("password", registeredUserAccount.password);
                        put("name", registeredUserAccount.name);
                        put("email", registeredUserAccount.email);
                        put("userImage", Utilities.BlurImg.bitmapToString(registeredUserAccount.profileImage));

                    }};

                    ((LoginActivity) getContext()).runAccountHandler(
                            "register.php",
                            map_data
                    );

                    ((LoginActivity) getContext()).setOnSignUpListener(new LoginActivity.OnSignUpListener() {

                        @Override
                        public void OnSignUp(String response) {
                            if (response.equals("Account has been registered.")) {
                                username.setText("");
                                name.setText("");
                                email.setText("");
                                password.setText("");
                                register_img.setImageDrawable(getResources().getDrawable(R.drawable.account));
                            }
                        }
                    });


                }
                else {
                    Toast.makeText(
                            getContext(),
                            "Your credentials must consist of at least 6 characters in length.",
                            Toast.LENGTH_SHORT).show();
                }

            }

        });


        return registerView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK){
            Bundle getPic = data.getExtras();
            userImage = (Bitmap) getPic.get("data");
            register_img.setImageBitmap(userImage);
        }

    }
}
