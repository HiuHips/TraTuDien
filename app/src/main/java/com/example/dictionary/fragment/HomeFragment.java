package com.example.dictionary.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.R;

import java.util.Random;


public class HomeFragment extends Fragment {

    TextView tvEl, tvVne;
    ImageView ivHinhHome;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Ánh xạ
        View root= (ViewGroup) inflater.inflate(R.layout.fragment_home, null);
        tvEl = root.findViewById(R.id.tv_el_home);
        tvVne = root.findViewById((R.id.tv_vne_home));
        ivHinhHome = root.findViewById(R.id.iv_hinh_home);

        //Sự kiện
        Random generator = new Random();
        int p = Math.abs(MyWordFragment.data.size());
        int value = generator.nextInt(p);
        tvEl.setText(MyWordFragment.data.get(value).getEnglish());
        tvVne.setText(MyWordFragment.data.get(value).getVietnamese());
        byte[] hinhAnh = MyWordFragment.data.get(value).getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
        ivHinhHome.setImageBitmap(bitmap);

        return root;
    }

}