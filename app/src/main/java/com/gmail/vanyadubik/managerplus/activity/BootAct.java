package com.gmail.vanyadubik.managerplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;

public class BootAct extends AppCompatActivity {

    private static int TIME_START_APP = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final ImageView imageView = (ImageView) findViewById(R.id.splash);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate_right);
        imageView.startAnimation(animation);
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;

                    ((ManagerPlusAplication) getApplication()).getComponent().inject(BootAct.this);

                    while (waited < TIME_START_APP) {
                        sleep(100);
                        waited += 100;
                    }

                    Intent intent = new Intent(BootAct.this, StartActivity.class);
                    startActivity(intent);
                    finish();

                    BootAct.this.finish();
                } catch (InterruptedException e) {
                } finally {
                    BootAct.this.finish();
                }
            }
        };
        splashTread.start();


    }
}