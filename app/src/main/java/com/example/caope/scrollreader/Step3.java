package com.example.caope.scrollreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Step3 extends AppCompatActivity {

    private String string = "Commands:\n";
    private Intent intent;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step3);

        intent = getIntent();
        textView = (TextView)findViewById(R.id.textView1);

        presentResult();
    }

    private void presentResult(){
        /* test presentResult */
        if(intent.getStringExtra("digital_scroll").equals("1"))
            string += "digital_scroll: "+ intent.getStringExtra("digital_scroll") + "\n";
        if(!intent.getStringExtra("transcript").equals("0"))
            string += "transcript into: " + intent.getStringExtra("transcript") + intent.getStringExtra("sharp_flat") + "\n";
        if(intent.getStringExtra("predict").equals("1"))
            string += "predict: " + intent.getStringExtra("predict");
        textView.setText(string);
    }

    public void next(View view){
        Intent intent = new Intent();
        intent.setClass(this, Step1.class);
        this.startActivityForResult(intent, 0);
    }

    public void back(View view){
        this.finish();
    }

}
