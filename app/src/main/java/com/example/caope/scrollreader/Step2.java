package com.example.caope.scrollreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

public class Step2 extends AppCompatActivity {
    private Intent command = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step2);

        CheckBox digital_scroll = (CheckBox)findViewById(R.id.checkBox);
        CheckBox transcript = (CheckBox)findViewById(R.id.checkBox2);
        CheckBox predict = (CheckBox)findViewById(R.id.checkBox3);

        /* default intent data */
        command.putExtra("digital_scroll", "1");
        command.putExtra("transcript", "0");
        command.putExtra("sharp_flat", "0");
        command.putExtra("predict", "0");

        digital_scroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    command.putExtra("digital_scroll", "1");
                }else{
                    command.putExtra("digital_scroll", "0");
                }
            }
        });

        transcript.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Spinner major_selector = (Spinner)findViewById(R.id.spinner);
                    Spinner sharp_flat = (Spinner)findViewById(R.id.spinner2);
                    final ArrayAdapter<CharSequence> major_adapter = ArrayAdapter.createFromResource(Step2.this, R.array.translate_array, android.R.layout.simple_spinner_item);
                    final ArrayAdapter<CharSequence> sharp_flat_adapter = ArrayAdapter.createFromResource(Step2.this, R.array.sharp_flat, android.R.layout.simple_spinner_item);

                    major_selector.setAdapter(major_adapter);
                    sharp_flat.setAdapter(sharp_flat_adapter);
                    major_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            command.putExtra("transcript", major_adapter.getItem(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            command.putExtra("transcript", "0");
                        }
                    });
                    sharp_flat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            command.putExtra("sharp_flat", sharp_flat_adapter.getItem(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            command.putExtra("sharp_flat", "0");
                        }
                    });
                }
            }
        });

        predict.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) command.putExtra("predict", "1");
                else command.putExtra("predict", "0");
            }
        });
    }

    public void next(View view){
        if(command.getStringExtra("digital_scroll").equals("0")&&
                command.getStringExtra("transcript").equals("0")&&
                command.getStringExtra("predict").equals("0")){
            Toast.makeText(Step2.this, R.string.toast2, Toast.LENGTH_LONG).show(); return;
        }

        sendToServer();

        command.setClass(this, Step3.class);
        this.startActivityForResult(command, 2);
    }

    public void sendToServer(){

    }

    public void back(View view){
        this.finish();
    }

}
