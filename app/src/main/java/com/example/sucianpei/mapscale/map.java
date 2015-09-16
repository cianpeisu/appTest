package com.example.sucianpei.mapscale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
//size, density, and font scaling ...etc
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;


public class map extends Activity {
    private ImageView imageView;
    private DisplayMetrics dm;
    private Bitmap bitmap;

    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //to initialize DisplayMetrics dm
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imageView = (ImageView)findViewById(R.id.image_view);
        zoomInButton = (ImageButton)findViewById(R.id.zoomInButton);
        zoomOutButton = (ImageButton)findViewById(R.id.zoomOutButton);
        //to convert graph into stream
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.graph);
        imageView.setImageBitmap(bitmap);//sets a Bitmap as the content of this ImageView.
        new ImageViewHelper(dm,imageView,bitmap,zoomInButton,zoomOutButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
