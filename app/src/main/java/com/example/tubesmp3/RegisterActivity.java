package com.example.tubesmp3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tubesmp3.UserDatabaseHelper;


public class RegisterActivity extends AppCompatActivity {

    EditText inputUsername, inputEmail, inputPassword;
    Button btnRegister;
    TextView textLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textLoginLink = findViewById(R.id.textLoginLink);

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);

        btnRegister.setOnClickListener(v -> {
            String username = inputUsername.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isUsernameTaken(username)) {
                Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.registerUser(username, email, password);
                if (inserted) {
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        btnRegister.setOnClickListener(v -> {
//            String username = inputUsername.getText().toString();
//            String email = inputEmail.getText().toString();
//            String password = inputPassword.getText().toString();
//
//            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
//            } else {
//                // Simpan data ke DB atau SharedPreferences (sementara)
//                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
//                // redirect ke Login
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//            }
//        });

        textLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}


