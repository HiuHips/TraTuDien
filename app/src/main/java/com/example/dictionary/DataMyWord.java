package com.example.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class DataMyWord extends SQLiteOpenHelper {

    public DataMyWord(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void QuerryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor GetData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void INSERT_TU(String el, String vne, byte[] hinh){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO MyWords VALUES(null, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, el);
        statement.bindString(2, vne);
        statement.bindBlob(3, hinh);

        statement.executeInsert();
    }

    public void UPDATE_TU(int id, String el, String vne, byte[] hinh){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE MyWords SET English = ?, Vietnamese = ?, HinhAnh = ? WHERE Id = '"+id+"'";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, el);
        statement.bindString(2, vne);
        statement.bindBlob(3, hinh);

        statement.executeInsert();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
