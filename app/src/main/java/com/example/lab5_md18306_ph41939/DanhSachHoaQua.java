package com.example.lab5_md18306_ph41939;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lab5_md18306_ph41939.Adapter.FruitAdapter;
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

public class DanhSachHoaQua extends AppCompatActivity implements FruitAdapter.updateFruit{
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;
    RecyclerView rcvFruit;
    ArrayList<Fruit> ds = new ArrayList<>();
    private ArrayList<File> listImg = new ArrayList<>();
    TextInputLayout tilTenSpSua, tilSoLuongSua, tilGiaSua, tilTrangThaiSua, tilMoTaSua;
    TextInputEditText edtTenSpSua, edtSoLuongSua, edtGiaSua, edtTrangThaiSua, edtMoTaSua;
    Spinner sp_distributorSua;
    Button btnQuayLaiSua, btnUpdateFruit;
    ImageView imgChonAnhSua;
    ApiServices apiService;
    private ArrayList<Distrobutor> distrobutorArrayList;
    private String id_Distributor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_hoa_qua);
        ImageButton btnThem = findViewById(R.id.btnAddHQ);
        rcvFruit = findViewById(R.id.rcvHoaQua);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServices.class);
        sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
        apiService.getListFruit("Bearer " + token).enqueue(getListFruitAPI);
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DanhSachHoaQua.this, ThemHoaQua.class));
            }
        });
    }
    Callback<Response<ArrayList<Fruit>>> getListFruitAPI = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    ds = response.body().getData();
                    adapter = new FruitAdapter(DanhSachHoaQua.this, ds, DanhSachHoaQua.this);
                    rcvFruit.setLayoutManager(new LinearLayoutManager(DanhSachHoaQua.this));
                    rcvFruit.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
            t.getMessage();
        }
    };
    @Override
    public void capNhat(Fruit fruit) {
        String idFruit = fruit.get_id();
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = inflater.inflate(R.layout.dialog_sua_hoa_qua, null);
        builder.setView(view1);
        Dialog dialog = builder.create();
        dialog.show();
        tilTenSpSua = view1.findViewById(R.id.tilTenSpSua);
        tilGiaSua = view1.findViewById(R.id.tilGiaSua);
        tilSoLuongSua = view1.findViewById(R.id.tilSoLuongSua);
        tilMoTaSua = view1.findViewById(R.id.tilMoTaSua);
        tilTrangThaiSua = view1.findViewById(R.id.tilTrangThaiSua);
        edtTenSpSua = view1.findViewById(R.id.edtTenSpSua);
        edtGiaSua = view1.findViewById(R.id.edtGiaSua);
        edtMoTaSua = view1.findViewById(R.id.edtMoTaSua);
        edtSoLuongSua = view1.findViewById(R.id.edtSoLuongSua);
        edtTrangThaiSua = view1.findViewById(R.id.edtTrangThaiSua);
        btnQuayLaiSua = view1.findViewById(R.id.btnQuayLaiSua);
        btnUpdateFruit = view1.findViewById(R.id.btnUpdateFruit);
        imgChonAnhSua = view1.findViewById(R.id.imgChonAnhSua);
        sp_distributorSua = view1.findViewById(R.id.sp_distributorSua);
        btnQuayLaiSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(DanhSachHoaQua.this, "Giữ nguyên thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
        apiService.getListDistributor().enqueue(new Callback<ArrayList<Distrobutor>>() {
            @Override
            public void onResponse(Call<ArrayList<Distrobutor>> call, retrofit2.Response<ArrayList<Distrobutor>> response) {
                distrobutorArrayList = response.body();
                String[] items = new String[distrobutorArrayList.size()];

                for (int i = 0; i < distrobutorArrayList.size(); i++) {
                    items[i] = distrobutorArrayList.get(i).getName();
                }
                ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(DanhSachHoaQua.this, android.R.layout.simple_spinner_item, items);
                adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_distributorSua.setAdapter(adapterSpin);
            }

            @Override
            public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                t.getMessage();
            }
        });
        sp_distributorSua.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_Distributor = distrobutorArrayList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_distributorSua.setSelection(0);
        edtTenSpSua.setText(fruit.getName());
        edtTrangThaiSua.setText(fruit.getStatus());
        edtSoLuongSua.setText(fruit.getQuantity());
        edtGiaSua.setText(fruit.getPrice());
        edtMoTaSua.setText(fruit.getDescription());

        String url  = fruit.getImage().get(0);
        String newUrl = url.replace("localhost", "192.168.1.4");
        Glide.with(DanhSachHoaQua.this)
                .load(newUrl)
                .thumbnail(Glide.with(DanhSachHoaQua.this).load(R.drawable.fruit))
                .into(imgChonAnhSua);
        imgChonAnhSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpdateFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String , RequestBody> mapRequestBody = new HashMap<>();
                String _name = edtTenSpSua.getText().toString().trim();
                String _quantity = edtSoLuongSua.getText().toString().trim();
                String _price = edtGiaSua.getText().toString().trim();
                String _status = edtTrangThaiSua.getText().toString().trim();
                String _description = edtMoTaSua.getText().toString().trim();
                boolean check = true;
                if (TextUtils.isEmpty(_name)) {
                    tilTenSpSua.setError("Vui lòng nhập tên mặt hàng!");
                    check = false;
                } else {
                    tilTenSpSua.setError(null);
                }

                if (TextUtils.isEmpty(_quantity)) {
                    tilSoLuongSua.setError("Vui lòng nhập số lượng mặt hàng!");
                    check = false;
                } else if (!TextUtils.isDigitsOnly(_quantity) || Integer.parseInt(_quantity) <= 0) {
                    tilSoLuongSua.setError("Số lượng phải là số lớn hơn 0!");
                    check = false;
                } else {
                    tilSoLuongSua.setError(null);
                }

                if (TextUtils.isEmpty(_price)) {
                    tilGiaSua.setError("Vui lòng nhập giá mặt hàng!");
                    check = false;
                } else if (!TextUtils.isDigitsOnly(_price) || Integer.parseInt(_price) <= 0) {
                    tilGiaSua.setError("Giá phải là số lớn hơn 0!");
                    check = false;
                } else {
                    tilGiaSua.setError(null);
                }

                if (TextUtils.isEmpty(_status)) {
                    tilTrangThaiSua.setError("Vui lòng nhập trạng thái mặt hàng!");
                    check = false;
                } else if (!(_status.equals("0") || _status.equals("1"))) {
                    tilTrangThaiSua.setError("Trạng thái phải là 0 (Còn) hoặc 1 (Hết)!");
                    check = false;
                } else {
                    tilTrangThaiSua.setError(null);
                }

                if (TextUtils.isEmpty(_price)) {
                    tilMoTaSua.setError("Vui lòng nhập mô tả mặt hàng!");
                    check = false;
                } else {
                    tilMoTaSua.setError(null);
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
                apiService.updateFruit(idFruit, mapRequestBody, _ds_image).enqueue(responseFruit);
                dialog.dismiss();
            }
        });
        dialog.show();
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
                            Glide.with(DanhSachHoaQua.this)
                                    .load(tempUri)
                                    .thumbnail(Glide.with(DanhSachHoaQua.this).load(R.drawable.fruit))
                                    .centerCrop()
                                    .circleCrop()
                                    .skipMemoryCache(true)
                                    .into(imgChonAnhSua);
                        }

                    }
                }
            });
    private File createFileFormUri (Uri path, String name) {
        File _file = new File(DanhSachHoaQua.this.getCacheDir(), name + ".png");
        try {
            InputStream in = DanhSachHoaQua.this.getContentResolver().openInputStream(path);
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
                if (response.body().getStatus() == 200) {
                    Toast.makeText(DanhSachHoaQua.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    apiService.getListFruit("Bearer " + token).enqueue(getListFruitAPI);
                } else {
                    Toast.makeText(DanhSachHoaQua.this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
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