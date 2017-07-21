package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by emman on 6/17/2017.
 */

public class DialogActivity extends AppCompatActivity{

    TextView msg;
    Button positive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);
        Bundle bundle = getIntent().getExtras();
        msg = (TextView) findViewById(R.id.dialog_msg);
        msg.setText(bundle.getInt("message"));
        positive = (Button) findViewById(R.id.positive_button);

        if(bundle.getInt("message") == R.string.not_sign)
            positive.setText("Ok");

        setFinishOnTouchOutside(false);
        Log.d("DialogActivity", "So bad");
    }

    @Override
    public void onBackPressed() {
        // Do nothing.
    }

    public void onClick(View view){

        switch (view.getId()){

            case R.id.positive_button:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.negative_button:
                setResult(RESULT_CANCELED);
                finish();
                break;

        }

    }

}
