package com.x.down;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DownloadDatabase extends SQLiteOpenHelper{
    static final String TABLE_NAME="xDownload";

    static final String _ID         ="id";
    static final String _TAG        ="tag";
    static final String _URL        ="url";
    static final String _SAVE       ="save";
    static final String _HEADER     ="header";
    static final String _PARAMS     ="params";
    static final String _FILENAME   ="filename";
    static final String _MD5        ="md5";
    static final String _STATUS     ="status";
    static final String _BLOCK      ="block";
    static final String _TOTAL      ="total";
    static final String _SOFAR      ="sofar";
    static final String _RPOGRESS   ="progress";

    static final String SQLITE_CREATE=
            "CREATE TABLE "+TABLE_NAME+"("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            _TAG+" TEXT,"+
            _URL+" TEXT," +
            _SAVE+" TEXT," +
            _HEADER+" TEXT," +
            _PARAMS+" TEXT," +
            _FILENAME+" TEXT," +
            _MD5+" TEXT," +
            _STATUS+" INTEGER," +
            _BLOCK+" INTEGER," +
            _TOTAL+" INTEGER," +
            _SOFAR+" INTEGER," +
            _RPOGRESS+" TEXT," +
            "UNIQUE ("+_TAG+")" +
            ")";

    public DownloadDatabase( Context context)
    {
        super(context,"XDownload.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQLITE_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
}
