package com.example.peter.onlinequiz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.peter.onlinequiz.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Playing extends AppCompatActivity implements View.OnClickListener{

    final static long INTERVAL=1000;
    final static long TIMEOUT=7000;
    int progressValue=0;


    CountDownTimer mcountDown;
    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA,btnB,btnC,btnD;
    TextView txtScore,txtQuestionNum,question_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);


        //views
        txtScore =(TextView)findViewById(R.id.txtScore);
        txtQuestionNum=(TextView)findViewById(R.id.txtTotalQuestion);
        question_text=(TextView)findViewById(R.id.question_text);
       question_image=(ImageView)findViewById(R.id.question_image);

        progressBar =(ProgressBar)findViewById(R.id.progressBar);

        btnA=(Button)findViewById(R.id.btnAnswerA);
        btnB=(Button)findViewById(R.id.btnAnswerB);
        btnC=(Button)findViewById(R.id.btnAnswerC);
        btnD=(Button)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        mcountDown.cancel();
        final MediaPlayer castSoundMP= MediaPlayer.create(this,R.raw.correct);
        final MediaPlayer castSoundMP1= MediaPlayer.create(this,R.raw.wrong);

        if (index<totalQuestion)//still have questions in list
        {
            Button clickedButton=(Button)view;
            if(clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {

                score+=10;
                correctAnswer++;
                showQuestion(++index);
                castSoundMP.start();

            }
            else
            {
                //wrong answer
                castSoundMP1.start();
                Intent intent=new Intent(this,Done.class);
                Bundle dataSend=new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();

            }
            txtScore.setText(String.format("%d",score));

        }
    }

    private void showQuestion(int index) {
        if (index<totalQuestion)
        {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d/ %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue=0;

            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                Picasso.with(getBaseContext())
                            .load(Common.questionList.get(index).getQuestion())
                            .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }
            else
            {
                //if is image
                question_text.setText(Common.questionList.get(index).getQuestion());
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);


            }
            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            mcountDown.start();

        }
        else
        {
            //if it is final question
            Intent intent=new Intent(this,Done.class);
            Bundle dataSend=new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion=Common.questionList.size();
        mcountDown = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long minisec) {
            progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mcountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
