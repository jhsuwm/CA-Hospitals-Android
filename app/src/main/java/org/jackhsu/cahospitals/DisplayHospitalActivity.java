package org.jackhsu.cahospitals;

/**
 * Created by Jack Hsu on 4/7/15.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Trace;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;


public class DisplayHospitalActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String searchText = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);

        /*
        // for testing only
        TextView textView = new TextView(this);
        textView.setText(searchText);
        textView.setTextSize(40);
        setContentView(textView);
        */

        setContentView(R.layout.activity_display_hospital);

        //queryDB();
        queryMyDB();

    }

    private void queryMyDB() {
        //final MyDB db = new MyDB(this);
        final DatabaseHelper db = new DatabaseHelper(this);

        Log.d("debug", "calling createDatabase() ...");
        db.createDataBase();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.hospital_view);
        final ScrollView sv = new ScrollView(this);
        rl.addView(sv);

        final TableLayout tl = new TableLayout(this);
        tl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        sv.addView(tl);

        //Trace.beginSection("SelectRows");
        // get all returned rows
        try {
            //db.open();

            List<Hospital> hospitals = db.getHospitals();
            Iterator<Hospital> iter = hospitals.iterator();

            while (iter.hasNext()) {
                Hospital hospital = iter.next();
                /*
                System.out.println("*** hospitalName: " + hospital.hospitalName);
                System.out.println("*** address: " + hospital.address);
                System.out.println("*** phone: " + hospital.phone);
                System.out.println("*** photoFile: " + hospital.photoFile);
                System.out.println("*** latitude: " + hospital.latitude);
                System.out.println("*** longitude: " + hospital.longitude);
                */

                final TableRow tr = new TableRow(getApplicationContext());
                final TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 0);
                tr.setLayoutParams(lp);
                tr.setBackgroundColor(Color.WHITE);

                // show hospital image on this table row
                final ImageView iv = new ImageView(getApplicationContext());
                //iv.setMaxWidth(400);
                //iv.setMaxHeight(350);
                iv.setAdjustViewBounds(true);
                //String uri = "drawable/alameda_health_system_highland_hospital";
                String uri = "drawable/" + hospital.photoFile.split(".jpg")[0];
                int resourceId = getResources().getIdentifier(uri, "drawable", getPackageName());

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), resourceId, options);

                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;
                String imageType = options.outMimeType;
                /*
                Log.d("imageWidth", Integer.toString(imageWidth));
                Log.d("imageHeight", Integer.toString(imageHeight));
                Log.d("imageType", imageType);
                Log.d("imageName", hospital.hospitalName);
                */


                int reqWidth = 400;
                int reqHeight = 350;
                /*
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                BitmapFactory.decodeResource(getResources(), resourceId, options);
                */
                //iv.setImageBitmap(decodeSampledBitmapFromResource(getResources(), resourceId, 45, 40));
                iv.setImageBitmap(decodeSampledBitmapFromResource(getResources(), resourceId, 30, 25));

                //iv.setImageResource(resourceId);
                //iv.setMaxWidth(reqWidth);
                //iv.setMaxHeight(reqHeight);
                tr.addView(iv);


                final LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                // show hospital name on this table row
                final TextView tv1 = new TextView(getApplicationContext());
                tv1.setWidth(600);
                tv1.setHeight(140);
                tv1.setPadding(20, 10, 10, 0);
                tv1.setMaxLines(3);
                tv1.setText(hospital.hospitalName);
                tv1.setBackgroundColor(Color.WHITE);
                tv1.setTextColor(Color.BLACK);
                tv1.setTextSize(14);
                ll.addView(tv1);

                // show hospital address on this table row
                final TextView tv2 = new TextView(getApplicationContext());
                tv2.setWidth(600);
                tv2.setHeight(100);
                tv2.setPadding(20, 10, 10, 0);
                tv2.setMaxLines(2);
                //tv2.setText(hospital.address);

                tv2.setText(hospital.photoFile.split(".jpg")[0]);
                tv2.setBackgroundColor(Color.WHITE);
                tv2.setTextColor(Color.BLACK);
                tv2.setTextSize(10);
                ll.addView(tv2);

                // show hospital address on this table row
                final TextView tv3 = new TextView(getApplicationContext());
                tv3.setWidth(600);
                tv3.setHeight(100);
                tv3.setPadding(20, 10, 10, 0);
                tv3.setMaxLines(1);
                //tv3.setText("(951) 486-4397");
                tv3.setText(hospital.phone);
                tv3.setBackgroundColor(Color.WHITE);
                tv3.setTextColor(Color.RED);
                tv3.setTextSize(10);
                ll.addView(tv3);

                tr.addView(ll);
                tl.addView(tr);
            }

        } catch(SQLException e) {
            System.out.println("sql error:" + e);
        }
        //Trace.endSection(); // end the trace "SelectRows"

        db.close();
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_hospital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
