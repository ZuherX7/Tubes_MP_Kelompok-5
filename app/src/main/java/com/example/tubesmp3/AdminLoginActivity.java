package com.example.tubesmp3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText etAdminUsername, etAdminPassword;
    Button btnAdminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnAdminLogin.setOnClickListener(v -> {
            String username = etAdminUsername.getText().toString();
            String password = etAdminPassword.getText().toString();

            if (username.equals("admin") && password.equals("admin123")) {
                Toast.makeText(this, "Login admin berhasil", Toast.LENGTH_SHORT).show();
                // lanjut ke halaman admin
            } else {
                Toast.makeText(this, "Username/password salah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

