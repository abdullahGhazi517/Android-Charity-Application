package com.example.tawfiqthefooddonationapp;

import android.content.Intent;
import android.text.TextUtils;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mSignUpBtn;
    private boolean isPasswordVisible = false;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mEmail = findViewById(R.id.edttxtemail);
        mPassword = findViewById(R.id.edttxtpassword);
        mSignUpBtn = findViewById(R.id.creataccount);
        mLoginBtn = findViewById(R.id.loginButton);
        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mPassword.getRight() - mPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }

            private void togglePasswordVisibility() {
                if (isPasswordVisible) {
                    mPassword

.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPassword

.setCompoundDrawablesWithIntrinsicBounds(R.drawable.custom_password_icon, 0, R.drawable.eye_closed, 0);
                } else {
                    mPassword

.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPassword

.setCompoundDrawablesWithIntrinsicBounds(R.drawable.custom_password_icon, 0, R.drawable.eye_open, 0);
                }
                isPasswordVisible = !isPasswordVisible;
                mPassword

.setSelection(mPassword

.getText().length()); // Move cursor to the end of the text
            }



        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be >=6 Characters");
                    return;
                }


                //authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.putExtra("user_id", fAuth.getCurrentUser().getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(SignIn.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, ForgetPassword.class);
                startActivity(intent);
            }
        });
    }

}