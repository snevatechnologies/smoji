package com.sneva.smoji.view.search;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleEmojiDataAdapter extends SQLiteOpenHelper implements DataAdapter<Emoji> {

    private static File DB_FILE;
    private static final String DATABASE_NAME = "emoji.db";
    private static final int DATABASE_VERSION = 1;
    public boolean querySearchLikeEnabled = true;

    public Context context;
    static SQLiteDatabase sqliteDataBase;

    public SimpleEmojiDataAdapter(Context context) {
        super(context, DATABASE_NAME, null ,DATABASE_VERSION);
        DB_FILE = context.getDatabasePath(DATABASE_NAME);
        this.context = context;
    }

    private void createDataBase(){
        if(!DB_FILE.exists()){
            this.getReadableDatabase();
            this.close();
            try {
                InputStream myInput = context.getAssets().open(DATABASE_NAME);
                OutputStream myOutput = new FileOutputStream(DB_FILE);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();

                openDataBase();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            if (sqliteDataBase == null) openDataBase();
        }
    }

    private void openDataBase() throws SQLException {
        sqliteDataBase = SQLiteDatabase.openDatabase(DB_FILE.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(sqliteDataBase != null)
            sqliteDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void init() {
        try {
            createDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public List<Emoji> searchFor(String value) {
        String search = fixSearchValue(value);
        List<Emoji> list = new ArrayList<>();

        if (customs!=null){
            list.addAll(customs);
            customs = null;
        }

        if (sqliteDataBase == null || search.isEmpty()) return list;

        load("SELECT * FROM emojis WHERE name = ? COLLATE NOCASE",search,list);
        if (querySearchLikeEnabled) load("SELECT * FROM emojis WHERE name LIKE ? COLLATE NOCASE",search+"%",list);

        return list;
    }

    protected void load (String query,String search,List<Emoji> list){
        try (Cursor cursor = sqliteDataBase.rawQuery(query, new String[]{search})) {
            while (cursor.moveToNext()) {
                Emoji em = SmojiManager.getInstance().findEmoji(cursor.getString(cursor.getColumnIndex("unicode")));
                if (em != null && !list.contains(em)) list.add(em);
            }
        }
    }

    @Override
    public void destroy() {
        close();
    }

    public String fixSearchValue(String value){
        String text = value.trim().toLowerCase();
        if (text.equalsIgnoreCase("heart")){
            loadSpecialEmoji(EmojiData.getHeartEmojis());
            return text;
        }
        if (text.equals(":)") || text.equals(":-)")) text = "smile";
        if (text.equals(":(") || text.equals(":-(")) {
            loadSpecialEmoji("😔","😕","☹","🙁","🥺","😢","😥","\uD83D\uDE2D","\uD83D\uDE3F","\uD83D\uDC94");
            return "";
        }
        if (text.equals(":|") || text.equals(":/") || text.equals(":\\")
                || text.equals(":-/" )|| text.equals(":-\\") || text.equals(":-|")) text = "meh";
        if (text.equals(";)") || text.equals(";-)") || text.equals(";-]")) text = "wink";
        if (text.equals(":]")) {
            loadSpecialEmoji("😏");
            return "";
        }
        if (text.equals(":D") || text.equals(";D")){
            loadSpecialEmoji("😁","😃","😄","\uD83D\uDE06");
            return "";
        }
        if (text.equals("=|") || text.equals("=/") || text.equals("=\\")) {
            loadSpecialEmoji("😐","😕","😟");
            return "";
        }
        return text;
    }

    List<Emoji> customs = null;

    protected void loadSpecialEmoji(String... emoji){
        customs = new ArrayList<>();
        for (String e : emoji){
            Emoji em = SmojiManager.getInstance().findEmoji(e);
            if (em!=null && !customs.contains(em)) customs.add(em);
        }
    }
}
