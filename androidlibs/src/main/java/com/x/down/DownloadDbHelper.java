package com.x.down;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

class DownloadDbHelper{
    private DownloadDatabase databaseHelper;
    private SQLiteDatabase writableDatabase;

    public synchronized DownloadDbHelper start(Context context){
        if(databaseHelper==null){
            databaseHelper=new DownloadDatabase(context);
        }
        if(writableDatabase==null||!writableDatabase.isOpen()||writableDatabase.isReadOnly()){
            writableDatabase=databaseHelper.getWritableDatabase();
        }
        return this;
    }

    public synchronized DownloadDbHelper end(){
        if(writableDatabase!=null){
            writableDatabase.close();
        }
        if(databaseHelper!=null){
            databaseHelper.close();
        }
        return this;
    }

    public static synchronized DownloadDbHelper get(){
        return new DownloadDbHelper();
    }

    public DownloadDbHelper save(DownloadInfo info){
        ContentValues values=new ContentValues();
        values.put(DownloadDatabase._TAG,info.getTag());
        values.put(DownloadDatabase._URL,info.getUrl());
        values.put(DownloadDatabase._SAVE,info.getSave());
        values.put(DownloadDatabase._HEADER,info.getHeader());
        values.put(DownloadDatabase._PARAMS,info.getParams());
        values.put(DownloadDatabase._FILENAME,info.getFilename());
        values.put(DownloadDatabase._MD5,info.getMd5());
        values.put(DownloadDatabase._STATUS,info.getStatus());
        values.put(DownloadDatabase._BLOCK,info.getBlock());
        values.put(DownloadDatabase._TOTAL,info.getTotal());
        values.put(DownloadDatabase._SOFAR,info.getSofar());
        values.put(DownloadDatabase._RPOGRESS,writeInt(info.getProgress()));
        writableDatabase.replace(DownloadDatabase.TABLE_NAME,null,values);
        return this;
    }

    public DownloadInfo read(String tag){
        Cursor query=writableDatabase.query(DownloadDatabase.TABLE_NAME,null,
                                            DownloadDatabase._TAG+" = ?",new String[]{tag},null,null,null);
        if(query.moveToNext()){
            DownloadInfo info=new DownloadInfo();
            info.setId(query.getInt(query.getColumnIndex(DownloadDatabase._ID)));
            info.setTag(query.getString(query.getColumnIndex(DownloadDatabase._TAG)));
            info.setUrl(query.getString(query.getColumnIndex(DownloadDatabase._URL)));
            info.setSave(query.getString(query.getColumnIndex(DownloadDatabase._SAVE)));
            info.setHeader(query.getString(query.getColumnIndex(DownloadDatabase._HEADER)));
            info.setParams(query.getString(query.getColumnIndex(DownloadDatabase._PARAMS)));
            info.setFilename(query.getString(query.getColumnIndex(DownloadDatabase._FILENAME)));
            info.setMd5(query.getString(query.getColumnIndex(DownloadDatabase._MD5)));
            info.setStatus(query.getInt(query.getColumnIndex(DownloadDatabase._STATUS)));
            info.setBlock(query.getInt(query.getColumnIndex(DownloadDatabase._BLOCK)));
            info.setTotal(query.getLong(query.getColumnIndex(DownloadDatabase._TOTAL)));
            info.setSofar(query.getLong(query.getColumnIndex(DownloadDatabase._SOFAR)));
            info.setProgress(readInt(query.getString(query.getColumnIndex(DownloadDatabase._RPOGRESS))));
            query.close();
            return info;
        } else{
            query.close();
            return null;
        }
    }

    private String writeInt(int[] ints){
        if(ints==null||ints.length==0){
            return "";
        }
        if(ints.length==1){
            return String.valueOf(ints[0]);
        }
        StringBuilder builder=new StringBuilder();
        for(int value: ints){
            builder.append(value);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    private int[] readInt(String value){
        if(value==null){
            return null;
        }
        if(value.indexOf(",")<=0){
            return new int[]{valueOf(value)};
        }
        String[] split=value.split(",");
        int[] ints=new int[split.length];
        for(int i=0;i<split.length;i++){
            ints[i]=valueOf(split[i]);
        }
        return ints;
    }

    private int valueOf(String value){
        try{
            return Integer.valueOf(value);
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
