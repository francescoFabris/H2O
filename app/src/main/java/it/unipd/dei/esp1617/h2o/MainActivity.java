package it.unipd.dei.esp1617.h2o;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    public Button mButtonIn;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonIn = (Button) findViewById(R.id.buttonIn);
        mButtonIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_input);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);

    }

}
