package com.example.dictionary.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.CustomAdapter;
import com.example.dictionary.DataMyWord;
import com.example.dictionary.MyWord;
import com.example.dictionary.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class MyWordFragment extends Fragment {
    private ListView lvMyWord;
    public static ArrayList<MyWord> data = new ArrayList<>();
    private TextView tv_tongTu;
    private Button btn_add;
    private EditText edt_search;
    private ImageButton ib_search;
    private ImageView btn_reload;
    private CustomAdapter adapter;
    private MyWord myWordIndex = null;
    private Bitmap bitmapImage = null;
    public static DataMyWord dataMyWord;

    public MyWordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = (ViewGroup) inflater.inflate(R.layout.fragment_my_word, container, false);

        //Ánh xạ
        lvMyWord = root.findViewById(R.id.lv_MyWord);
        tv_tongTu = root.findViewById(R.id.tv_sum_word);
        btn_add = root.findViewById(R.id.btn_add);
        btn_reload = root.findViewById(R.id.btn_reload);
        edt_search = root.findViewById(R.id.edtSearch);
        ib_search = root.findViewById(R.id.btn_search);

        //khởi tạo Database
        dataMyWord = new DataMyWord(getActivity(), "myword.sqlite", null, 1);
        //tạo bảng
        dataMyWord.QuerryData("CREATE TABLE IF NOT EXISTS MyWords" +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, English VARCHAR(100)," +
                " Vietnamese NVARCHAR(100), HinhAnh BLOB)");

        getDataMyWord();
        adapter = new CustomAdapter(getActivity(), R.layout.listview_item, data);
        lvMyWord.setAdapter(adapter);

        String s = String.valueOf(data.size());
        tv_tongTu.setText(s);

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edt_search.getText().toString().trim();
                if(s != null){
                    Cursor dt = dataMyWord.GetData("SELECT * FROM MyWords " +
                            "WHERE LOWER(English) = LOWER('"+s+"') OR English LIKE '%"+s+"%'");
                    data.clear();
                    if(dt.moveToFirst()){
                        do {
                            int id = dt.getInt(0);
                            String english = dt.getString(1);
                            String vietnamese = dt.getString(2);
                            byte[] hinhanh = dt.getBlob(3);
                            data.add(new MyWord(id, english, vietnamese, hinhanh));
                        }while (dt.moveToNext());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddMyWord(null, null);
            }
        });

        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataMyWord();
                adapter.notifyDataSetChanged();
                String s = String.valueOf(data.size());
                tv_tongTu.setText(s);
                Toast.makeText(getActivity(),"Reloaded.", Toast.LENGTH_SHORT).show();
            }
        });

        lvMyWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myWordIndex = data.get(position);
                openEditMyWord();
            }
        });

        lvMyWord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myWordIndex = data.get(position);
                openDeleteMyWord();
                return true;
            }
        });

        return root;
    }



    private void openDeleteMyWord() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_delete);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = myWordIndex.getId();
                dataMyWord.QuerryData("DELETE FROM MyWords WHERE Id ='"+id+"'");
                Toast.makeText(getActivity(), "Đã xóa.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                getDataMyWord();
                adapter.notifyDataSetChanged();
                String s = String.valueOf(data.size());
                tv_tongTu.setText(s);
            }
        });

        dialog.show();
    }

    public void openAddMyWord(@Nullable String el, String vne) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        //anh xa dialog
        EditText edtEnglish = dialog.findViewById(R.id.edtEnglish);
        EditText edtVietnamese = dialog.findViewById(R.id.edtVietnamese);
        ImageView edtHinh = dialog.findViewById(R.id.addHinh);
        Button btnThemhinh = dialog.findViewById(R.id.btn_themhinh);
        Button btnLoad = dialog.findViewById(R.id.btn_loadHinh);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        //gán dữ liệu vào dialog
        edtEnglish.setText(el);
        edtVietnamese.setText(vne);

        btnThemhinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestPermission
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        cameraIntent.setType("image/*");
                        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, 1000);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };

                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapImage == null){
                    Toast.makeText(getActivity(), "Chưa chọn hình.", Toast.LENGTH_SHORT).show();
                }else{
                    edtHinh.setImageBitmap(bitmapImage);
                    bitmapImage = null;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEnglish = edtEnglish.getText().toString().trim();
                String newVietnamese = edtVietnamese.getText().toString().trim();
                //Chuyển data imageview >> byte[]
                BitmapDrawable bitmapDrawable = (BitmapDrawable) edtHinh.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArray);
                byte[] hinhAnh = byteArray.toByteArray();

                if(newEnglish.equals("") || newVietnamese.equals("")){
                    Toast.makeText(getActivity(), "Vui lòng không để trống.", Toast.LENGTH_SHORT).show();
                }else{
                    dataMyWord.INSERT_TU(newEnglish, newVietnamese, hinhAnh);
                    Toast.makeText(getActivity(), "Đã thêm.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getDataMyWord();
                    String s = String.valueOf(data.size());
                    tv_tongTu.setText(s);
                }
            }
        });

        dialog.show();
    }

    public void openEditMyWord() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        //anh xa dialog
        EditText edtEnglish = dialog.findViewById(R.id.edtEnglish);
        EditText edtVietnamese = dialog.findViewById(R.id.edtVietnamese);
        ImageView edtHinh = dialog.findViewById(R.id.edtHinh);
        Button btnThemhinh = dialog.findViewById(R.id.btn_themhinh);
        Button btnLoad = dialog.findViewById(R.id.btn_loadHinh);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        //gán dữ liệu vào dialog
        edtEnglish.setText(myWordIndex.getEnglish());
        edtVietnamese.setText(myWordIndex.getVietnamese());
        byte[] hinhAnh = myWordIndex.getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
        edtHinh.setImageBitmap(bitmap);

        btnThemhinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestPermission
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        cameraIntent.setType("image/*");
                        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, 1000);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };

                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapImage == null){
                    Toast.makeText(getActivity(), "Chưa chọn hình.", Toast.LENGTH_SHORT).show();
                }else{
                    edtHinh.setImageBitmap(bitmapImage);
                    bitmapImage = null;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = myWordIndex.getId();
                String newEnglish = edtEnglish.getText().toString().trim();
                String newVietnamese = edtVietnamese.getText().toString().trim();

                BitmapDrawable bitmapDrawable = (BitmapDrawable) edtHinh.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArray);
                byte[] hinhAnh = byteArray.toByteArray();

                if(newEnglish.equals("") || newVietnamese.equals("")){
                    Toast.makeText(getActivity(), "Vui lòng không để trống.", Toast.LENGTH_SHORT).show();
                }else {
                    dataMyWord.UPDATE_TU(id,newEnglish,newVietnamese,hinhAnh);
                    Toast.makeText(getActivity(), "Đã sửa.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getDataMyWord();
                }
                adapter.notifyDataSetChanged();
            }
        });

        dialog.show();

    }

    private void getDataMyWord(){
        Cursor dt = dataMyWord.GetData("SELECT * FROM MyWords");
        data.clear();
        if(dt.moveToFirst()){
            do {
                int id = dt.getInt(0);
                String english = dt.getString(1);
                String vietnamese = dt.getString(2);
                byte[] hinhanh = dt.getBlob(3);
                data.add(new MyWord(id, english, vietnamese, hinhanh));
            }while (dt.moveToNext());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                try {
                    bitmapImage = MediaStore.Images
                            .Media.getBitmap(getActivity().getContentResolver(), returnUri);
                    Toast.makeText(getActivity(),
                            "Đã chọn ảnh, hãy load ảnh lên.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}