package com.example.ah.push;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ah.push.login.AuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by AH on 4/25/2018.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AuthenticationActivity authAct = new AuthenticationActivity();

    private FirebaseAuth mAuth;

    public static Boolean isVisible = false;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPurchasesListNewRecord:
                MainActivity.this.finish();
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
         if (i == R.id.sign_out_button) {

             MainActivity.this.finish();
             mAuth.signOut();
        }
    }


}
