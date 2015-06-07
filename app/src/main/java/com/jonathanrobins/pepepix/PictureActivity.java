package com.jonathanrobins.pepepix;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.Settings;
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
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends ActionBarActivity {
    private Button backButton;
    private Button pepeButton;
    private Button decreaseButton;
    private Button increaseButton;
    private Button deleteButton;
    private Button flipButton;
    private Button doneButton;
    private ImageView picture;
    private ImageView flipIcon;
    private ImageView saveIcon;
    private ImageView tapIndicator;
    private boolean clicked = false;
    private int width;
    private int height;
    private ScrollView scrollView;
    private RelativeLayout relativeLayout;
    private ViewGroup mainRelativeLayout;
    ImageView lastClickedPepe = null;

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
        flipButton = (Button) findViewById(R.id.flipButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        backButton.setTextColor(Color.parseColor("white"));
        increaseButton.setTextColor(Color.parseColor("white"));
        decreaseButton.setTextColor(Color.parseColor("white"));
        deleteButton.setTextColor(Color.parseColor("red"));
        //set visibility of buttons
        deleteButton.setVisibility(View.INVISIBLE);
        increaseButton.setVisibility(View.INVISIBLE);
        decreaseButton.setVisibility(View.INVISIBLE);
        flipButton.setVisibility(View.INVISIBLE);
        //dank pepes and on-click setting
        int[] pepes = {R.id.pic0, R.id.pic1, R.id.pic2, R.id.pic3, R.id.pic4, R.id.pic5, R.id.pic6, R.id.pic7, R.id.pic8, R.id.pic9, R.id.pic10,
                       R.id.pic11, R.id.pic12, R.id.pic13, R.id.pic14, R.id.pic15, R.id.pic16, R.id.pic17, R.id.pic18, R.id.pic19};
        for (int i = 0; i < pepes.length; i++) {
            ImageView pepe = (ImageView) findViewById(pepes[i]);
            pepe.setTag(i);
            pepe.setOnClickListener(pictureMovement);
        }
        //receives picture and sets it to imageview
        Intent intent = getIntent();
        picture = (ImageView) findViewById(R.id.picture);
        Bitmap bitmap = GlobalClass.bitmap;
        picture.setImageBitmap(bitmap);
        picture.setAdjustViewBounds(true);

        //various views and layouts
        scrollView = (ScrollView) findViewById(R.id.pictureScrollView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mainRelativeLayout = (ViewGroup) findViewById(R.id.mainRelativeLayout);
        flipIcon = (ImageView) findViewById(R.id.flip);
        saveIcon = (ImageView) findViewById(R.id.save);
        tapIndicator = (ImageView) findViewById(R.id.tapIndicator);
        flipIcon.setVisibility(View.INVISIBLE);
        tapIndicator.setVisibility(View.INVISIBLE);

        //width and height of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        //initializes on-click methods for various buttons
        miscButtonLogic();

        //home button detector
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();
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
            GlobalClass.didFinishEditing = true;
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
                GlobalClass.didFinishEditing = true;
                startActivity(i);
                PictureActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                GlobalClass.bitmap = null;
            }
        });

        //pepes button listener
        pepeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //not clicked yet
                if (clicked == false) {
                    if (lastClickedPepe != null) {
                        lastClickedPepe.setBackgroundResource(R.drawable.no_border);
                    }
                    deleteButton.setVisibility(View.INVISIBLE);
                    backButton.setVisibility(View.INVISIBLE);
                    increaseButton.setVisibility(View.INVISIBLE);
                    decreaseButton.setVisibility(View.INVISIBLE);
                    flipButton.setVisibility(View.INVISIBLE);
                    doneButton.setVisibility(View.INVISIBLE);
                    flipIcon.setVisibility(View.INVISIBLE);
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
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tapIndicator.setVisibility(View.VISIBLE);
                            tapIndicator.bringToFront();
                        }
                    });
                    scrollView.startAnimation(a);
                }
                //clicked already
                else {
                    if (lastClickedPepe != null) {
                        lastClickedPepe.setBackgroundResource(R.drawable.no_border);
                    }
                    tapIndicator.setVisibility(View.INVISIBLE);
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
                            deleteButton.setVisibility(View.INVISIBLE);
                            backButton.setVisibility(View.VISIBLE);
                            increaseButton.setVisibility(View.INVISIBLE);
                            decreaseButton.setVisibility(View.INVISIBLE);
                            flipButton.setVisibility(View.INVISIBLE);
                            doneButton.setVisibility(View.VISIBLE);
                            flipIcon.setVisibility(View.INVISIBLE);
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
                if (lastClickedPepe != null) {
                    lastClickedPepe.setBackgroundResource(R.drawable.no_border);
                }
                deleteButton.setVisibility(View.INVISIBLE);
                increaseButton.setVisibility(View.INVISIBLE);
                decreaseButton.setVisibility(View.INVISIBLE);
                flipButton.setVisibility(View.INVISIBLE);
                flipIcon.setVisibility(View.INVISIBLE);
                if (clicked == true) {
                    pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
                    tapIndicator.setVisibility(View.INVISIBLE);
                    clicked = false;
                    //moves scrollview out of view
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
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
                            deleteButton.setVisibility(View.INVISIBLE);
                            backButton.setVisibility(View.VISIBLE);
                            increaseButton.setVisibility(View.INVISIBLE);
                            decreaseButton.setVisibility(View.INVISIBLE);
                            flipButton.setVisibility(View.INVISIBLE);
                            doneButton.setVisibility(View.VISIBLE);
                            flipIcon.setVisibility(View.INVISIBLE);
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
                backButton.setVisibility(View.INVISIBLE);
                pepeButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                decreaseButton.setVisibility(View.INVISIBLE);
                increaseButton.setVisibility(View.INVISIBLE);
                flipButton.setVisibility(View.INVISIBLE);
                doneButton.setVisibility(View.INVISIBLE);
                flipIcon.setVisibility(View.INVISIBLE);
                saveIcon.setVisibility(View.INVISIBLE);
                if (lastClickedPepe != null) {
                    lastClickedPepe.setBackgroundResource(R.drawable.no_border);
                }
                //calls dialog window and save logic
                openDialog();
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
            layoutParams.width = 600;
            clickedPepe.setLayoutParams(layoutParams);
            clickedPepe.setMaxWidth(3000);
            clickedPepe.setAdjustViewBounds(true);
            //sets onTouch listeners for dragging/zooming
            clickedPepe.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //sets methods for zoom buttons when a certain pepe is touched
                            if (lastClickedPepe != null) {
                                lastClickedPepe.setBackgroundResource(R.drawable.no_border);
                            }
                            lastClickedPepe = clickedPepe;
                            clickedPepe.setBackgroundResource(R.drawable.border);
                            deleteButton.setVisibility(View.VISIBLE);
                            increaseButton.setVisibility(View.VISIBLE);
                            decreaseButton.setVisibility(View.VISIBLE);
                            flipButton.setVisibility(View.VISIBLE);
                            flipIcon.setVisibility(View.VISIBLE);
                            //deletes pepe
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedPepe.setVisibility(View.GONE);
                                    deleteButton.setVisibility(View.INVISIBLE);
                                    increaseButton.setVisibility(View.INVISIBLE);
                                    flipButton.setVisibility(View.INVISIBLE);
                                    decreaseButton.setVisibility(View.INVISIBLE);
                                    flipIcon.setVisibility(View.INVISIBLE);
                                }
                            });
                            //zooms in pepe
                            increaseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(layoutParams.width);
                                    if (layoutParams.width < width + 1000) {
                                        layoutParams.width = layoutParams.width + 50;
                                        clickedPepe.setLayoutParams(layoutParams);
                                    }
                                }
                            });
                            //zooms out pepe
                            decreaseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(layoutParams.width);
                                    if (layoutParams.width > 100) {
                                        layoutParams.width = layoutParams.width - 50;
                                        clickedPepe.setLayoutParams(layoutParams);
                                    }
                                }
                            });
                            //flips pepe
                            flipButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bitmap bitmap = ((BitmapDrawable) clickedPepe.getDrawable()).getBitmap();
                                    Matrix matrix = new Matrix();
                                    float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                                    Matrix matrixMirrorY = new Matrix();
                                    matrixMirrorY.setValues(mirrorY);
                                    matrix.postConcat(matrixMirrorY);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    clickedPepe.setImageBitmap(bitmap);
                                }
                            });
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //for dragging and moving pepes
                            if (event.getPointerCount() == 1) {
                                int x = (int) event.getRawX();
                                int y = (int) event.getRawY();
                                layoutParams.leftMargin = x - 400;
                                layoutParams.rightMargin = -400;
                                layoutParams.topMargin = y - 400;
                                layoutParams.bottomMargin = -400;
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
            deleteButton.bringToFront();
            increaseButton.bringToFront();
            decreaseButton.bringToFront();
            flipButton.bringToFront();
            doneButton.bringToFront();
            pepeButton.bringToFront();
            //move other picture_activity stuff away
            pepeButton.setBackgroundResource(R.drawable.pepepicturesicon);
            tapIndicator.setVisibility(View.INVISIBLE);
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
                    backButton.setVisibility(View.VISIBLE);
                    doneButton.setVisibility(View.VISIBLE);
                    saveIcon.setVisibility(View.VISIBLE);}
            });
            scrollView.startAnimation(a);
        }
    };

    public void openDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("All Finished?")
                .setMessage("Would you like to save this picture?")
                        //yes
                .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //screenshots current screen with added pepes
                        View v = getWindow().getDecorView().getRootView();
                        v.setDrawingCacheEnabled(true);
                        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                        v.setDrawingCacheEnabled(false);

                        try {

                            String path = Environment.getExternalStorageDirectory()
                                    .toString();
                            File newFolder = new File(path + "/Pepes");
                            newFolder.mkdirs();
                            OutputStream fOut = null;

                            //get timestamp of picture taken
                            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                            String timestamp = s.format(new Date());

                            //save image
                            File file = new File(path, "/Pepes/" + timestamp + ".png");
                            fOut = new FileOutputStream(file);
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();

                            //refresh galleries and photo apps
                            MediaScannerConnection.scanFile(PictureActivity.this, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);

                            //final logic for saving picture
                            Toast.makeText(getApplicationContext(),
                                    "Your Pepe has been saved!", Toast.LENGTH_LONG)
                                    .show();
                            finish();
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            GlobalClass.didFinishEditing = true;
                            startActivity(i);
                            PictureActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                        } catch (Exception e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Problem to Save the File", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                        //no
                .setNegativeButton("Not yet.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        backButton.setVisibility(View.VISIBLE);
                        pepeButton.setVisibility(View.VISIBLE);
                        doneButton.setVisibility(View.VISIBLE);
                        saveIcon.setVisibility(View.VISIBLE);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
