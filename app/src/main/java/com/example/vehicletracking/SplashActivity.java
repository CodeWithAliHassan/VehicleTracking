package com.example.vehicletracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    ImageView ivRoadmap,ivVehicle;
    TextView tvText1,tvText2;
    Animation Top_anim,Left_anim,Bottom_anim;

    private static final int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        init();
        splashdelay();

    }

    private void splashdelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void  init(){
        ivRoadmap=findViewById(R.id.ivRoadMap);
        ivVehicle=findViewById(R.id.ivVehicle);
        tvText1=findViewById(R.id.tvText1);
        tvText2=findViewById(R.id.tvText2);

        Top_anim= AnimationUtils.loadAnimation(this,R.anim.topanim);
        Left_anim= AnimationUtils.loadAnimation(this,R.anim.leftanim);
        Bottom_anim= AnimationUtils.loadAnimation(this,R.anim.bottomanim);

        ivRoadmap.setAnimation(Top_anim);
        ivVehicle.setAnimation(Left_anim);
        tvText1.setAnimation(Bottom_anim);
        tvText2.setAnimation(Bottom_anim);
    }
}