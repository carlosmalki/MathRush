package com.example.mathrush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private TextView streakRecordNumberMainPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vi skapar puls på vår "PLAY"-knapp
        Button playButton = findViewById(R.id.playButton);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        playButton.startAnimation(pulse);

        // Vi går från MainActivity dvs startsidan till GameActivity dvs spelsidan genom att trycka på vår "playButton"
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        // Vi hämtar GUI TextViewn:n för att kunna sätta funktionalitet på den
        streakRecordNumberMainPage = findViewById(R.id.streakRecordNumberMainPage);

        // Vi hämtar det sparade rekordet från SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MathRushPrefs", MODE_PRIVATE);
        int savedStreak = prefs.getInt("StreakRecord", 0);
        String stringSavedStreak = Integer.toString(savedStreak);

        // Vi visar rekordet i TextView:n sist
        streakRecordNumberMainPage.setText(stringSavedStreak);


    }
}