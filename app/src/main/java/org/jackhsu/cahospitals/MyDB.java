package org.jackhsu.cahospitals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jack Hsu on 4/12/15.
 */
public class MyDB {
    private static final String DB_PATH = "/data/data/org.jackhsu.cahospitals/databases/";
    private static final String DB_NAME = "CA-H-Finder";
    private final Context context;
    private SQLiteDatabase db = null;

    public MyDB (Context context) {
        //super( context , DB_NAME , null , 1);
        this.context = context;
    }
    public boolean open() throws SQLException {
        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        return true;
    }

    public synchronized void close() {
        if (db != null) {
            db.close();
        }
    }

    public List<Hospital> getHospitals() {

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
            SQLiteDatabase db = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
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
