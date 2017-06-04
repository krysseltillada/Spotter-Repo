package com.lmos.spotter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lmos.spotter.MainInterface.Activities.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Intent launchHome = new Intent(this, HomeActivity.class);
                startActivity(launchHome);
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
