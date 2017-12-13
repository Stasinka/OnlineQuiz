package com.example.peter.onlinequiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.onlinequiz.Common.Common;
import com.example.peter.onlinequiz.Model.Category;
import com.example.peter.onlinequiz.Model.User;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    EditText edtNewUser,edtNewPassword,edtNewEmail; //fo sign ip
    EditText edtUser,edtPassword; //for sign in

    Button btnSignUp,btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        database =FirebaseDatabase.getInstance();
        users =database.getReference("users");

        edtUser=(EditText)findViewById(R.id.edtUser);
        edtPassword=(EditText)findViewById(R.id.edtPassword);

        btnSignIn=(Button)findViewById(R.id.btn_sign_in);
        btnSignUp=(Button)findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                showSignUpDialog();
            }

        });
        btnSignIn.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view)
             {
                 signIn(edtUser.getText().toString(),edtPassword.getText().toString());
             }

        });

    }
    private void signIn(final String user, final String pwd){

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists())
                {
                    if(!user.isEmpty())
                    {
                        User login =dataSnapshot.child(user).getValue(User.class);

                        if (login.getPassword().equals(pwd)){

                            Intent homeActivity =new Intent(MainActivity.this,Home.class);
                            Common.currentUser=login;
                            startActivity(homeActivity);

                            finish();
                         //   Toast.makeText(MainActivity.this,"Login ok",Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(MainActivity.this,"Wrong password",Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Please enter your user name",Toast.LENGTH_SHORT).show();

                    }
                }
                else
                    Toast.makeText(MainActivity.this,"User is not exist ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void showSignUpDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information ");

        LayoutInflater inflater =this.getLayoutInflater();
        View sign_up_layout=inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser=(EditText)sign_up_layout.findViewById(R.id.edtNewUserName);
        edtNewEmail=(EditText)sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword=(EditText)sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);
        alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface ,int i){
                Toast.makeText(MainActivity.this,"Dialog closed!",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();

            }


        });

        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener(){

        @Override
            public void onClick(DialogInterface dialogInterface,int i){
            final User user =new User(edtNewUser.getText().toString(),edtNewPassword.getText().toString(),edtNewEmail.getText().toString());
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(user.getUserName()).exists())
                        Toast.makeText(MainActivity.this,"User alredy exist!",Toast.LENGTH_SHORT).show();
                    else
                    {
                        users.child(user.getUserName())
                                .setValue(user);
                        Toast.makeText(MainActivity.this,"User registration success !",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            dialogInterface.dismiss();

        }
        });
        alertDialog.show();
    }







}
