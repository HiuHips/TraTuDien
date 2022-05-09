package com.example.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dictionary.fragment.MyWordFragment;

import java.util.ArrayList;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter {
    Context context;
    int resource;
    ArrayList<MyWord> data;

    public CustomAdapter(@NonNull Context context, int resource, ArrayList<MyWord> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data =data;
    }

    @Override
    public int getCount() { return data.size();}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, null);
        TextView tvEnglish = convertView.findViewById(R.id.tvEnglisg);
        TextView tvVietnamese = convertView.findViewById(R.id.tvVietnamese);
        ImageView ivHinh = convertView.findViewById(R.id.ivHinh);

        MyWord myWord = data.get(position);
        tvEnglish.setText(myWord.getEnglish());
        tvVietnamese.setText(myWord.getVietnamese());
        //Chuyển byte[] >> Bitmap
        byte[] hinhAnh = myWord.getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
        ivHinh.setImageBitmap(bitmap);

        return convertView;
    }

}