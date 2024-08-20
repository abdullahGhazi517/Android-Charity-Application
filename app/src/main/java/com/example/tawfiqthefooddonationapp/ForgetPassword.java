package com.example.tawfiqthefooddonationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private EditText edtTxtResetEmail;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        edtTxtResetEmail = findViewById(R.id.edttxtresetemail);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtTxtResetEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgetPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ForgetPassword.this, "Error sending reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
