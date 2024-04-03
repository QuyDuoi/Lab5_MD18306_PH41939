package com.example.lab5_md18306_ph41939.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_md18306_ph41939.Model.Distrobutor;
import com.example.lab5_md18306_ph41939.R;
import com.example.lab5_md18306_ph41939.Services.ApiServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DistrobutorAdapter extends RecyclerView.Adapter<DistrobutorAdapter.ViewHolder> {
    List<Distrobutor> list;
    Context context;

    public DistrobutorAdapter(Context context, List<Distrobutor> distrobutors) {
        this.context = context;
        this.list = distrobutors;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_distributor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Distrobutor distrobutor = list.get(position);
        int stt = position+1;
        holder.txtSTT.setText(""+stt);
        holder.lblTenCty.setText(distrobutor.getName());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Distrobutor index = list.get(position);
                String idUpdate = index.getId();
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = inflater.inflate(R.layout.dialog_them_sua_distributor, null);
                builder.setView(view1);
                Dialog dialog = builder.create();
                dialog.show();
                TextView txtTitle = view1.findViewById(R.id.txtTitle);
                TextInputEditText edtCty = view1.findViewById(R.id.edtDistributor);
                TextInputLayout tilCty = view1.findViewById(R.id.tilDistributor);
                Button btnCancelSua = view1.findViewById(R.id.btnCancelThem);
                Button btnSaveSua = view1.findViewById(R.id.btnSaveThem);
                txtTitle.setText("Cập nhật thông tin");
                edtCty.setText(index.getName());
                btnSaveSua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tenCty = edtCty.getText().toString();
                        boolean check = true;
                        if (TextUtils.isEmpty(tenCty)) {
                            tilCty.setError("Vui lòng nhập nhà phân phối");
                            check = false;
                        } else {
                            tilCty.setError(null);
                        }

                        if (!check) {
                            return;
                        }

                        Distrobutor distrobutor1 = new Distrobutor();
                        distrobutor1.setName(tenCty);
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiServices.link)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiServices apiService = retrofit.create(ApiServices.class);
                        Call<Distrobutor> call = apiService.updateDistributor(idUpdate, distrobutor1);
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
                                                list.clear();
                                                list.addAll(response.body());
                                                notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Distrobutor>> call, Throwable t) {
                                            Log.e("Main", t.getMessage());
                                        }
                                    });
                                    dialog.dismiss();
                                    Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Không thể cập nhật thông tin", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Distrobutor> call, Throwable t) {
                                Toast.makeText(context, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                btnCancelSua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Toast.makeText(context, "Giữ nguyên thông tin!", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Distrobutor index = list.get(position);
                String idCongTy = index.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa nhà phân phối");
                builder.setMessage("Bạn có chắc chắn muốn xóa không?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiServices.link)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiServices apiService = retrofit.create(ApiServices.class);
                        Call<Void> call = apiService.deleteDistributor(idCongTy);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Xóa nhà phân phối thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Không thể xóa nhà phân phối", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Lỗi khi xóa nhà phân phối", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSTT, lblTenCty;
        ImageButton btnXoa;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSTT = itemView.findViewById(R.id.soThuTu);
            lblTenCty = itemView.findViewById(R.id.lblTenCty);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

    public void updateData(List<Distrobutor> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }

}
