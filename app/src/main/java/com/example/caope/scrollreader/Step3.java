package com.example.caope.scrollreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Step3 extends AppCompatActivity {
    Intent intent = getIntent();
    TextView textView = (TextView)findViewById(R.id.textView1);
    String string = "Commands:\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step3);

        if(intent.getStringExtra("digital_scroll").equals("1"))
            string += "digital_scroll: "+ intent.getStringExtra("digital_scroll") + "\n";
        if(intent.getStringExtra("transcript").equals("1"))
            string += "transcript into: " + intent.getStringExtra("transcript") + intent.getStringExtra("sharp_flat") + "\n";
        if(intent.getStringExtra("predict").equals("1"))
            string += "predict: " + intent.getStringExtra("predict");
        textView.setText(string);

        presentResult();
    }

    private void presentResult(){
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
