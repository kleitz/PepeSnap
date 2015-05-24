package com.jonathanrobins.pepepix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


public class PictureActivity extends ActionBarActivity {

    Button backButton;
    Button pepeButton;
    ImageView picture;
    boolean clicked = false;
    int width;
    int height;
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
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_picture);
        //intializes back backButton and then sets color of back backButton
        backButton = (Button) findViewById(R.id.backButton);
        pepeButton = (Button) findViewById(R.id.pepeButton);
        backButton.setTextColor(Color.parseColor("white"));
        //receives picture and sets it to imageview
        Intent intent = getIntent();
        picture = (ImageView) findViewById(R.id.picture);
        Bitmap bitmap = GlobalClass.img;
        picture.setImageBitmap(bitmap);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        //initializes on-click methods for various buttons
        buttonLogic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture, menu);
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        pepeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //not clicked yet
                if (clicked == false) {
                    backButton.setAlpha(0.0f);
                    clicked = true;
                    //moves scrollview in view
                    ScrollView scrollview = (ScrollView) findViewById(R.id.pictureScrollView);
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollview.getLayoutParams();
                    layouts.leftMargin = 0;
                    layouts.rightMargin = width/2 - ((width/2)/2);
                    scrollview.setLayoutParams(layouts);
                }
                //clicked already
                else{
                    backButton.setAlpha(100.0f);
                    clicked = false;
                    //moves scrollview out of view
                    ScrollView scrollview = (ScrollView) findViewById(R.id.pictureScrollView);
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollview.getLayoutParams();
                    layouts.leftMargin = -width;
                    layouts.rightMargin = width;
                    scrollview.setLayoutParams(layouts);
                }
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == true){
                    backButton.setAlpha(100.0f);
                    clicked = false;
                    //moves scrollview out of view
                    ScrollView scrollview = (ScrollView) findViewById(R.id.pictureScrollView);
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollview.getLayoutParams();
                    layouts.leftMargin = -width;
                    layouts.rightMargin = width;
                    scrollview.setLayoutParams(layouts);
                }
            }
        });
    }
}
