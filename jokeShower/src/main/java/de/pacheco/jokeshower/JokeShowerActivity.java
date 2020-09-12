package de.pacheco.jokeshower;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JokeShowerActivity extends AppCompatActivity {

    public static final String JOKE = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_shower);
        Intent intent = getIntent();
        if (intent != null){
            String joke = intent.getStringExtra(JOKE);
            if (joke!=null){
                TextView jokeText = findViewById(R.id.joketext);
                jokeText.setText(joke);
            }
        }
    }
}