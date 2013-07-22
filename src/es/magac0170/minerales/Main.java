package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class Main
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

public class Main extends ListActivity {

	private Cursor cursor;
	private MineralesDBAdapter dbHelper;
	private AlertDialog.Builder window;
	private static final int ACTIVIDAD_NUEVA = 0;
	private static final int ACTIVIDAD_EDITAR = 1;
	private static final int MENU_ID = Menu.FIRST + 1;
	private String ordenListadoCampo;
	private int ordenListado, nRegistros = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dbHelper = new MineralesDBAdapter(this);
		dbHelper.openDB();
		
		// Recuperamos una variable que se almacena con el estado
		SharedPreferences prefe = PreferenceManager.getDefaultSharedPreferences(Main.this);
		ordenListadoCampo = prefe.getString("orden", MineralesDBAdapter.CAMPO_ID);
		ordenListado = prefe.getInt("opcion", 0);
		
		cargaDatos(ordenListadoCampo, ordenListado);

		registerForContextMenu(this.getListView());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != dbHelper)
			dbHelper.closeDB();
		
		// Guardamos el estado de la Actividad (el orden del listado)
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(Main.this);
		SharedPreferences.Editor editor = preferencias.edit();
		
		editor.putString("orden", ordenListadoCampo);
		editor.putInt("opcion", ordenListado);
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {				// MENÚ PRINCIPAL [NUEVO|Acerca|Ordenar por|Exportar|Importar]
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menuprincipal, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int id, MenuItem item) {	// SELECCIÓN DE OPCIÓN DE MENÚ PRINCIPAL
		switch (item.getItemId()) {
			// NUEVO
			case R.id.nuevo:
				Intent i = new Intent(this, GestionarMineral.class);	// Creamos una actividad indicando el tipo de petición "ACTIVIDAD_NUEVA"
				startActivityForResult(i, ACTIVIDAD_NUEVA);
				return true;
			// EXPORTAR
			case R.id.exportar:
				if (nRegistros < 1)
					Toast.makeText(this, "Para exportar la base de datos es necesario dar de alta algún registro.", Toast.LENGTH_SHORT).show();
				else if (compruebaAlmacenamientoExt()) {				// Comprobamos almacenamiento externo
					window = new AlertDialog.Builder(this);
					window.setIcon(android.R.drawable.ic_dialog_info);
					window.setTitle("Atención");
					window.setMessage("Esta operación puede llevar un tiempo ¿Desea continuar?");
					window.setCancelable(false);
					
					window.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int boton) {
							new ExportarBDFileTask(Main.this, dbHelper).execute();		// Iniciamos exportación en hilo nuevo
						}
					});
					
					window.setNegativeButton("No", null);
					window.create().show();
				}
				return true;
			// IMPORTAR
			case R.id.importar:
				if (compruebaAlmacenamientoExt()) {						// Comprobamos almacenamiento externo
					window = new AlertDialog.Builder(this);	
					window.setIcon(android.R.drawable.ic_dialog_info);
					window.setTitle("Atención");
					window.setMessage("Esta operación borra todos los registros y puede llevar un tiempo ¿Desea continuar?");
					window.setCancelable(false);
					
					window.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int boton) {
							new ImportarBDFileTask(Main.this, dbHelper).execute();		// Iniciamos importación en hilo nuevo
						}
					});
					
					window.setNegativeButton("No", null);
					window.create().show();
				}
				return true;
			// ACERCA
			case R.id.acerca:
				window = new AlertDialog.Builder(this);
				window.setIcon(android.R.drawable.ic_dialog_info);
				window.setTitle("Acerca de");
				window.setMessage("Esta aplicación gestiona una colección de minerales.\n" +
								  "2013 - Miguel Ángel García Carnota");
				window.setCancelable(false);
				window.setPositiveButton("Aceptar", null);
				window.create().show();
				return true;
			// ORDENAR
			case R.id.ordenar:
				window = new AlertDialog.Builder(this);
				window.setIcon(android.R.drawable.ic_dialog_alert);
				window.setTitle("Selecciona una opción");

				window.setItems(R.array.Orden, new DialogInterface.OnClickListener() {	 	// Cargamos opciones de ordenación + listener en cada una de ellas
					public void onClick(DialogInterface dialog, int opcion) {
						ordenListado = opcion;
						switch(opcion) {
							case 0:		// Orden por ID
								cargaDatos(MineralesDBAdapter.CAMPO_ID, opcion);
								break;
							case 1:		// Orden por NOMBRE
								cargaDatos(MineralesDBAdapter.CAMPO_NOMBRE + " ASC", opcion);
								break;
							case 2:		// Orden por DUREZA
								cargaDatos(MineralesDBAdapter.CAMPO_DUREZA + " DESC", opcion);
								break;
							case 3:		// Orden por DISPONIBILIDAD
								cargaDatos(MineralesDBAdapter.CAMPO_DISPONIBILIDAD + " DESC", opcion);
								break;
						}
					}
				});
				
				window.create().show();
				return true;
		}
		
		return super.onMenuItemSelected(id, item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {		// Click en ELEMENTO [EDITAR]
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, GestionarMineral.class);	// Creamos una nueva actividad {ojo AndroidManifest.xml [Application->Application Nodes]}
		
		i.putExtra(MineralesDBAdapter.CAMPO_ID, id);			// Pasamos el campo ID como un dato extra
		
		startActivityForResult(i, ACTIVIDAD_EDITAR);			// Iniciando actividad con tipo ACTIVIDAD_EDITAR y esperando el resultado
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {	// SE LLAMA AL DEVOLVER RESULTADO UNA SUBACTIVIDAD
		super.onActivityResult(requestCode, resultCode, intent);
		// Recargamos los datos si se ha modificado algo.
		// Es decir, el usuario ha hecho clic en OK
		if (resultCode == Activity.RESULT_OK)
			cursor.requery();
	}

	public void refrescarDatos() {							// REFRESCA DATOS DEL LISTADO
		cargaDatos(ordenListadoCampo, ordenListado);
	}
	
	private void cargaDatos(String orden, int opcion) {		// CARGA DATOS PARA MOSTRAR EN LISTADO
		ordenListadoCampo = orden;
		cursor = dbHelper.obtenerMinerales(orden);

		startManagingCursor(cursor);	// Gestionamos el cursor (si termina la Actividad, se elimina este Cursor de la memoria)
		
		// Indicamos cómo debe pasarse el campo título de (from) a (to) a la Vista de la opción (fila_mineral.xml)
		String[] from = new String[] {MineralesDBAdapter.CAMPO_NOMBRE, MineralesDBAdapter.CAMPO_COMPOSICION,
									  MineralesDBAdapter.CAMPO_DISPONIBILIDAD, MineralesDBAdapter.CAMPO_FECHA,
									  MineralesDBAdapter.CAMPO_DUREZA, MineralesDBAdapter.CAMPO_NOTAS};
		
		int[] to = new int[] {R.id.nombreMineralTV, R.id.composicionTV,
							  R.id.disponibilidadTV, R.id.fechaTV,
							  R.id.durezaTV, R.id.notasTV};

		// Adaptador de tipo matriz asociado al cursor
		MineralesCursorAdapter notas = new MineralesCursorAdapter(this, R.layout.fila_mineral, cursor, from, to);

		nRegistros = notas.getCount();		// Número de registros del cursor

		setListAdapter(notas);				// Indicamos al listado el adaptador que le corresponde
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {	// MENU CONTEXTUAL BORRAR
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(0, MENU_ID, 0, "borrar");
	}
	
	@Override
	public boolean onContextItemSelected(final MenuItem item) {		// Click en BORRAR MINERAL
		switch(item.getItemId()) {
			case MENU_ID:
				window = new AlertDialog.Builder(this);								// Pedimos confirmación del borrado del registro
				window.setIcon(android.R.drawable.ic_dialog_info);
				window.setTitle("ALERTA");
				window.setMessage("¿Estás seguro de querer borrar este mineral?");
				window.setCancelable(false);
				
				window.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int boton) {						// Al pulsar SI
						AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();	// Obtener ID del elemento seleccionado
						dbHelper.borraMineral(info.id);												// Borramos ese registro
						cargaDatos(ordenListadoCampo, ordenListado);								// Recargamos los datos
					}
				});
				
				window.setNegativeButton("No", null);
				window.create().show();
				
				return true;
		}
		
		return super.onContextItemSelected(item);
	}

	private boolean compruebaAlmacenamientoExt(){				// COMPROBAR ALMACENAMIENTO EXTERNO (si está activo y se puede escribir)
		String estado = Environment.getExternalStorageState();	// Obtenemos el estado del almacenamiento externo del teléfono

		if (Environment.MEDIA_MOUNTED.equals(estado)) {			// La tarjeta está activa y se puede escribir en ella
			return true;
		} else													// Sólo se puede leer el almacenamiento externo
			if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(estado)) {
				Toast.makeText(this, "El teléfono dispone de almacenamiento externo conectado " +
						"pero no se puede escribir en él.", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(this, "No está disponible el almacenamiento externo para hacer la exportación de datos.",
						Toast.LENGTH_SHORT).show();
			}
		
		return false;
	}

}
