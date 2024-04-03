package com.example.lab5_md18306_ph41939.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab5_md18306_ph41939.Model.Fruit;
import com.example.lab5_md18306_ph41939.R;
import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    List<Fruit> list;
    Context context;
    private updateFruit updateFruit;
    public FruitAdapter (Context context, List<Fruit> fruits, updateFruit updateFruit) {
        this.context = context;
        this.list = fruits;
        this.updateFruit = updateFruit;
    }
    public interface updateFruit {
        void capNhat (Fruit fruit);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Fruit fruit = list.get(position);
        holder.txtTen.setText("Tên: " + fruit.getName());
        holder.txtSoLuong.setText("Số lượng: " + fruit.getQuantity());
        holder.txtGia.setText("Giá: " + fruit.getPrice());
        holder.txtMoTa.setText("Mô tả: " + fruit.getDescription());
        Glide.with(context)
                .load(fruit.getImage().get(0))
                .thumbnail(Glide.with(context).load(R.drawable.fruit))
                .into(holder.imgHoaQua);
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fruit fruit = list.get(position);
                String idFruit = fruit.get_id();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa hoa quả");
                builder.setMessage("Bạn có chắc chắn muốn xóa không?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiServices.link)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiServices apiService = retrofit.create(ApiServices.class);
                        Call<Void> call = apiService.deleteFruits(idFruit);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Xóa mặt hàng thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Không thể xóa mặt hàng này!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Lỗi khi xóa mặt hàng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(context, "Giữ nguyên thông tin!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        holder.btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFruit.capNhat(fruit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtSoLuong, txtGia, txtMoTa;
        ImageView imgHoaQua;
        ImageButton btnXoa, btnCapNhat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.tvTen);
            txtSoLuong = itemView.findViewById(R.id.tvSoLuong);
            txtGia = itemView.findViewById(R.id.tvGia);
            txtMoTa = itemView.findViewById(R.id.tvMoTa);
            btnCapNhat = itemView.findViewById(R.id.btn_edit);
            btnXoa = itemView.findViewById(R.id.btn_delete);
            imgHoaQua = itemView.findViewById(R.id.imgFruit);
        }
    }
}
