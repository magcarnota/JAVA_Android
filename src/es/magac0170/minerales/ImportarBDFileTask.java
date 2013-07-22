package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class ImportarBDFileTask
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

public class ImportarBDFileTask extends AsyncTask<String, Void, Boolean> {
	private final ProgressDialog dialogo;
	private Context contexto;
	private MineralesDBAdapter dbHelper;
	private Main actividad;

	public ImportarBDFileTask (Context contexto, MineralesDBAdapter dbHelper){
		dialogo = new ProgressDialog(contexto);
		this.contexto = contexto;
		this.dbHelper = dbHelper;
		this.actividad = (Main) contexto;
	}

	protected void onPreExecute() {										// MENSAJE MOSTRADO ANTES DE EJECUTAR LA TAREA
		this.dialogo.setMessage("Importando la base de datos...");
		this.dialogo.show();
	}

	protected Boolean doInBackground(final String... args) {			// TAREA EN SEGUNDO PLANO
		File dir = android.os.Environment.getExternalStorageDirectory();
		File importFile = new File(dir, "BackupMinerales/copia_seguridad.csv");
		
		if (!importFile.exists()) {				// Si no existe el archivo de importación mostramos un mensaje de error
			if (this.dialogo.isShowing())
				this.dialogo.dismiss();
			Toast.makeText(contexto, "ERROR al importar datos.", Toast.LENGTH_SHORT).show();
			return false;
		}

		try {
			dbHelper.getBD().beginTransaction();
			dbHelper.borraTodosLosMinerales();
			BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(importFile)));
			String auxStr = "";
			
			fin.readLine();													// Leemos la primera línea para quitar la cabecera
			while(null != (auxStr = fin.readLine())) {
				String[] DatosRegistro = auxStr.split(";");
				
				if (DatosRegistro[6] == null)								// Si el último campo no tiene contenido es necesario inicializarlo
						DatosRegistro[6] = "";
				
				for (int i=0; i < DatosRegistro.length; i++)				// Para campos vacíos [que contienen #]
					if (DatosRegistro[i].equals("#")) DatosRegistro[i] = "";

				dbHelper.crearMineral(DatosRegistro[1], DatosRegistro[2], Integer.parseInt(DatosRegistro[3]),
									  DatosRegistro[5], Integer.parseInt(DatosRegistro[4]), DatosRegistro[6]);
			}
			fin.close();

			dbHelper.getBD().setTransactionSuccessful();
			return true;
		}
		catch(SQLException e) {
			if (this.dialogo.isShowing()) {
				this.dialogo.dismiss();
			}
			Toast.makeText(contexto, "ERROR de BD al importar datos.", Toast.LENGTH_SHORT).show();
			return false;
		}
		catch(IOException e) {
			if (this.dialogo.isShowing()) {
				this.dialogo.dismiss();
			}
			Toast.makeText(contexto, "ERROR de lectura a acceso ficheros.", Toast.LENGTH_SHORT).show();
			return false;
		}
		finally {
			dbHelper.getBD().endTransaction();
		}
	}

	protected void onPostExecute(final Boolean success) {	// AL ACABAR LA EJECUCIÓN DEL HILO
		if (this.dialogo.isShowing())
			this.dialogo.dismiss();
		if (success)
			Toast.makeText(contexto, "Importación finalizada correctamente.", Toast.LENGTH_SHORT).show();
		actividad.refrescarDatos();							// Recargamos los registros
	}

}
