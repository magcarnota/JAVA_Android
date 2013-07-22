package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class MineralesDatosViewBinder
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MineralesDatosViewBinder implements SimpleCursorAdapter.ViewBinder {

	private Context contexto;
	
	public MineralesDatosViewBinder(Context context){	// En el constructor guardamos el contexto de la aplicación principal
		this.contexto = context;
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		/*
		 * Vamos obteniendo con getColumnIndex la columna que vamos a dibujar
		 * y usamos el componente view para redibujarlo
		 */
		int columnaNombre = cursor.getColumnIndex(MineralesDBAdapter.CAMPO_NOMBRE);
		if(columnaNombre == columnIndex) {	// NOMBRE
			String nombre = cursor.getString(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_NOMBRE));
			TextView tV = (TextView)view;
			tV.setText(nombre);
			return true;
		} 
		else {	// COMPOSICIÓN
			int columnaVal  = cursor.getColumnIndex(MineralesDBAdapter.CAMPO_COMPOSICION);
			if(columnaVal == columnIndex) {
				String composicion = cursor.getString(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_COMPOSICION));
				TextView tV = (TextView)view;
				tV.setText(composicion);
				return true;
			}
			else	// DISPONIBILIDAD
				if (columnIndex == cursor.getColumnIndex(MineralesDBAdapter.CAMPO_DISPONIBILIDAD)) {
					int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_DISPONIBILIDAD));
					TextView tV = (TextView)view;
					if (1 == disponibilidad)
						tV.setBackgroundColor(Color.GREEN);
					else
						tV.setBackgroundColor(Color.RED);
					return true;
				}
				else	// FECHA
					if (columnIndex == cursor.getColumnIndex(MineralesDBAdapter.CAMPO_FECHA)) {
						String fecha = cursor.getString(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_FECHA));
						TextView tV = (TextView)view;
						tV.setText(fecha);
						return true;
					}
					else	// DUREZA
						if (columnIndex == cursor.getColumnIndex(MineralesDBAdapter.CAMPO_DUREZA)) {
							Integer dureza = cursor.getInt(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_DUREZA));
							TextView tV = (TextView)view;
							tV.setText(dureza.toString());
							return true;
						}
						else	// NOTAS
							if (columnIndex == cursor.getColumnIndex(MineralesDBAdapter.CAMPO_NOTAS)) {
								String notas = cursor.getString(cursor.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_NOTAS));
								TextView tV = (TextView)view; 
								if (notas.length() > 0)
									tV.setBackgroundColor(Color.GREEN);
								else
									tV.setBackgroundColor(Color.RED);
								return true;
							}
		}
		return false;
	}

}
