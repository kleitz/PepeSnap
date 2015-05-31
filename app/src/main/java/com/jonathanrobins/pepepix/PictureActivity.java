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
    private Button backButton;
    private Button pepeButton;
    private Button decreaseButton;
    private Button increaseButton;
    private Button deleteButton;
    private Button doneButton;
    private ImageView picture;
    private ImageView saveIcon;
    private boolean clicked = false;
    private int width;
    private int height;
    private ScrollView scrollView;
    private RelativeLayout relativeLayout;
    private ViewGroup mainRelativeLayout;

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
        //intializes buttons and sets colors
        backButton = (Button) findViewById(R.id.backButton);
        pepeButton = (Button) findViewById(R.id.pepeButton);
        decreaseButton = (Button) findViewById(R.id.decreaseButton);
        increaseButton = (Button) findViewById(R.id.increaseButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        backButton.setTextColor(Color.parseColor("white"));
        increaseButton.setTextColor(Color.parseColor("white"));
        decreaseButton.setTextColor(Color.parseColor("white"));
        deleteButton.setTextColor(Color.parseColor("red"));
        //dank pepes and on-click setting
        int[] pepes = {R.id.pic0, R.id.pic1, R.id.pic2, R.id.pic3};
        for (int i = 0; i < pepes.length; i++) {
            ImageView pepe = (ImageView) findViewById(pepes[i]);
            pepe.setTag(i);
            pepe.setOnClickListener(pictureMovement);
        }
        //receives picture and sets it to imageview
        Intent intent = getIntent();
        picture = (ImageView) findViewById(R.id.picture);
        Bitmap bitmap = GlobalClass.img;
        picture.setImageBitmap(bitmap);

        //various views and layouts
        scrollView = (ScrollView) findViewById(R.id.pictureScrollView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mainRelativeLayout = (ViewGroup) findViewById(R.id.mainRelativeLayout);
        saveIcon = (ImageView) findViewById(R.id.save);

        //width and height of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        //initializes on-click methods for various buttons
        miscButtonLogic();
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

    public void miscButtonLogic() {
        //back button listener
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

        //pepes button listener
        pepeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //not clicked yet
                if (clicked == false) {
                    deleteButton.setAlpha(0.0f);
                    backButton.setAlpha(0.0f);
                    increaseButton.setAlpha(0.0f);
                    decreaseButton.setAlpha(0.0f);
                    doneButton.setAlpha(0.0f);
                    saveIcon.setVisibility(View.INVISIBLE);
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
                    pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
                    clicked = false;
                    //moves scrollview out of view
                    final Animation a = new Animation() {
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
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            deleteButton.setAlpha(100.0f);
                            backButton.setAlpha(100.0f);
                            increaseButton.setAlpha(100.0f);
                            decreaseButton.setAlpha(100.0f);
                            doneButton.setAlpha(100.0f);
                            saveIcon.setVisibility(View.VISIBLE);
                        }
                    });
                    scrollView.startAnimation(a);
                }
            }
        });

        //main background picture listener
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == true) {
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
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            deleteButton.setAlpha(100.0f);
                            backButton.setAlpha(100.0f);
                            increaseButton.setAlpha(100.0f);
                            decreaseButton.setAlpha(100.0f);
                            doneButton.setAlpha(100.0f);
                            saveIcon.setVisibility(View.VISIBLE);
                        }
                    });
                    scrollView.startAnimation(a);
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SAVING!");
            }
        });
    }

    public View.OnClickListener pictureMovement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get tag number and resID to find correct picture for display
            System.out.println("Tag is: " + (int) v.getTag());
            String stringVar = "pic" + v.getTag();
            int resID = getResources().getIdentifier(stringVar, "drawable", PictureActivity.this.getPackageName());
            //create image based on resID and set to main RelativeLayout
            final ImageView clickedPepe = new ImageView(PictureActivity.this);
            clickedPepe.setImageResource(resID);
            //sets image to main view and sets margins for where to appear
            mainRelativeLayout.addView(clickedPepe);
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clickedPepe.getLayoutParams();
            layoutParams.topMargin = (int) (height - height / 1.5);
            layoutParams.leftMargin = (int) (width - width / 1.5);
            clickedPepe.setLayoutParams(layoutParams);
            //sets onTouch listeners for dragging/zooming
            clickedPepe.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //sets methods for zoom buttons when a certain pepe is touched
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedPepe.setVisibility(View.GONE);
                                }
                            });
                            increaseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layoutParams.width = layoutParams.width + 50;
                                    clickedPepe.setLayoutParams(layoutParams);
                                }
                            });
                            decreaseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layoutParams.width = layoutParams.width - 50;
                                    clickedPepe.setLayoutParams(layoutParams);
                                }
                            });
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //for dragging and moving pepes
                            if (event.getPointerCount() == 1) {
                                int x = (int) event.getRawX();
                                int y = (int) event.getRawY();
                                layoutParams.leftMargin = x - 400;
                                layoutParams.topMargin = y - 400;
                                //MAYBE SET params.width FOR IMAGE FIRST THEN PERFORM ACTIONS ON ITS SIZE SINCE IT STARTS AT 0 FOR SOME REASON
                                //layoutParams.width =  layoutParams.width + 20;
                                clickedPepe.setLayoutParams(layoutParams);
                                break;
                            }
                            if (event.getPointerCount() == 2) {
                                float x1 = event.getX(0);
                                float y1 = event.getY(0);
                                float x2 = event.getX(1);
                                float y2 = event.getY(1);
                                float distance = FloatMath.sqrt((x1 * x2) + (y1 * y2));
                                break;
                            }
                        default:
                            break;
                    }
                    return true;
                }
            });

            //setContentView(mainRelativeLayout);
            //bringing misc views in front of pictures because pictures are thrown over them
            scrollView.bringToFront();
            backButton.bringToFront();
            pepeButton.bringToFront();
            //move other picture_activity stuff away
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
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    deleteButton.setAlpha(100.0f);
                    backButton.setAlpha(100.0f);
                    increaseButton.setAlpha(100.0f);
                    decreaseButton.setAlpha(100.0f);
                    doneButton.setAlpha(100.0f);
                    saveIcon.setVisibility(View.VISIBLE);
                }
            });
            scrollView.startAnimation(a);
        }
    };
}
