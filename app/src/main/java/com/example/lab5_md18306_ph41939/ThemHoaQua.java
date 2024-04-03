package com.example.lab5_md18306_ph41939;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lab5_md18306_ph41939.Model.Distrobutor;
import com.example.lab5_md18306_ph41939.Model.Fruit;
import com.example.lab5_md18306_ph41939.Model.Response;
import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ThemHoaQua extends AppCompatActivity {
    private String id_Distributor;
    private ArrayList<Distrobutor> distrobutorArrayList;
    private ArrayList<File> listImg;
    ImageView imgAnh;
    TextInputLayout tilTen, tilGia, tilSoLuong, tilTrangThai, tilMoTa;
    TextInputEditText edtTen, edtGia, edtSoLuong, edtTrangThai, edtMoTa;
    Spinner spnCongTy;
    Button btnQuayLai, btnThemSp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_hoa_qua);
        imgAnh = findViewById(R.id.imgChonAnh);
        tilTen = findViewById(R.id.tilTenSp);
        tilGia = findViewById(R.id.tilGia);
        tilSoLuong = findViewById(R.id.tilSoLuong);
        tilTrangThai = findViewById(R.id.tilTrangThai);
        tilMoTa = findViewById(R.id.tilMoTa);
        edtTen = findViewById(R.id.edtTenSp);
        edtGia = findViewById(R.id.edtGia);
        edtSoLuong = findViewById(R.id.edtSoLuong);
        edtTrangThai = findViewById(R.id.edtTrangThai);
        edtMoTa = findViewById(R.id.edtMoTa);
        spnCongTy = findViewById(R.id.sp_distributor);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        btnThemSp = findViewById(R.id.btnThemFruit);
        listImg = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServices apiService = retrofit.create(ApiServices.class);
        apiService.getListDistributor().enqueue(new Callback<ArrayList<Distrobutor>>() {
            @Override
            public void onResponse(Call<ArrayList<Distrobutor>> call, retrofit2.Response<ArrayList<Distrobutor>> response) {
                distrobutorArrayList = response.body();
                String[] items = new String[distrobutorArrayList.size()];

                for (int i = 0; i < distrobutorArrayList.size(); i++) {
                    items[i] = distrobutorArrayList.get(i).getName();
                }
                ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(ThemHoaQua.this, android.R.layout.simple_spinner_item, items);
                adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnCongTy.setAdapter(adapterSpin);
            }

            @Override
            public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                t.getMessage();
            }
        });
        spnCongTy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_Distributor = distrobutorArrayList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnCongTy.setSelection(0);
        imgAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThemHoaQua.this, DanhSachHoaQua.class));
            }
        });
        btnThemSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String , RequestBody> mapRequestBody = new HashMap<>();
                String _name = edtTen.getText().toString().trim();
                String _quantity = edtSoLuong.getText().toString().trim();
                String _price = edtGia.getText().toString().trim();
                String _status = edtTrangThai.getText().toString().trim();
                String _description = edtMoTa.getText().toString().trim();
                boolean check = true;
                if (TextUtils.isEmpty(_name)) {
                    tilTen.setError("Vui lòng nhập tên mặt hàng!");
                    check = false;
                } else {
                    tilTen.setError(null);
                }

                if (TextUtils.isEmpty(_quantity)) {
                    tilSoLuong.setError("Vui lòng nhập số lượng mặt hàng!");
                    check = false;
                } else if (!TextUtils.isDigitsOnly(_quantity) || Integer.parseInt(_quantity) <= 0) {
                    tilSoLuong.setError("Số lượng phải là số lớn hơn 0!");
                    check = false;
                } else {
                    tilSoLuong.setError(null);
                }

                if (TextUtils.isEmpty(_price)) {
                    tilGia.setError("Vui lòng nhập giá mặt hàng!");
                    check = false;
                } else if (!TextUtils.isDigitsOnly(_price) || Integer.parseInt(_price) <= 0) {
                    tilGia.setError("Giá phải là số lớn hơn 0!");
                    check = false;
                } else {
                    tilGia.setError(null);
                }

                if (TextUtils.isEmpty(_status)) {
                    tilTrangThai.setError("Vui lòng nhập trạng thái mặt hàng!");
                    check = false;
                } else if (!(_status.equals("0") || _status.equals("1"))) {
                    tilTrangThai.setError("Trạng thái phải là 0 (Còn) hoặc 1 (Hết)!");
                    check = false;
                } else {
                    tilTrangThai.setError(null);
                }

                if (TextUtils.isEmpty(_price)) {
                    tilMoTa.setError("Vui lòng nhập mô tả mặt hàng!");
                    check = false;
                } else {
                    tilTen.setError(null);
                }
                if (!check) {
                    return;
                }
                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("status", getRequestBody(_status));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("id_distributor", getRequestBody(id_Distributor));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
                listImg.forEach(file1 -> {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),file1);
                    MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(),requestFile);
                    _ds_image.add(multipartBodyPart);
                });
                apiService.addFruitWithFileImage(mapRequestBody, _ds_image).enqueue(responseFruit);
            }
        });
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        getImage.launch(intent);
    }
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Uri tempUri = null;
                        listImg.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                tempUri = imageUri;
                                File file = createFileFormUri(imageUri, "image" + i);
                                listImg.add(file);
                            }
                        } else if (data.getData() != null) {
                            Uri imageUri = data.getData();

                            tempUri = imageUri;
                            File file = createFileFormUri(imageUri, "image" );
                            listImg.add(file);
                        }
                        if (tempUri != null) {
                            Glide.with(ThemHoaQua.this)
                                    .load(tempUri)
                                    .thumbnail(Glide.with(ThemHoaQua.this).load(R.drawable.fruit))
                                    .centerCrop()
                                    .circleCrop()
                                    .skipMemoryCache(true)
                                    .into(imgAnh);
                        }

                    }
                }
            });
    private File createFileFormUri (Uri path, String name) {
        File _file = new File(ThemHoaQua.this.getCacheDir(), name + ".png");
        try {
            InputStream in = ThemHoaQua.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) >0) {
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
    Callback<Response<Fruit>> responseFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus()==200) {
                    Toast.makeText(ThemHoaQua.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ThemHoaQua.this, DanhSachHoaQua.class));
                    finish();
                } else {
                    Toast.makeText(ThemHoaQua.this, "Thêm mới thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("111111", "onResponse: " + response.body().getStatus());
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Log.e("zzzzzzzzzz", "onFailure: "+t.getMessage());
        }
    };
    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),value);
    }
}