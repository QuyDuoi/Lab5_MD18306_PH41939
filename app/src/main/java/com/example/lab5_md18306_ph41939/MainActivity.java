package com.example.lab5_md18306_ph41939;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcv;
    private DistrobutorAdapter adapter;
    ArrayList<Distrobutor> list = new ArrayList<>();
    TextInputEditText edtDistributor;
    TextInputLayout tilDistributor;
    EditText edtSearch;
    ImageButton btnThem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcv = findViewById(R.id.rcvDistributor);
        btnThem = findViewById(R.id.btnAdd);
        edtSearch = findViewById(R.id.edtSearch);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiServices.link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServices apiService = retrofit.create(ApiServices.class);
        Call<ArrayList<Distrobutor>> distributor = apiService.getListDistributor();
        distributor.enqueue(new Callback<ArrayList<Distrobutor>>() {
            @Override
            public void onResponse(Call<ArrayList<Distrobutor>> call, retrofit2.Response<ArrayList<Distrobutor>> response) {
                if (response.isSuccessful()){
                    list = response.body();

                    adapter = new DistrobutorAdapter(MainActivity.this, list);
                    rcv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    rcv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themCongTy();
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String key = edtSearch.getText().toString();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiServices.link)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiServices apiService = retrofit.create(ApiServices.class);
                    Call<ArrayList<Distrobutor>> search = apiService.searchDistributor(key);

                    // Thực hiện cuộc gọi mạng để tìm kiếm
                    search.enqueue(new Callback<ArrayList<Distrobutor>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Distrobutor>> call, Response<ArrayList<Distrobutor>> response) {
                            if (response.isSuccessful()) {
                                ArrayList<Distrobutor> searchResult = response.body();
                                if (searchResult != null && !searchResult.isEmpty()) {
                                    adapter = new DistrobutorAdapter(MainActivity.this, searchResult);
                                    rcv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    rcv.setAdapter(adapter);
                                    Toast.makeText(MainActivity.this, "Tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Không tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Lỗi khi thực hiện tìm kiếm", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                            Log.e("Main", "Search failed: " + t.getMessage());
                        }
                    });
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
    }
    private void themCongTy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_them_sua_distributor, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        edtDistributor = dialogView.findViewById(R.id.edtDistributor);
        tilDistributor = dialogView.findViewById(R.id.tilDistributor);
        Button btnCancelThem = dialogView.findViewById(R.id.btnCancelThem);
        Button btnSaveThem = dialogView.findViewById(R.id.btnSaveThem);

        btnSaveThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtDistributor.getText().toString();

                boolean check = true;

                if (TextUtils.isEmpty(name)) {
                    tilDistributor.setError("Vui lòng nhập nhà phân phối");
                    check = false;
                } else {
                    tilDistributor.setError(null);
                }

                if (!check) {
                    return;
                }

                Distrobutor distrobutor = new Distrobutor();
                distrobutor.setName(name);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiServices.link)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiServices apiService = retrofit.create(ApiServices.class);
                Call<Distrobutor> call = apiService.addDistributor(distrobutor);
                call.enqueue(new Callback<Distrobutor>() {
                    @Override
                    public void onResponse(Call<Distrobutor> call, Response<Distrobutor> response) {
                        if (response.isSuccessful()){
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(ApiServices.link)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            ApiServices apiService = retrofit.create(ApiServices.class);
                            Call<ArrayList<Distrobutor>> distributor = apiService.getListDistributor();
                            distributor.enqueue(new Callback<ArrayList<Distrobutor>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Distrobutor>> call, retrofit2.Response<ArrayList<Distrobutor>> response) {
                                    if (response.isSuccessful()){
                                        adapter.updateData(response.body());
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                                    Log.e("Main", t.getMessage());
                                }
                            });
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Thêm nhà phân phối thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Distrobutor> call, Throwable t) {

                    }
                });
            }
        });
        btnCancelThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Hủy thao tác!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

}