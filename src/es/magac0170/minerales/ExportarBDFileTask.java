package es.magac0170.minerales;

/**
 * Proyecto curso Android Aula Mentor
 * 
 * @class ExportarBDFileTask
 * @author Miguel Ángel García Carnota
 * @version 1.0
 * @date 16/05/2013
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ExportarBDFileTask extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialogo;
    private Context contexto;
    private MineralesDBAdapter dbHelper;
    private Cursor mCursor;

    public ExportarBDFileTask (Context contexto, MineralesDBAdapter dbHelper){
        dialogo = new ProgressDialog(contexto);
        this.contexto = contexto;
        this.dbHelper = dbHelper;
    }

    protected void onPreExecute() {								// ANTES DE EMPEZAR LA EXPORTACIÓN
        this.dialogo.setMessage("Exportando la base de datos...");
        mCursor = dbHelper.obtenerMineralesBackup();
        this.dialogo.show();
    }

    protected Boolean doInBackground(final String... args) {	// TAREA EN SEGUNDO PLANO

        File dir = android.os.Environment.getExternalStorageDirectory();
        File exportDir = new File(dir, "BackupMinerales");
        
        if(!exportDir.exists())		// Si no existe el directorio BackupMinerales lo creamos
            exportDir.mkdirs();

        String dirAlmacExt = exportDir.getAbsolutePath();
        
        try {
            File fichero = new File(dirAlmacExt, "copia_seguridad.csv");			// Abrimos el fichero en el raíz de la tarjeta SD
            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(fichero));

            int numcols = mCursor.getColumnCount();
            String auxStr = "";
            
            for(int idx = 0; idx < numcols; idx++) {			// Obtenemos los nombres de los campos
                auxStr += mCursor.getColumnName(idx) + ";";
            }
            
            fout.write(auxStr + "\n");							// Escribimos la primera línea del csv con los nombres de los campos
            
            mCursor.moveToFirst();
            while(mCursor.getPosition() < mCursor.getCount()) {	// Recorremos el cursor para escribir los datos
                auxStr = "";
                for(int idx = 0; idx < numcols; idx++) {
                    if (mCursor.getString(idx).isEmpty())		// Usamos # para campos vacíos
                    	auxStr += "#;";
                    else
                    	auxStr += mCursor.getString(idx)+ ";";
                }
                fout.write(auxStr + "\n");
                mCursor.moveToNext();
            }
            
            fout.close();
            
            return true;
        }
        catch(Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero en memoria externa");
            return false;
        }
    }

    protected void onPostExecute(final Boolean success) {		// AL FINALIZAR
        if (this.dialogo.isShowing())
            this.dialogo.dismiss();
        
        if (success)
            Toast.makeText(contexto, "Exportación finalizada correctamente.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "ERROR al exportar datos.", Toast.LENGTH_SHORT).show();
    }

}

