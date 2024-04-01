package com.example.lab5_md18306_ph41939;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab5_md18306_ph41939.Model.Response;
import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DangNhap extends AppCompatActivity {
    TextInputEditText edtTaiKhoan, edtMatKhau;
    TextInputLayout tilTaiKhoan, tilMatKhau;
    Button btnDangNhap;
    TextView dangKy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        edtTaiKhoan = findViewById(R.id.edtTaiKhoanDn);
        edtMatKhau = findViewById(R.id.edtMatKhauDn);
        tilTaiKhoan = findViewById(R.id.tilTaiKhoanDn);
        tilMatKhau = findViewById(R.id.tilMatKhauDn);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        dangKy = findViewById(R.id.txtDangKy);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServices apiService = retrofit.create(ApiServices.class);
        dangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DangNhap.this, DangKy.class));
                finish();
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                String _username = edtTaiKhoan.getText().toString();
                String _password = edtMatKhau.getText().toString();
                boolean check = true;
                if (TextUtils.isEmpty((_username))) {
                    tilTaiKhoan.setError("Vui lòng nhập tài khoản!");
                    check = false;
                } else {
                    tilTaiKhoan.setError(null);
                }

                if (TextUtils.isEmpty(_password)) {
                    tilMatKhau.setError("Vui lòng nhập mật khẩu!");
                    check = false;
                } else {
                    tilMatKhau.setError(null);
                }

                if (!check) {
                    return;
                }

                user.setUsername(_username);
                user.setPassword(_password);
                apiService.login(user).enqueue(responseUser);
            }
        });
    }
    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if(response.isSuccessful()) {
                if(response.body().getStatus() == 200) {
                    Toast.makeText(DangNhap.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getToken());
                    editor.putString("refreshToken", response.body().getRefreshToken());
                    editor.putString("id", response.body().getData().getId());
                    editor.apply();
                    startActivity(new Intent(DangNhap.this, MainActivity.class));
                } else {
                    Toast.makeText(DangNhap.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            Log.d(">>> GetListDistributor", "onFailure"+ t.getMessage());
        }
    };
}