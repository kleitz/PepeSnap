package com.jonathanrobins.pepepix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class PictureActivity extends ActionBarActivity {
    Button backButton;
    Button pepeButton;
    Button undoButton;
    ImageView picture;
    boolean clicked = false;
    int width;
    int height;
    ScrollView scrollView;
    RelativeLayout relativeLayout;
    ViewGroup mainRelativeLayout;
    int windowwidth;
    int windowheight;
    ImageView clickedPepe = null;
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
        setContentView(R.layout.activity_picture);
        //intializes back backButton and then sets color of back backButton
        backButton = (Button) findViewById(R.id.backButton);
        pepeButton = (Button) findViewById(R.id.pepeButton);
        undoButton = (Button) findViewById(R.id.undoButton);
        backButton.setTextColor(Color.parseColor("white"));
        undoButton.setTextColor(Color.parseColor("white"));
        //dank pepes and on-click setting
        int[] pepes = {R.id.pic0, R.id.pic1, R.id.pic2, R.id.pic3};
        for (int i = 0; i < pepes.length; i++) {
            ImageView pepe = (ImageView) findViewById(pepes[i]);
            pepe.setTag(i);
            pepe.setOnClickListener(pictureEditing);
        }
        //receives picture and sets it to imageview
        Intent intent = getIntent();
        picture = (ImageView) findViewById(R.id.picture);
        Bitmap bitmap = GlobalClass.img;
        picture.setImageBitmap(bitmap);

        scrollView = (ScrollView) findViewById(R.id.pictureScrollView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mainRelativeLayout = (ViewGroup) findViewById(R.id.mainRelativeLayout);

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

    //back button override
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
            this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void buttonLogic() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setBackgroundResource(R.drawable.rounded_button_pressed);
                finish();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                PictureActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        pepeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //not clicked yet
                if (clicked == false) {
                    backButton.setAlpha(0.0f);
                    undoButton.setAlpha(0.0f);
                    pepeButton.setBackgroundResource(R.drawable.sadpepeicon);
                    clicked = true;
                    //moves scrollview in view
                   /*
                    ScrollView scrollview = (ScrollView) findViewById(R.id.pictureScrollView);
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollview.getLayoutParams();
                    layouts.leftMargin = 0;
                    layouts.rightMargin = width/2 - ((width/2)/2);
                    scrollview.setLayoutParams(layouts);*/
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                            layouts.topMargin = (int) (-125 * interpolatedTime);
                            scrollView.setLayoutParams(layouts);
                        }
                    };
                    a.setDuration(700); // in ms
                    scrollView.startAnimation(a);
                }
                //clicked already
                else {
                    backButton.setAlpha(100.0f);
                    undoButton.setAlpha(100.0f);
                    pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
                    clicked = false;
                    //moves scrollview out of view
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                            //layouts.leftMargin = (int)(width * 2 * interpolatedTime);
                            //layouts.rightMargin = (int)(-width * 2 * (interpolatedTime));
                            layouts.topMargin = (int) (height * 2 * interpolatedTime);
                            scrollView.setLayoutParams(layouts);
                        }
                    };
                    a.setDuration(700); // in ms
                    scrollView.startAnimation(a);
                }
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == true) {
                    backButton.setAlpha(100.0f);
                    undoButton.setAlpha(100.0f);
                    pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
                    clicked = false;
                    //moves scrollview out of view
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                            //layouts.leftMargin = (int)(width * 2 * interpolatedTime);
                            // layouts.rightMargin = (int)(-width * 2 * (interpolatedTime));
                            layouts.topMargin = (int) (height * 2 * interpolatedTime);
                            scrollView.setLayoutParams(layouts);
                        }
                    };
                    a.setDuration(700); // in ms
                    scrollView.startAnimation(a);
                }
            }
        });
    }

    public View.OnClickListener pictureEditing = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get tag number and resID to find correct picture for display
            System.out.println("Tag is: " + (int) v.getTag());
            String stringVar = "pic" + v.getTag();
            int resID = getResources().getIdentifier(stringVar, "drawable", PictureActivity.this.getPackageName());
            //create image based on resID and set to main RelativeLayout after layout  customization
            clickedPepe = new ImageView(PictureActivity.this);
            clickedPepe.setImageResource(resID);
            //sets image to main view
            mainRelativeLayout.addView(clickedPepe);
           /// layoutParams.leftMargin = width/2 - (width/2/2);
           /// layoutParams.topMargin = height/2 - (width/2/2);
           /// clickedPepe.setLayoutParams(layoutParams);
            //sets onTouch listener for dragging/zooming
            clickedPepe.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clickedPepe.getLayoutParams();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                           break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(event.getPointerCount() == 1) {
                                int x = (int) event.getRawX();
                                int y = (int) event.getRawY();
                                layoutParams.leftMargin = x - 400;
                                layoutParams.topMargin = y - 400;
                                clickedPepe.setLayoutParams(layoutParams);
                            }
                            if(event.getPointerCount() == 2){
                                float x = event.getX(0) - event.getX(1);
                                float y = event.getY(0) - event.getY(1);
                                float z = FloatMath.sqrt(x * x + y * y);
                                return true;
                            }
                                break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            /*TouchImageView clickedPepe = new TouchImageView(PictureActivity.this);
            clickedPepe.setMinZoom(0.25f);
            clickedPepe.setMaxZoom(4.0f);
            clickedPepe.setZoom(.85f);*/

            //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            //layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            //clickedPepe.setLayoutParams(layoutParams);

            //setContentView(mainRelativeLayout);
            //bringing misc views in front of pictures because pictures are thrown over them
            scrollView.bringToFront();
            backButton.bringToFront();
            pepeButton.bringToFront();
            undoButton.bringToFront();
            //move other picture_activity stuff away
            backButton.setAlpha(100.0f);
            undoButton.setAlpha(100.0f);
            pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
            clicked = false;
            //moves scrollview out of view
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                    //layouts.leftMargin = (int)(width * 2 * interpolatedTime);
                    // layouts.rightMargin = (int)(-width * 2 * (interpolatedTime));
                    layouts.topMargin = (int) (height * 2 * interpolatedTime);
                    scrollView.setLayoutParams(layouts);
                }
            };
            a.setDuration(700); // in ms
            scrollView.startAnimation(a);
        }
    };

}
