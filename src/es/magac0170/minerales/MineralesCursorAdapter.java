package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class MineralesCursorAdapter
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class MineralesCursorAdapter extends SimpleCursorAdapter{
	// Redefinimos el método que dibuja los elementos del listado
	public MineralesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		
		setViewBinder(new MineralesDatosViewBinder(context));
	}
	
}
