package com.lmos.spotter.AccountInterface.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.lmos.spotter.AccountInterface.Fragments.FragmentRecover;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignIn;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignUp;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

public class LoginActivity extends AppCompatActivity {

    ImageView imgHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imgHolder = (ImageView) findViewById(R.id.background_img_holder);
        imgHolder.setImageBitmap(
                Utilities.BlurImg.blurImg(
                        this,
                        ((BitmapDrawable) getResources().getDrawable(R.drawable.traveler_bg)).getBitmap(),
                        2f
                )
        );

        getSupportFragmentManager().beginTransaction()
                .add(R.id.account_fragment_holder,new FragmentSignIn(), "Sign In")
                .commit();

    }

    public void clickListener(View view){

        switch (view.getId()){

            case R.id.sign_up:
                switchFragment(new FragmentSignUp());
                return;
            case R.id.sign_in:
                startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                return;
            case R.id.forgot_pass:
                switchFragment(new FragmentRecover());
                return;
            default:
                break;

        }

    }

    private void switchFragment(Fragment fragment){

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.account_fragment_holder, fragment, "Sign Up")
                .addToBackStack("Sign In")
                .commit();

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
