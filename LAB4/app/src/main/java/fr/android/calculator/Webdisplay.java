package fr.android.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Webdisplay extends AppCompatActivity {
    private Button button;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webdisplay);
        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value= editText.getText().toString();
                Intent i = new Intent(Webdisplay.this, WebviewOp.class);
                i.putExtra("key",value);
                startActivity(i);
            }
        });

    }
}