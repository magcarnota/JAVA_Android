package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class GestionarMineral
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GestionarMineral extends Activity{
	
	private MineralesDBAdapter dbHelper;
	
	private EditText nombre;				// VISTAS
	private EditText composicion;
	private ToggleButton disponibilidad;
	private DatePicker fecha;
	private Spinner dureza;
	private EditText notas;
	private Button guardar;
	
	private Long filaId;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		dbHelper = new MineralesDBAdapter(this);
		dbHelper.openDB();
		
		setContentView(R.layout.editar_mineral);
		
		nombre = (EditText)findViewById(R.id.nombreTB);							// Obtenemos los IDs de las vistas
		composicion = (EditText)findViewById(R.id.composicionTB);
		disponibilidad = (ToggleButton)findViewById(R.id.disponibilidadTB);
		fecha = (DatePicker)findViewById(R.id.fechaDP);
		dureza = (Spinner)findViewById(R.id.durezaSP);
		notas = (EditText)findViewById(R.id.notasTB);
		guardar = (Button)findViewById(R.id.guardarBT);
		
		filaId = null;								// ID del registro actual para ediciones
		Bundle extras = getIntent().getExtras();	// Obtenemos ID pasado en la invocación de la actividad si estamos editando el registro
		
		if (null != extras)							// Si extras contiene algo [estamos editando] cargamos ese ID
			filaId = extras.getLong(MineralesDBAdapter.CAMPO_ID);

		cargarRegistro();							// Cargamos el registro en los componentes de la pantalla

		guardar.setOnClickListener(new View.OnClickListener() {		// Listener del botón GUARDAR
			public void onClick(View view) {						// onClick guardamos los datos y devolvemos OK a la Actividad
				String nombreMineral = nombre.getText().toString();
				String composicionMineral = composicion.getText().toString();
				Integer disponibilidadMineral = (disponibilidad.isChecked()) ? 1 : 0;
				String fechaSeleccionada = null;
				
				if ((nombreMineral.isEmpty()) || (composicionMineral.isEmpty())) {	// Si no se escribe el título o el autor mostramos un mensaje
					Toast.makeText(getBaseContext(), "Es obligatorio rellenar los campos 'nombre' y 'composición'.", Toast.LENGTH_SHORT).show();
					return;
				} 
				else if (null != fecha) {			// Añadir 1 a mes porque el DatePicker los almacena del 0 al 11
					fechaSeleccionada = fecha.getDayOfMonth() + "-" + ( fecha.getMonth() + 1 ) + "-" + fecha.getYear();
				}
				
				Integer durezaMineral =  Integer.parseInt(dureza.getSelectedItem().toString());
				String notasMineral = notas.getText().toString();

				if (null == filaId)			// ALTA
					dbHelper.crearMineral(nombreMineral, composicionMineral, disponibilidadMineral, fechaSeleccionada, durezaMineral, notasMineral);
				else 						// ACTUALIZACIÓN
					dbHelper.actualizarMineral(filaId, nombreMineral, composicionMineral, disponibilidadMineral, fechaSeleccionada, durezaMineral, notasMineral);
				
				setResult(RESULT_OK);		// Devolvemos el resultado de la actividad
				
				finish();					// Acabamos la actividad
			}
		}); // end guardar.setOnClickListener()

	}

	private void cargarRegistro() {				// CARGAMOS LOS DATOS DEL REGISTRO
		if (null != filaId) {					// En caso de que exista un ID [cargar datos para editar]
			Cursor mineral = dbHelper.getMineral(filaId);
			startManagingCursor(mineral);		// Empezamos a gestionar el Cursor
			
			// NOMBRE
			nombre.setText(mineral.getString(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_NOMBRE)));
			
			// COMPOSICIÓN
			composicion.setText(mineral.getString(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_COMPOSICION)));

			// DISPONIBILIDAD
			Integer disponibilidadMaterial = mineral.getInt(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_DISPONIBILIDAD));
			disponibilidad.setChecked((disponibilidadMaterial == 1) ? true : false);

			// FECHA [String dd-mm-yyyy a int dd, int mm, int yyyy]
			String fechaCompuesta = mineral.getString(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_FECHA));
			String[] fechaSeparada = fechaCompuesta.split("-"); 
			fecha.updateDate(Integer.parseInt(fechaSeparada[2].trim()),			// Año [yyyy]
							 Integer.parseInt(fechaSeparada[1].trim())-1,		// Mes [mm] - (restar 1 porque se almacenan los meses del 0 al 11)	
							 Integer.parseInt(fechaSeparada[0].trim()));		// Día [dd]
			
			// DUREZA
			String durezaMineral = mineral.getString(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_DUREZA));
			dureza.setSelection(Integer.parseInt(durezaMineral)-1);			// Durezas de 1 a 10 y selecciones de 0 a 9, por eso hay que restar 1
			
			// NOTAS
			notas.setText(mineral.getString(mineral.getColumnIndexOrThrow(MineralesDBAdapter.CAMPO_NOTAS)));

			stopManagingCursor(mineral);		// Dejamos de gestionar el Cursor
		}
	}
	
}
