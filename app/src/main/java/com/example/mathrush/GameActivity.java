package com.example.mathrush;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    Button backButton, additionButton, subtractionButton, multiplicationButton, divisionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.backButton);
        additionButton = findViewById(R.id.additionButton);
        subtractionButton = findViewById(R.id.subtractionButton);
        multiplicationButton = findViewById(R.id.multiplicationButton);
        divisionButton = findViewById(R.id.divisionButton);


        // Puls på knapparna
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        backButton.startAnimation(pulse);
        additionButton.startAnimation(pulse);
        subtractionButton.startAnimation(pulse);
        multiplicationButton.startAnimation(pulse);
        divisionButton.startAnimation(pulse);

        // Vi går från GameActivity dvs spelsidan tillbaka till MainActivity dvs startsidan genom att trycka på vår "backButton"
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        });


        // När det trycks på "+"-knappen
        additionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen när det trycks på den för att visa att spelaren har valt den symbolen för att spela
            additionButton.setBackgroundColor(Color.parseColor("#82b6ff"));

        });


        // När det trycks på "-"-knappen
        subtractionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen när det trycks på den för att visa att spelaren har valt den symbolen för att spela
            subtractionButton.setBackgroundColor(Color.parseColor("#ff5994"));

        });

        // När det trycks på "*"-knappen
        multiplicationButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen när det trycks på den för att visa att spelaren har valt den symbolen för att spela
            multiplicationButton.setBackgroundColor(Color.parseColor("#ff9668"));

        });


        // När det trycks på "÷"-knappen
        divisionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen när det trycks på den för att visa att spelaren har valt den symbolen för att spela
            divisionButton.setBackgroundColor(Color.parseColor("#c88cff"));

        });






    }


}