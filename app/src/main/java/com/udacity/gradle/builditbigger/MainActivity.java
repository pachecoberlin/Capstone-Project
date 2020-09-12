package com.udacity.gradle.builditbigger;

import de.pacheco.jokeshower.JokeShowerActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(final View view) {
        tellJokeForFriend(view);
    }

    @SuppressLint("StaticFieldLeak")
    public void tellJokeForFriend(final View view) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return JokesEndPointConnection.giveMeAJoke();
            }

            @Override
            protected void onPostExecute(String joke) {
                Toast.makeText(view.getContext(), joke, Toast.LENGTH_SHORT).show();
                jokeTester = joke;
                Intent intent = new Intent(view.getContext(), JokeShowerActivity.class);
                intent.putExtra(JokeShowerActivity.JOKE, joke);
                startActivity(intent);
            }
        }.execute();
    }
    public static String jokeTester;
}