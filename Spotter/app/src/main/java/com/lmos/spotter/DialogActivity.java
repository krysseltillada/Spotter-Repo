package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by emman on 6/17/2017.
 */

public class DialogActivity extends AppCompatActivity{

    TextView msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);
        Bundle bundle = getIntent().getExtras();
        msg = (TextView) findViewById(R.id.dialog_msg);
        msg.setText(bundle.getInt("message"));
        Log.d("DialogActivity", "So bad");
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
