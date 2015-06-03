package com.jonathanrobins.pepepix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore.Audio.Media;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



public class MainActivity extends ActionBarActivity {

    private Button cameraButton;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private int cameraID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hides various bars
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        cameraButton = (Button) findViewById(R.id.cameraButton);

        cameraButton.setBackgroundResource(R.drawable.camera_animation);
        AnimationDrawable animation = (AnimationDrawable) cameraButton.getBackground();
        animation.start();

        cameraButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        cameraButton.setBackgroundResource(R.drawable.camerabutton_pressed);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        cameraButton.setBackgroundResource(R.drawable.camerabutton_pressed);
                        cameraButton.setBackgroundResource(R.drawable.camera_animation);
                        AnimationDrawable animation = (AnimationDrawable) cameraButton.getBackground();
                        animation.start();
                        buttonLogic();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up backButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonLogic() {
        //create intent and send picture through intent as extra, created as temp file on device
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(getApplicationContext())));
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        MainActivity.this.overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    final File file = getTempFile(this);
                    try {
                        //retrieve bitmap for picture taken
                        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                        Bitmap rotatedBitmap = null;
                        //get exif data and orientation to determine auto-rotation for picture
                        ExifInterface exif = new ExifInterface("" + file);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        //rotate picture certain # of degrees depending on orientation then sets new matrix
                        Matrix matrix = new Matrix();
                        switch (orientation) {
                            case 1:
                                matrix.postRotate(270);
                                rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                            case 3:
                                matrix.postRotate(180);
                                rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                                break;
                            case 6:
                                matrix.postRotate(90);
                                rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                                break;
                            case 8:
                                matrix.postRotate(270);
                                rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                                break;
                        }
                        //check for cameraID of front facing camera and flips accordingly
                        /*Camera.CameraInfo info = new Camera.CameraInfo();
                        android.hardware.Camera.getCameraInfo(cameraID, info);
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        {
                            System.out.println("WHY");
                            float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);
                            matrix.postConcat(matrixMirrorY);
                            rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                        }*/

                        Intent i = new Intent(getBaseContext(), PictureActivity.class);
                        //assigns to global bitmap variable then goes to intent
                        GlobalClass.img = rotatedBitmap;
                        startActivity(i);
                        finish();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private File getTempFile(Context context) {
        //it will return /sdcard/image.tmp
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }
}
