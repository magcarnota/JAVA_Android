package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class MineralesDBAdapter
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MineralesDBAdapter {

	private Context contexto;
	private SQLiteDatabase db;
	private MineralesDBHelper dbHelper;
	
	public static final String TABLE_DB = "minerales";
	public static final String CAMPO_ID = "_id";
	public static final String CAMPO_NOMBRE = "nombre";
	public static final String CAMPO_COMPOSICION = "composicion";
	public static final String CAMPO_DISPONIBILIDAD = "disponibilidad";
	public static final String CAMPO_FECHA = "fecha";
	public static final String CAMPO_DUREZA = "dureza";
	public static final String CAMPO_NOTAS = "notas";
	
	public MineralesDBAdapter(Context contexto) {
		this.contexto = contexto;
	}
	
	public SQLiteDatabase getBD() {
		return db;
	}

	public MineralesDBAdapter openDB() throws SQLException {
		dbHelper = new MineralesDBHelper(contexto);
		db = dbHelper.getWritableDatabase();
		
		return this;
	}

	public void closeDB() {
		dbHelper.close();
	}

	/**
	 * Inserta el mineral en la base de datos
	 * 
	 * @param nombreMineral
	 * @param composicionMineral
	 * @param disponibilidadMineral
	 * @param fechaSeleccionada
	 * @param durezaMineral
	 * @param notasMineral
	 * @return ID del mineral o -1 en error
	 */
	public long crearMineral(String nombreMineral, String composicionMineral, Integer disponibilidadMineral,
							 String fechaSeleccionada, Integer durezaMineral, String notasMineral) {

		ContentValues initialValues = crearContentValues(nombreMineral, composicionMineral,	
														 disponibilidadMineral, fechaSeleccionada,
														 durezaMineral, notasMineral);
		
		return db.insert(TABLE_DB, null, initialValues);	
	}

	/**
	 * Actualiza el mineral con ID filaId en la base de datos
	 * 
	 * @param filaId
	 * @param nombreMineral
	 * @param composicionMineral
	 * @param disponibilidadMineral
	 * @param fechaSeleccionada
	 * @param durezaMineral
	 * @param notasMineral
	 * @return TRUE si éxito / FALSE en error
	 */
	public boolean actualizarMineral(Long filaId, String nombreMineral, String composicionMineral,
									 Integer disponibilidadMineral, String fechaSeleccionada,
									 Integer durezaMineral, String notasMineral) {

		ContentValues updateValues = crearContentValues(nombreMineral, composicionMineral,
														disponibilidadMineral, fechaSeleccionada,
														durezaMineral, notasMineral);
		
		return db.update(TABLE_DB, updateValues, CAMPO_ID + "=" + filaId, null) > 0;
	}

	/**
	 * Borra todos los minerales de la base de datos
	 * 
	 * @return TRUE en éxito / FALSE en error
	 */
	public boolean borraTodosLosMinerales() {
		return db.delete(TABLE_DB, null , null) > 0;
	}

	/**
	 * Elimina el mineral con el ID indicado de la base de datos
	 * 
	 * @param id
	 * @return TRUE en éxito / FALSE en error
	 */
	public boolean borraMineral(long id) {
		return db.delete(TABLE_DB, CAMPO_ID + "=" + id, null) > 0;
	}

	/**
	 * Obtiene todos los minerales ordenados por orden
	 * 
	 * @param orden
	 * @return Cursor con todos los minerales ordenados por orden
	 */
	public Cursor obtenerMinerales(String orden) {
		return db.query(TABLE_DB, new String[] {CAMPO_ID, CAMPO_NOMBRE, CAMPO_COMPOSICION, CAMPO_DISPONIBILIDAD,
				CAMPO_FECHA, CAMPO_DUREZA, CAMPO_NOTAS}, null, null, null, null, orden);
	}

	/**
	 * Ontiene todos los minerales de la base de datos
	 * 
	 * @return Cursor con todos los minerales de la base de datos
	 */
	public Cursor obtenerMineralesBackup() {
		return db.query(TABLE_DB, null, null, null, null, null, null);
	}

	/**
	 * Obtiene el mineral con el id suministrado
	 * 
	 * @param id
	 * @return Cursor con los datos del mineral con el id suministrado
	 * @throws SQLException
	 */
	public Cursor getMineral(long id) throws SQLException {
		Cursor mCursor = db.query(true, TABLE_DB, new String[] {CAMPO_ID, CAMPO_NOMBRE, CAMPO_COMPOSICION,
				CAMPO_DISPONIBILIDAD, CAMPO_FECHA, CAMPO_DUREZA, CAMPO_NOTAS}, CAMPO_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null)		// Situándonos en el primer registro del cursor
			mCursor.moveToFirst();
		
		return mCursor;
	}

	/**
	 * Crea un objeto para almacenar los campos y sus valores
	 * 
	 * @param nombreMineral
	 * @param composicionMineral
	 * @param disponibilidadMineral
	 * @param fechaSeleccionada
	 * @param durezaMineral
	 * @param notasMineral
	 * @return ContentValues
	 */
	private ContentValues crearContentValues(String nombreMineral, String composicionMineral,
											 Integer disponibilidadMineral, String fechaSeleccionada,
											 Integer durezaMineral, String notasMineral) {
		
		ContentValues values = new ContentValues();
		
		values.put(CAMPO_NOMBRE, nombreMineral);
		values.put(CAMPO_COMPOSICION, composicionMineral);
		values.put(CAMPO_DISPONIBILIDAD, disponibilidadMineral);
		values.put(CAMPO_FECHA, fechaSeleccionada);
		values.put(CAMPO_DUREZA, durezaMineral);
		values.put(CAMPO_NOTAS, notasMineral);

		return values;
	}	
	
}
