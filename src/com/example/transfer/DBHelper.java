package com.example.transfer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "test.db";
	private static final int DATABASE_VERSION = 3;// 3.7

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public Cursor getKen(String tableName){
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		//String[] sqlSelect = { "0 _id", "ENGNAME", "CHINAME", "LAT", "LONG",
		//		"DISTRICT" };
		//String sqlTables = tableName;

		//qb.setTables(tableName);
		Cursor c = db.rawQuery("Select * from " + tableName, null);

		// Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

		c.moveToFirst();
		return c;
	}
	
	public Cursor getDistricts() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "ENGNAME", "CHINAME", "LAT", "LONG",
				"DISTRICT" };
		String sqlTables = "BASKET_COURT";

		qb.setTables(sqlTables);
		Cursor c = db.rawQuery("Select * from BASKET_COURT", null);

		// Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

		c.moveToFirst();
		return c;

	}
}