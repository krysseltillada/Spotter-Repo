package com.lmos.spotter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                Intent launchToHome = new Intent(this, HomeActivity.class);
                launchToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchToHome);
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
            int a = 2;
            return null;
        }
    }

}
