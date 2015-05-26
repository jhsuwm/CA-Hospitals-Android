package org.jackhsu.cahospitals;

/**
 * Created by Jack Hsu on 5/17/15.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_PATH = "/data/data/org.jackhsu.cahospitals/databases";
    private static final String DB_NAME = "CA-H-Finder";
    private final Context context;
    private SQLiteDatabase myDataBase;

    // Constructor
    // Takes and keeps a reference of the passed context in order to access to the application assets and resources.
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.context = context;

    }

    // Creates a empty database on the system and rewrites it with your own database.
    public void createDataBase() {

        boolean dbExist = false;
        try {

            dbExist = checkDataBase();

        } catch (SQLiteException e) {

            Log.d("debug", "checkDatabase() return exception");
            //e.printStackTrace();
            //throw new Error("database dose not exist");

        }

        Log.d("debug", Boolean.toString(dbExist));
        if(dbExist){
            //do nothing - database already exist
        } else {

            try {

                Log.d("debug", "copying database ...");
                copyDataBase();

            } catch (IOException e) {

                Log.d("debug", "copyDatabase() return exception");
                e.printStackTrace();
                throw new Error("Error copying database");

            }
            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
        }
    }

    // Check if the database already exist to avoid re-copying the file each time you open the application.
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH +"/"+ DB_NAME;

            Log.d("debug", "calling SQLiteDatabase.openDatabase() ...");
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e) {

            Log.d("debug", "No database file" + DB_NAME);
            //database does't exist yet.
            //throw new Error("database does't exist yet.");

        }

        Log.d("debug", "checking checkDB ...");
        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    // Copies your database from your local assets-folder to the just created empty database in the
    // system folder, from where it can be accessed and handled.
    // This is done by transferring bytestream.
    private void copyDataBase() throws IOException{

        //copyDataBase();
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH +"/"+ DB_NAME;
        File databaseFile = new File( DB_PATH);
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()){
            //outFileName = context.getDatabasePath(DB_NAME).getPath();
            databaseFile.mkdirs();
        }

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Hospital> getHospitals() throws SQLException {

        List<Hospital> hospitals = null;

        try {

            String cityOrZip = "los angeles";   // for testing only
            String query = "SELECT h.hospital_name, h.address, h.phone, h.photo_file, h.latitude, h.longitude " +
                    "FROM hospital h, zip z, city c " +
                    "WHERE h.zip_id=z.zip_id " +
                    "AND z.city_id=c.city_id " +
                    //"AND (LOWER(c.city_name)='" + cityOrZip + "' OR z.zip_code='" + cityOrZip + "') " +
                    //"AND h.photo_file LIKE 'a%'" +
                    //"AND h.hospital_name='Adventist Medical Center - Hanford'" +
                    //"AND h.hospital_name IN ('Adventist Medical Center - Hanford','Adventist Medical Center - Reedley','Adventist Medical Center - Selma','Ahmc Anaheim Regional Medical Center','Alameda Health System - Highland Hospital','Alameda Hospital')" +
                    //"AND h.hospital_name IN ('Adventist Medical Center - Hanford','Adventist Medical Center - Reedley','Adventist Medical Center - Selma','Ahmc Anaheim Regional Medical Center','Alameda Health System - Highland Hospital')" +
                    "AND h.hospital_name LIKE 'A%'" +
                    "ORDER BY 1";
            //"LIMIT 1";
            SQLiteDatabase db = SQLiteDatabase.openDatabase( DB_PATH + '/' + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            hospitals = new LinkedList<Hospital>();

            if (cursor.moveToFirst()) {
                do {

                    Hospital hospital = new Hospital();
                    hospital.hospitalName = cursor.getString(0);
                    hospital.address = cursor.getString(1);
                    hospital.phone = cursor.getString(2);
                    hospital.photoFile = cursor.getString(3);
                    hospital.latitude = Double.parseDouble(cursor.getString(4));
                    hospital.longitude = Double.parseDouble(cursor.getString(5));

                    hospitals.add(hospital);

                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            // sql error
        }

        return hospitals;
    }
}
