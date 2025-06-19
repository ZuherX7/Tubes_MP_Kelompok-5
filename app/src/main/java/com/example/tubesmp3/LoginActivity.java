package com.example.tubesmp3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubesmp3.UserDatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnAdmin;
    LinearLayout btnGoogle, btnFacebook;
    TextView tvBuatAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        tvBuatAkun = findViewById(R.id.tvBuatAkun);

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi username & password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUser(username, password)) {
                // Ambil data user dari database
                UserModel user = dbHelper.getUserData(username);

                // Simpan data user ke SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (user != null) {
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                } else {
                    // Fallback jika getUserData gagal
                    editor.putString("username", username);
                    editor.putString("email", username + "@example.com");
                }
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish(); // tambahin biar gak bisa balik ke login lagi pakai tombol back
            } else {
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Login dengan Google", Toast.LENGTH_SHORT).show();
        });

        btnFacebook.setOnClickListener(v -> {
            Toast.makeText(this, "Login dengan Facebook", Toast.LENGTH_SHORT).show();
        });

        // BAGIAN YANG LU MINTA: PINDAH KE REGISTER
        tvBuatAkun.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}