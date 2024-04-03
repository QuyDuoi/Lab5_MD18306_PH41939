package com.example.lab5_md18306_ph41939;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lab5_md18306_ph41939.Model.Response;
import com.example.lab5_md18306_ph41939.Model.User;
import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DangKy extends AppCompatActivity {
    TextInputEditText edtTaiKhoan, edtPass, edtEmail, edtHoTen;
    TextInputLayout tilTaiKhoan, tilPass, tilEmail, tilHoTen;
    TextView backDangNhap;
    ImageView imgAvatar;
    Button btnDangKy;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        edtPass = findViewById(R.id.edtPass);
        edtEmail = findViewById(R.id.edtEmail);
        edtHoTen = findViewById(R.id.edtHoTen);
        tilTaiKhoan = findViewById(R.id.tilTaiKhoan);
        tilPass = findViewById(R.id.tilPass);
        tilEmail = findViewById(R.id.tilEmail);
        tilHoTen = findViewById(R.id.tilHoTen);
        btnDangKy = findViewById(R.id.btnDangKy);
        backDangNhap = findViewById(R.id.back_dangNhap);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        backDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DangKy.this, DangNhap.class));
                finish();
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taiKhoan = edtTaiKhoan.getText().toString().trim();
                String matKhau = edtPass.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String hoTen = edtHoTen.getText().toString().trim();

                boolean check = true;

                if (TextUtils.isEmpty((taiKhoan))) {
                    tilTaiKhoan.setError("Vui lòng nhập tài khoản!");
                    check = false;
                } else if (taiKhoan.length()<6) {
                    tilTaiKhoan.setError("Tài khoản phải trên 6 ký tự!");
                    check = false;
                } else {
                    tilTaiKhoan.setError(null);
                }

                if (TextUtils.isEmpty(matKhau)) {
                    tilPass.setError("Vui lòng nhập mật khẩu!");
                    check = false;
                } else if (matKhau.length() < 6) {
                    tilPass.setError("Mật khẩu phải nhiều hơn 6 ký tự!");
                    check = false;
                } else if (!checkPass(matKhau)) {
                    tilPass.setError("Mật khẩu không đúng định dạng!");
                    check = false;
                } else {
                    tilPass.setError(null);
                }

                if (TextUtils.isEmpty(email)) {
                    tilEmail.setError("Vui lòng nhập Email!");
                    check = false;
                } else if (!checkEmail(email)) {
                    tilEmail.setError("Email không đúng định dạng!");
                    check = false;
                } else {
                    tilEmail.setError(null);
                }

                if (TextUtils.isEmpty(hoTen)) {
                    tilHoTen.setError("Vui lòng nhập họ tên!");
                    check = false;
                } else {
                    tilHoTen.setError(null);
                }

                if (!check) {
                    return;
                }

                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), edtTaiKhoan.getText().toString());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edtPass.getText().toString());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edtEmail.getText().toString());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edtHoTen.getText().toString());
                MultipartBody.Part multiparBody;
                if(file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multiparBody = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                } else {
                    multiparBody = null;
                }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiServices.link)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiServices apiService = retrofit.create(ApiServices.class);
                apiService.register(_username, _password, _email, _name, multiparBody).enqueue(responseUser);
            }
        });
    }
    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if(response.isSuccessful()) {
                if(response.body().getStatus() == 200) {
                    Toast.makeText(DangKy.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DangKy.this, DangNhap.class));
                    finish();
                } else {
                    Toast.makeText(DangKy.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            Log.d(">>> GetListDistributor", "onFailure: "+ t.getMessage());
        }
    };
    private void chooseImage() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImage.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri imagePath = data.getData();
                file = createFileFromUri(imagePath, "avatar");
                Glide.with(DangKy.this).load(file)
                        .thumbnail(Glide.with(DangKy.this)
                                .load(R.drawable.avatar))
                        .centerCrop()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgAvatar);
            }
        }
    });

    private File createFileFromUri (Uri path, String name) {
        File _file = new File(DangKy.this.getCacheDir(), name+".png");
        try {
            InputStream in = DangKy.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf))>0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean checkEmail (String email) {
        String check = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(check);
    }
    private boolean checkPass (String password) {
        String check = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        return password.matches(check);
    }
}