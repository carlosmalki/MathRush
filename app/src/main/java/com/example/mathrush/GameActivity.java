package com.example.mathrush;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Button additionButton, subtractionButton, multiplicationButton, divisionButton, choiceOne, choiceTwo, choiceThree, choiceFour, newExpressionButton;
    TextView restartGameIcon, gameSiteTitle, mathExpressionBackground, answerFeedbackText;
    Random random;
    boolean harTryckt;
    int randomNumber1, randomNumber2;
    String sumText;

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

        harTryckt = false;
        random = new Random();

        gameSiteTitle = findViewById(R.id.gameSiteTitle);
        additionButton = findViewById(R.id.additionButton);
        subtractionButton = findViewById(R.id.subtractionButton);
        multiplicationButton = findViewById(R.id.multiplicationButton);
        divisionButton = findViewById(R.id.divisionButton);
        mathExpressionBackground = findViewById(R.id.mathExpressionBackground);
        restartGameIcon = findViewById(R.id.restartGameIcon);
        choiceOne = findViewById(R.id.choiceOne);
        choiceTwo = findViewById(R.id.choiceTwo);
        choiceThree = findViewById(R.id.choiceThree);
        choiceFour = findViewById(R.id.choiceFour);
        newExpressionButton = findViewById(R.id.newExpressionButton);
        answerFeedbackText = findViewById(R.id.answerFeedbackText);

        // Puls på knapparna vid start
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        additionButton.startAnimation(pulse);
        subtractionButton.startAnimation(pulse);
        multiplicationButton.startAnimation(pulse);
        divisionButton.startAnimation(pulse);


        // När det trycks på "+"-knappen
        additionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen när det trycks på den för att visa att spelaren har valt den symbolen för att spela
            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges));

            mathExpressionBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges));
            // Här gör vi spel komponenterna synliga då spelaren har valt en symbol för att spela
            mathExpressionBackground.setVisibility(View.VISIBLE);
            restartGameIcon.setVisibility(View.VISIBLE);
            choiceOne.setVisibility(View.VISIBLE);
            choiceTwo.setVisibility(View.VISIBLE);
            choiceThree.setVisibility(View.VISIBLE);
            choiceFour.setVisibility(View.VISIBLE);
            newExpressionButton.setVisibility(View.VISIBLE);
            gameSiteTitle.setVisibility(View.INVISIBLE);

            choiceOne.startAnimation(pulse);
            choiceTwo.startAnimation(pulse);
            choiceThree.startAnimation(pulse);
            choiceFour.startAnimation(pulse);

            // Detta är för att inte det ska genereras nya uttryck då man trycker på symbolknappen, det ska genereras endast vid första klick
            if(!harTryckt) {
                harTryckt = true;
                generateAdditionExpression();
            }

        });

        newExpressionButton.setOnClickListener(v -> {
            // Puls på knapparna
            choiceOne.startAnimation(pulse);
            choiceTwo.startAnimation(pulse);
            choiceThree.startAnimation(pulse);
            choiceFour.startAnimation(pulse);

            // generera nytt uttryck
            generateAdditionExpression();
            answerFeedbackText.setVisibility(View.INVISIBLE);
            answerFeedbackText.setText("");
        });

        // Vi startar om allt förutom spelarens "streak" när det trycks på restart iconen
        restartGameIcon.setOnClickListener(v -> {

            choiceOne.clearAnimation();
            choiceTwo.clearAnimation();
            choiceThree.clearAnimation();
            choiceFour.clearAnimation();

            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));

            // Puls på knapparna
            additionButton.startAnimation(pulse);
            subtractionButton.startAnimation(pulse);
            multiplicationButton.startAnimation(pulse);
            divisionButton.startAnimation(pulse);

            // Här gör vi spel komponenterna osynliga då spelaren har valt att starta om spelet
            mathExpressionBackground.setVisibility(View.INVISIBLE);
            restartGameIcon.setVisibility(View.INVISIBLE);
            choiceOne.setVisibility(View.INVISIBLE);
            choiceTwo.setVisibility(View.INVISIBLE);
            choiceThree.setVisibility(View.INVISIBLE);
            choiceFour.setVisibility(View.INVISIBLE);
            newExpressionButton.setVisibility(View.INVISIBLE);
            answerFeedbackText.setVisibility(View.INVISIBLE);
            answerFeedbackText.setText("");

            resetChoiceButtonColor();
            generateAdditionExpression();

            // Vi visar titeln som säger att man ska välja symbol för att spela
            gameSiteTitle.setVisibility(View.VISIBLE);

        });

        choiceOne.setOnClickListener(v -> {
            showCorrectAnswer(choiceOne);
        });

        choiceTwo.setOnClickListener(v -> {
            showCorrectAnswer(choiceTwo);
        });

        choiceThree.setOnClickListener(v -> {
            showCorrectAnswer(choiceThree);
        });


        choiceFour.setOnClickListener(v -> {
            showCorrectAnswer(choiceFour);
        });
    }

    // Denna metod är för att generera addition uttryck för spelet
    public void generateAdditionExpression() {

        // Vi genererar två slumpmässiga siffror mellan 1-100 inklusive 1 och 100 för spelandet
        randomNumber1 = random.nextInt(100) + 1;
        randomNumber2 = random.nextInt(100) + 1;

        // Svaret i (Integer)
        int sum = randomNumber1 + randomNumber2;

        // Vi omvandlar till strängar för att kunna lägga in de i våra TextView:s
        String randomNumber1Text = Integer.toString(randomNumber1);
        String randomNumber2Text = Integer.toString(randomNumber2);
        sumText = Integer.toString(sum);

        // vi sätter in texten
        mathExpressionBackground.setText(randomNumber1Text+" + "+randomNumber2Text);

        // Här gör vi så att det rätta svaret hamnar på en slumpmässig knapp och sedan sätter fel svar på de resterande knapparna
        Button[] answerButtonsArray = {choiceOne, choiceTwo, choiceThree, choiceFour};

        int randomChoiceButtonIndex = random.nextInt(answerButtonsArray.length);

        // Vi sätter svaret i den slumpmässiga knappen
        answerButtonsArray[randomChoiceButtonIndex].setText(sumText);

        // Vi fyller de resterande knapparna med fel svars alternativ
        for (int i = 0; i < answerButtonsArray.length; i++) {
            if(i == randomChoiceButtonIndex) continue;

            int wrongAnswer;
            do {
                wrongAnswer = sum + random.nextInt(31) -10;
            } while(wrongAnswer == sum || wrongAnswer < 1);

            // Vi omvandlar de fel svaren till strings och sätter de i knapparna
            String wrongAnswerText = Integer.toString(wrongAnswer);
            answerButtonsArray[i].setText(wrongAnswerText);

            resetChoiceButtonColor();
        }

    }

    public void showCorrectAnswer(Button choicebutton) {
        int integerSum = Integer.parseInt(sumText);
        String sumTextOnButton = (String) choicebutton.getText();
        int integerSumOnButton = Integer.parseInt(sumTextOnButton);

        if (integerSum == integerSumOnButton) {
            choicebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.correct_answer_color));
            answerFeedbackText.setTextColor(Color.parseColor("#0E8A07"));
            answerFeedbackText.setText("Correct!");
            answerFeedbackText.setVisibility(View.VISIBLE);
            removeChoiceButtonAnimation();


        }
        else {
            choicebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.wrong_answer_color));
            answerFeedbackText.setTextColor(Color.parseColor("#9E1010"));
            answerFeedbackText.setText("Wrong!");
            answerFeedbackText.setVisibility(View.VISIBLE);
            removeChoiceButtonAnimation();
        }

    }

    public void resetChoiceButtonColor() {
        choiceOne.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceThree.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceFour.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
    }

    public void removeChoiceButtonAnimation() {
        choiceOne.clearAnimation();
        choiceTwo.clearAnimation();
        choiceThree.clearAnimation();
        choiceFour.clearAnimation();
    }

}