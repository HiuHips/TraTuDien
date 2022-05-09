package com.example.dictionary.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dictionary.DataMyWord;
import com.example.dictionary.MyWord;
import com.example.dictionary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;


public class SearchFragment extends Fragment {
    ArrayList<MyWord> data = new ArrayList<>();
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV, hinhMacDinh;
    private MaterialButton translatebtn, add;
    private TextView translatedTV;
    String[] fromLanguages = {"English", "Vietnamese"};
    String[] toLanguages = {"Vietnamese", "English"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int fromLanguageCode, toLanguageCode = 0;

    /*private DataMyWord dataMyWord;*/

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= (ViewGroup) inflater.inflate(R.layout.fragment_search, null);
        //set control
        fromSpinner = root.findViewById(R.id.idFromSpinner);
        toSpinner = root.findViewById(R.id.idToSpinner);
        sourceEdt = root.findViewById(R.id.idEditSource);
        micIV = root.findViewById(R.id.idIVMic);
        translatebtn = root.findViewById(R.id.idBtnTranslate);
        translatedTV = root.findViewById(R.id.idTVTranslated);
        add = root.findViewById(R.id.btn_add_srch);
        hinhMacDinh = root.findViewById(R.id.ivHinhMacDinh);

        //set event
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(getActivity().getBaseContext(),R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positon, long id) {
                toLanguageCode = getLanguageCode(toLanguages[positon]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");
                if(sourceEdt.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Hãy điền từ cần dịch", Toast.LENGTH_SHORT).show();
                }else if (fromLanguageCode==0){
                    Toast.makeText(getActivity(), "Hãy chọn ngôn ngữ gốc", Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode==0){
                    Toast.makeText(getActivity(), "Hãy chọn ngôn ngữ cần dịch", Toast.LENGTH_SHORT).show();
                }else {
                    translateText(fromLanguageCode, toLanguageCode, sourceEdt.getText().toString());

                }

            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Vui lòng nói gì đó");
                try {
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sourceEdt.getText().toString().isEmpty() || translatedTV.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Thêm thất bại.", Toast.LENGTH_SHORT).show();
                }else{
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) hinhMacDinh.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                    byte[] hinhAnh = byteArray.toByteArray();

                    addMyWord(sourceEdt.getText().toString().trim(),translatedTV.getText().toString().trim(), hinhAnh);
                }
            }
        });

        return root;
    }

    private void addMyWord(String newEnglish, String newVietnamese, byte[] hinhAnh) {
        MyWordFragment.dataMyWord.INSERT_TU(newEnglish, newVietnamese, hinhAnh);
        Toast.makeText(getActivity(), "Đã thêm.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(resultCode==getActivity().RESULT_OK && data!=null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source){
        translatedTV.setText("Đang dịch...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTV.setText("Đang dịch...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Không dịch được:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Lỗi khi tải ngôn ngữ"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getLanguageCode(String language){
        int languageCode = 0;
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;
    }

}