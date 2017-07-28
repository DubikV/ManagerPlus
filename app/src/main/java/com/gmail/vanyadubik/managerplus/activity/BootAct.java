package com.gmail.vanyadubik.managerplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gmail.vanyadubik.managerplus.R;

public class BootAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final ImageView imageView = (ImageView) findViewById(R.id.splash);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotating);
        imageView.startAnimation(animation);
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(BootAct.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashTread.start();


    }
}