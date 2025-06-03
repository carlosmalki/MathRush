package com.example.mathrush;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    // Kanal_id för att kunna skapa notifikationer
    private static final String CHANNEL_ID = "streak_channel";

    // Namnet på filen där vi sparar data lokalt i appen
    private static final String PREFS_NAME = "MathRushPrefs";

    // Nyckeln vi använder för att spara streak-rekordet
    private static final String STREAK_KEY = "StreakRecord";

    private Button additionButton, subtractionButton, multiplicationButton, divisionButton, choiceOne, choiceTwo, choiceThree, choiceFour, newExpressionButton, restartGameButton;
    private TextView gameSiteTitle, mathExpressionBackground, answerFeedbackText, pointsNumber, streakRecordNumber;
    private Random random;

    private int randomNumber1, randomNumber2;
    private String ExpressionResultsText;
    private Button[] answerButtonsArray;
    private int gamePoints;

    private MediaPlayer correctAnswerSound;
    private MediaPlayer wrongAnswerSound;

    // Denna variabel finns för att kunna generera rätt uttryck för spelaren
    private String selectedOperator;

    // Denna boolean finns för att den symbol-knappen vi trycker på ska inte bara generera uttryck vid varje klick
    private boolean hasGeneratedFirstExpression = false;




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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        random = new Random();
        gameSiteTitle = findViewById(R.id.gameSiteTitle);
        additionButton = findViewById(R.id.additionButton);
        subtractionButton = findViewById(R.id.subtractionButton);
        multiplicationButton = findViewById(R.id.multiplicationButton);
        divisionButton = findViewById(R.id.divisionButton);
        mathExpressionBackground = findViewById(R.id.mathExpressionBackground);
        restartGameButton = findViewById(R.id.restartGameButton);
        choiceOne = findViewById(R.id.choiceOne);
        choiceTwo = findViewById(R.id.choiceTwo);
        choiceThree = findViewById(R.id.choiceThree);
        choiceFour = findViewById(R.id.choiceFour);
        streakRecordNumber = findViewById(R.id.streakRecordNumber);
        pointsNumber = findViewById(R.id.pointsNumber);
        newExpressionButton = findViewById(R.id.newExpressionButton);
        answerFeedbackText = findViewById(R.id.answerFeedbackText);

        // Vi hämtar referens till SharedPreferences med vårt filnamn
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Här läser vi in det sparade streak-rekordet, eller 0 om det inte finns något sparat än
        int savedStreak = prefs.getInt(STREAK_KEY, 0);

        // Vi sätter värdet på streak-rekordet i TextView:n
        streakRecordNumber.setText(String.valueOf(savedStreak));

        correctAnswerSound = MediaPlayer.create(this, R.raw.correct_answer_sound);
        wrongAnswerSound = MediaPlayer.create(this, R.raw.wrong_answer_sound);

        answerButtonsArray = new Button[] {choiceOne, choiceTwo, choiceThree, choiceFour};
        symbolButtonsAnimation();

        // När det trycks på "plus"-knappen
        additionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen och tillhörande komponenter för att visa att spelaren har valt den symbolen för att spela
            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges));
            mathExpressionBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges));
            restartGameButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges));
            gameSiteTitle.setText("Addition");
            gameSiteTitle.setTextColor(Color.parseColor("#82b6ff"));

            // Här gör vi spel komponenterna synliga då spelaren har valt en symbol för att spela
            mathExpressionBackground.setVisibility(View.VISIBLE);
            restartGameButton.setVisibility(View.VISIBLE);
            setChoiceButtonsVisibility(View.VISIBLE);

            startChoiceButtonsAnimation();


            // Endast första gången vi trycker på knappen, generera uttrycket
            if (!hasGeneratedFirstExpression) {
                generateAdditionExpression();
                hasGeneratedFirstExpression = true;
            }

            subtractionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            multiplicationButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            divisionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            subtractionButton.setVisibility(View.INVISIBLE);
            multiplicationButton.setVisibility(View.INVISIBLE);
            divisionButton.setVisibility(View.INVISIBLE);


            // symbolen som spelaren har valt att spela med
            selectedOperator = "+";

        });

        // När det trycks på "minus"-knappen
        subtractionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen och tillhörande komponenter för att visa att spelaren har valt den symbolen för att spela
            subtractionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_pink));
            mathExpressionBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_pink));
            restartGameButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_pink));
            gameSiteTitle.setText("Subtraction");
            gameSiteTitle.setTextColor(Color.parseColor("#ff5994"));


            // Här gör vi spel komponenterna synliga då spelaren har valt en symbol för att spela
            mathExpressionBackground.setVisibility(View.VISIBLE);
            restartGameButton.setVisibility(View.VISIBLE);
            setChoiceButtonsVisibility(View.VISIBLE);

            startChoiceButtonsAnimation();

            // Endast första gången vi trycker på knappen, generera uttrycket
            if (!hasGeneratedFirstExpression) {
                generateSubtractionExpression();
                hasGeneratedFirstExpression = true;
            }

            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            multiplicationButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            divisionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            additionButton.setVisibility(View.INVISIBLE);
            multiplicationButton.setVisibility(View.INVISIBLE);
            divisionButton.setVisibility(View.INVISIBLE);

            // symbolen som spelaren har valt att spela med
            selectedOperator = "-";


        });

        // När det trycks på "gånger"-knappen
        multiplicationButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen och tillhörande komponenter för att visa att spelaren har valt den symbolen för att spela
            multiplicationButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_orange));
            mathExpressionBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_orange));
            restartGameButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_orange));
            gameSiteTitle.setText("Multiplication");
            gameSiteTitle.setTextColor(Color.parseColor("#ff9668"));

            // Här gör vi spel komponenterna synliga då spelaren har valt en symbol för att spela
            mathExpressionBackground.setVisibility(View.VISIBLE);
            restartGameButton.setVisibility(View.VISIBLE);
            setChoiceButtonsVisibility(View.VISIBLE);

            startChoiceButtonsAnimation();

            // Endast första gången vi trycker på knappen, generera uttrycket
            if (!hasGeneratedFirstExpression) {
                generateMultiplicationExpression();
                hasGeneratedFirstExpression = true;
            }

            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            subtractionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            divisionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            additionButton.setVisibility(View.INVISIBLE);
            subtractionButton.setVisibility(View.INVISIBLE);
            divisionButton.setVisibility(View.INVISIBLE);

            // symbolen som spelaren har valt att spela med
            selectedOperator = "*";


        });

        // När det trycks på "division"-knappen
        divisionButton.setOnClickListener(v -> {
            additionButton.clearAnimation();
            subtractionButton.clearAnimation();
            multiplicationButton.clearAnimation();
            divisionButton.clearAnimation();

            // Vi byter färg på knappen och tillhörande komponenter för att visa att spelaren har valt den symbolen för att spela
            divisionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_purple));
            mathExpressionBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_purple));
            restartGameButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_edges_purple));
            gameSiteTitle.setText("Division");
            gameSiteTitle.setTextColor(Color.parseColor("#c88cff"));

            // Här gör vi spel komponenterna synliga då spelaren har valt en symbol för att spela
            mathExpressionBackground.setVisibility(View.VISIBLE);
            restartGameButton.setVisibility(View.VISIBLE);
            setChoiceButtonsVisibility(View.VISIBLE);


            startChoiceButtonsAnimation();

            // Endast första gången vi trycker på knappen, generera uttrycket
            if (!hasGeneratedFirstExpression) {
                generateDivisionExpression();
                hasGeneratedFirstExpression = true;
            }

            additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            subtractionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            multiplicationButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            subtractionButton.setVisibility(View.INVISIBLE);
            multiplicationButton.setVisibility(View.INVISIBLE);
            additionButton.setVisibility(View.INVISIBLE);

            // symbolen som spelaren har valt att spela med
            selectedOperator = "/";


        });

        // När vi trycker på "generate new expression" knappen
        newExpressionButton.setOnClickListener(v -> {
            startChoiceButtonsAnimation();
            newExpressionButton.setVisibility(View.INVISIBLE);
            answerFeedbackText.setVisibility(View.INVISIBLE);
            answerFeedbackText.setText("");

            // Vi genererar rätt typ av uttryck baserat på symbolen som spelaren har valt att spela med
            if (selectedOperator == "+") {
                generateAdditionExpression();
            } else if (selectedOperator == "-") {
                generateSubtractionExpression();
            } else if (selectedOperator == "*") {
                generateMultiplicationExpression();
            } else if (selectedOperator == "/") {
                generateDivisionExpression();
            }

            newExpressionButton.clearAnimation();
            makingChoiceButtonsClickable();
        });

        // Vi startar om allt förutom spelarens "streak" när det trycks på restart knappen
        restartGameButton.setOnClickListener(v -> {
            if (gamePoints == 0) {
                resetGame();
            }
            else {
                showConfirmationDialog();
            }

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
        ExpressionResultsText = Integer.toString(sum);

        // vi sätter in texten
        mathExpressionBackground.setText(randomNumber1Text+" + "+randomNumber2Text);

        int randomChoiceButtonIndex = random.nextInt(answerButtonsArray.length);

        // Vi sätter svaret i den slumpmässiga knappen
        answerButtonsArray[randomChoiceButtonIndex].setText(ExpressionResultsText);

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

    // Denna metod är för att generera subtraktion uttryck för spelet
    public void generateSubtractionExpression() {

        // Vi genererar två slumpmässiga siffror mellan 1-100 inklusive 1 och 100 för spelandet
        randomNumber1 = random.nextInt(100) + 1;
        randomNumber2 = random.nextInt(100) + 1;

        // Svaret i (Integer)
        int resultsNumber = randomNumber1 - randomNumber2;

        // Vi omvandlar till strängar för att kunna lägga in de i våra TextView:s
        String randomNumber1Text = Integer.toString(randomNumber1);
        String randomNumber2Text = Integer.toString(randomNumber2);
        ExpressionResultsText = Integer.toString(resultsNumber);

        // vi sätter in texten
        mathExpressionBackground.setText(randomNumber1Text+" - "+randomNumber2Text);

        int randomChoiceButtonIndex = random.nextInt(answerButtonsArray.length);

        // Vi sätter svaret i den slumpmässiga knappen
        answerButtonsArray[randomChoiceButtonIndex].setText(ExpressionResultsText);

        // Vi fyller de resterande knapparna med fel svars alternativ
        for (int i = 0; i < answerButtonsArray.length; i++) {
            if(i == randomChoiceButtonIndex) continue;

            int wrongAnswer;
            do {
                wrongAnswer =  + random.nextInt(31) -10;
            } while(wrongAnswer == resultsNumber || wrongAnswer < 1);

            // Vi omvandlar de fel svaren till strings och sätter de i knapparna
            String wrongAnswerText = Integer.toString(wrongAnswer);
            answerButtonsArray[i].setText(wrongAnswerText);

            resetChoiceButtonColor();
        }

    }

    // Denna metod är för att generera multiplikation uttryck för spelet
    public void generateMultiplicationExpression() {

        // Vi genererar två slumpmässiga siffror mellan 1-10 inklusive 1 och 10 för spelandet
        randomNumber1 = random.nextInt(10) + 1;
        randomNumber2 = random.nextInt(10) + 1;

        // Svaret i (Integer)
        int resultsNumber = randomNumber1 * randomNumber2;

        // Vi omvandlar till strängar för att kunna lägga in de i våra TextView:s
        String randomNumber1Text = Integer.toString(randomNumber1);
        String randomNumber2Text = Integer.toString(randomNumber2);
        ExpressionResultsText = Integer.toString(resultsNumber);

        // vi sätter in texten
        mathExpressionBackground.setText(randomNumber1Text+" x "+randomNumber2Text);

        int randomChoiceButtonIndex = random.nextInt(answerButtonsArray.length);

        // Vi sätter svaret i den slumpmässiga knappen
        answerButtonsArray[randomChoiceButtonIndex].setText(ExpressionResultsText);

        // Vi fyller de resterande knapparna med fel svars alternativ
        for (int i = 0; i < answerButtonsArray.length; i++) {
            if(i == randomChoiceButtonIndex) continue;

            int wrongAnswer;
            do {
                wrongAnswer =  + random.nextInt(21) - 3;
            } while(wrongAnswer == resultsNumber || wrongAnswer < 1);

            // Vi omvandlar de fel svaren till strings och sätter de i knapparna
            String wrongAnswerText = Integer.toString(wrongAnswer);
            answerButtonsArray[i].setText(wrongAnswerText);

            resetChoiceButtonColor();
        }

    }

    public void generateDivisionExpression() {
        // Vi vill säkerställa att divisionen går jämnt ut
        randomNumber2 = random.nextInt(9) + 1; // 1 till 9
        int quotient = random.nextInt(10) + 1; // svaret, 1 till 10
        randomNumber1 = randomNumber2 * quotient; // säkerställer att divisionen blir ett heltal

        // Omvandlar till strängar
        String randomNumber1Text = Integer.toString(randomNumber1);
        String randomNumber2Text = Integer.toString(randomNumber2);
        ExpressionResultsText = Integer.toString(quotient);

        // Vi sätter uttrycket i textvyn
        mathExpressionBackground.setText(randomNumber1Text + " / " + randomNumber2Text);

        // Väljer en slumpmässig knapp för rätt svar
        int randomChoiceButtonIndex = random.nextInt(answerButtonsArray.length);
        answerButtonsArray[randomChoiceButtonIndex].setText(ExpressionResultsText);

        // Fyller övriga knappar med felaktiga, men rimliga svar
        for (int i = 0; i < answerButtonsArray.length; i++) {
            if (i == randomChoiceButtonIndex) continue;

            int wrongAnswer;
            do {
                wrongAnswer = quotient + random.nextInt(11) - 5; // plus minus 5 från det rätta svaret
            } while (wrongAnswer == quotient || wrongAnswer < 1);

            answerButtonsArray[i].setText(Integer.toString(wrongAnswer));
        }

        resetChoiceButtonColor();
    }


    // Metod som hanterar rätt samt fel svar
    public void showCorrectAnswer(Button choicebutton) {

        int integerSum = Integer.parseInt(ExpressionResultsText);
        String sumTextOnButton = (String) choicebutton.getText();
        int integerSumOnButton = Integer.parseInt(sumTextOnButton);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);


        if (integerSum == integerSumOnButton) { // Om det är rätt svar
            correctAnswerSound.seekTo(0);
            correctAnswerSound.start();
            makingChoiceButtonsUnclickable();
            choicebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.correct_answer_color));
            answerFeedbackText.setTextColor(Color.parseColor("#0E8A07"));
            answerFeedbackText.setText("Correct!");
            answerFeedbackText.setVisibility(View.VISIBLE);
            removeChoiceButtonAnimation();
            givePoint();
            newExpressionButton.setVisibility(View.VISIBLE);
            newExpressionButton.startAnimation(pulse);

        }

        else { // Om det är fel svar
            wrongAnswerSound.seekTo(0);
            wrongAnswerSound.start();
            makingChoiceButtonsUnclickable();
            choicebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.wrong_answer_color));
            answerFeedbackText.setTextColor(Color.parseColor("#9E1010"));
            answerFeedbackText.setText("Wrong! Correct answer is: "+integerSum);
            answerFeedbackText.setVisibility(View.VISIBLE);
            removeChoiceButtonAnimation();
            resetGamePoints();
            newExpressionButton.setVisibility(View.VISIBLE);
            newExpressionButton.startAnimation(pulse);

            // Vi färgar knappen med rätt svar grönt för att visa spelaren det rätta svaret.
            for (Button btn : answerButtonsArray) {
                if (btn.getText().toString().equals(ExpressionResultsText)) {
                    btn.setBackground(ContextCompat.getDrawable(this, R.drawable.correct_answer_color));
                    btn.setAnimation(pulse);
                    break;
                }
            }

        }

    }

    // Denna metod anropas när vi vill förändra designen på våra val knappar till default design
    public void resetChoiceButtonColor() {
        choiceOne.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceThree.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        choiceFour.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
    }

    // Denna metod anropas när vi vill förändra designen på våra symbol knappar till default design
    public void resetSymbolButtonColor() {
        additionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        subtractionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        multiplicationButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        divisionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
    }

    // Denna metod tar bort animationen (puls) från våra val knappar
    public void removeChoiceButtonAnimation() {
        choiceOne.clearAnimation();
        choiceTwo.clearAnimation();
        choiceThree.clearAnimation();
        choiceFour.clearAnimation();
    }

    // Denna metod anropas när vi vill att alla våra val knappar ska vara oklickbara
    public void makingChoiceButtonsUnclickable() {
        choiceOne.setEnabled(false);
        choiceTwo.setEnabled(false);
        choiceThree.setEnabled(false);
        choiceFour.setEnabled(false);
    }

    // Denna metod anropas när vi vill att alla våra val knappar ska bli klickbara
    public void makingChoiceButtonsClickable() {
        choiceOne.setEnabled(true);
        choiceTwo.setEnabled(true);
        choiceThree.setEnabled(true);
        choiceFour.setEnabled(true);
    }

    // Denna metod sätter animation på våra symbol knappar (puls) animation
    public void symbolButtonsAnimation() {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        additionButton.startAnimation(pulse);
        subtractionButton.startAnimation(pulse);
        multiplicationButton.startAnimation(pulse);
        divisionButton.startAnimation(pulse);
    }

    // Denna metod sätter animation på våra valknappar (puls) animation
    public void startChoiceButtonsAnimation() {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        choiceOne.startAnimation(pulse);
        choiceTwo.startAnimation(pulse);
        choiceThree.startAnimation(pulse);
        choiceFour.startAnimation(pulse);
    }


    // Denna metod ger poäng för varje rätt svar
    public void givePoint() {
        String pointsNumberText = (String) pointsNumber.getText();
        gamePoints = Integer.parseInt(pointsNumberText);
        gamePoints++;
        String UppdatedPointsNumberText = Integer.toString(gamePoints);
        pointsNumber.setText(UppdatedPointsNumberText);

        // Starta poäng-siffra-animation (hopp)
        Animation jump = AnimationUtils.loadAnimation(this, R.anim.point_jump);
        pointsNumber.startAnimation(jump);
    }

    // Denna metod nollställer de samlade poängen under spelandet
    public void resetGamePoints() {
        updateStreakIfNeeded(); // Kolla om spelaren har fått ett nytt rekord och uppdatera om det behövs
        gamePoints = 0;
        pointsNumber.setText("0");
    }


    // Metoden som uppdaterar spelarens rekord, den uppdaterar spelarens rekord siffra varje gång spelaren får ett högre rekord
    public void updateStreakIfNeeded() {
        String streakRecordNumberText = (String) streakRecordNumber.getText();

        int integerStreakRecordNumber = Integer.parseInt(streakRecordNumberText);
        int integerNewStreak;
        // Om spelaren får ett nytt rekord
        if(gamePoints > integerStreakRecordNumber) {

            // Vi sätter det nya rekordvärdet
            integerNewStreak = gamePoints;

            /*
             Vi sparar det nya streak-rekordet i (SharedPreferences).
             Detta kommer även hjälpa oss för att uppdatera och
             visa rekordet på huvudsidan vid texten "LAST BEST STREAK:"
             */
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Vi sätter det nya streak-värdet i editorn
            editor.putInt(STREAK_KEY, integerNewStreak);

            // Sparar ändringarna permanent
            editor.apply();

            // Vi omvandlar nya rekord siffran till string siffra
            String newStreakText = Integer.toString(integerNewStreak);
            // Vi skickar string siffran och visar den på vår GUI
            streakRecordNumber.setText(newStreakText);
            // Vi visar notifikation om att spelaren har nått ett nytt rekord
            showNewStreakNotification(integerNewStreak);
            // Vi skapar och startar animation för varje nytt rekord, vi låter siffran hoppa upp
            Animation jump = AnimationUtils.loadAnimation(this, R.anim.point_jump);
            streakRecordNumber.startAnimation(jump);


        }
    }

    // Metoden som visar en egen designad dialog när spelaren har samlade spelpoäng och väljer att reseta spelet
    public void showConfirmationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_confirmation_dialog, null);

        // Knapparna i layouten
        Button yesButton = dialogView.findViewById(R.id.yesButton);
        Button noButton = dialogView.findViewById(R.id.noButton);

        // Dialogen byggs
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Klick på "Yes" knappen
        yesButton.setOnClickListener(v -> {

            // Vi låter poäng siffran hoppa upp för att visa att poängen är 0 igen och sedan resetar spelet
            Animation jump = AnimationUtils.loadAnimation(this, R.anim.point_jump);
            pointsNumber.startAnimation(jump);
            resetGame();
            dialog.dismiss(); // Dialogen stängs
        });

        // Klick på "No" knappen
        noButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }


    // Denna metod anropas när spelaren trycker på "restart" knappen för att starta om spelet
    public void resetGame() {

        // Nollställer så att man kan generera igen ett uttryck
        hasGeneratedFirstExpression = false;

        // Vi tar bort pulsen som finns på val knapparna
        removeChoiceButtonAnimation();

        resetSymbolButtonColor();
        gameSiteTitle.setText("CHOOSE A SYMBOL TO PLAY");
        gameSiteTitle.setTextColor(Color.parseColor("#FF000000"));
        // Vi visar titeln som säger att man ska välja symbol för att spela
        gameSiteTitle.setVisibility(View.VISIBLE);

        updateStreakIfNeeded();

        // Puls på symbol knapparna
        symbolButtonsAnimation();

        // Här gör vi spel komponenterna osynliga då spelaren har valt att starta om spelet
        mathExpressionBackground.setVisibility(View.INVISIBLE);
        restartGameButton.setVisibility(View.INVISIBLE);
        setChoiceButtonsVisibility(View.INVISIBLE);
        newExpressionButton.setVisibility(View.INVISIBLE);
        answerFeedbackText.setVisibility(View.INVISIBLE);
        answerFeedbackText.setText("");
        newExpressionButton.clearAnimation();

        setSymbolButtonVisibility(View.VISIBLE);
        resetChoiceButtonColor();
        makingChoiceButtonsClickable();

        // Vi nollställer de samlade poängen under spelandet
        resetGamePoints();
    }

    // Metoden som visar en notifikation varje gång spelaren når ett nytt rekord
    public void showNewStreakNotification(int newStreak) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // VI skapar kanalen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Rekord Notifikation";
            String description = "Notis visas vid nytt rekord (streak)";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Här Byggs notifikationen
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Streak!")
                .setContentText("Congratulations! You've reached a new streak: "+ newStreak)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.parseColor("#84ff9f"))
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }


    // Metoden som sätter synligheten av våra val knappar
    public void setChoiceButtonsVisibility(int visibility) {
        choiceOne.setVisibility(visibility);
        choiceTwo.setVisibility(visibility);
        choiceThree.setVisibility(visibility);
        choiceFour.setVisibility(visibility);
    }

    // Metoden som sätter synligheten på våra symbol knappar
    public void setSymbolButtonVisibility(int visibility) {
        additionButton.setVisibility(visibility);
        subtractionButton.setVisibility(visibility);
        multiplicationButton.setVisibility(visibility);
        divisionButton.setVisibility(visibility);
    }

}