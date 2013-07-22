package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class MineralesDBHelper
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MineralesDBHelper extends SQLiteOpenHelper{
	
	private Context contexto;
	private static final String DB_NAME = "bdminerales.db";
	private static final int DB_VERSION = 1;
	private final String SQLQuery = "CREATE TABLE minerales(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
									+ "nombre TEXT NOT NULL, composicion TEXT NOT NULL, disponibilidad INTEGER NOT NULL, "
									+ "dureza INTEGER NOT NULL, fecha LONG, notas TEXT);";
	
	public MineralesDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.contexto = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQLQuery);
		
		InputStream fileSQL = contexto.getResources().openRawResource(R.raw.script_dbminerales);
		BufferedReader brin = new BufferedReader(new InputStreamReader(fileSQL));
		
		try{
			do {
				String sentenciaSQL = brin.readLine();
		    	 
				if(null == sentenciaSQL) break;
			     
				db.execSQL(sentenciaSQL);
		    	 
			} while(true);
			
			fileSQL.close();
		}
		catch(Exception ex){
			Log.e("MineralesDBHelper.onCreate",ex.toString());
		}

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS minerales");
		onCreate(db);
	}
	
}
