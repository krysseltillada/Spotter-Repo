package com.lmos.spotter;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    ImageView background_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        background_img = (ImageView) findViewById(R.id.background_img_holder);

        background_img.setImageBitmap(
                BlurImg.blurImg(this, ((BitmapDrawable) this.getResources().getDrawable(R.drawable.traveler_bg)).getBitmap())
        );

        getSupportFragmentManager().beginTransaction()
                .add(R.id.account_fragment_holder,new FragmentSignIn(), "Sign In")
                .commit();

    }

    public void clickListener(View view){

        switch (view.getId()){

            case R.id.sign_up:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right)
                        .replace(R.id.account_fragment_holder, new FragmentSignUp(), "Sign Up")
                        .addToBackStack("Sign In")
                        .commit();

        }

    }

    /**
     *
     *  This method will start AccountHandler
     *  Arguments will be coming from Login and Registration Fragment
     *  and it will be passed to AccountHandler to process request.
     *
     **/
    public void runAccountHandler(String... params){

    }

    public class AccountHandler extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
