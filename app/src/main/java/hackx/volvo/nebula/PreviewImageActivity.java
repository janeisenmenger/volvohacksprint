package hackx.volvo.nebula;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class PreviewImageActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private View lineview;
    private ImageView imageView;
    int bitMapHeight,bitMapWidth;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        lineview = (View) findViewById(R.id.imageviewLine);
        imageView = (ImageView) findViewById(R.id.imageview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String imageLocation = getIntent().getStringExtra("pictureLocation");
        File imgFile = new File(imageLocation);
        if(imgFile.exists()){

            Bitmap myBitmap = rotate(BitmapFactory.decodeFile(imgFile.getAbsolutePath()),90);
            ImageView myImage = (ImageView) findViewById(R.id.imageview);
            myImage.setImageBitmap(myBitmap);

            imageView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            final Button downButton = findViewById(R.id.btn_down);
            downButton.setOnTouchListener(downButtonListiner);

            final Button upButton = findViewById(R.id.btn_up);
            upButton.setOnTouchListener(upButtonListiner);

            final Button measureButton = findViewById(R.id.btn_measure);
            measureButton.setOnClickListener(measureBtnListiner);
        };
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static RectF getImageBounds(ImageView imageView) {
        RectF bounds = new RectF();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            imageView.getImageMatrix().mapRect(bounds, new RectF(drawable.getBounds()));
        }
        return bounds;
    }

    View.OnTouchListener downButtonListiner = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            RectF insideImage = getImageBounds(imageView);
            if(insideImage.bottom >= lineview.getY() + 5)
                lineview.setY(lineview.getY() + 5);
            return true;
        }
    };

    View.OnTouchListener upButtonListiner = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            RectF insideImage = getImageBounds(imageView);
            if(insideImage.top <= lineview.getY() - 5)
                lineview.setY(lineview.getY() - 5);
            return true;
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //Remove it here unless you want to get this callback for EVERY
            //layout pass, which can get you into infinite loops if you ever
            //modify the layout from within this method.
            imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            //Now you can get the width and height from content

            lineview.setY(imageView.getMeasuredHeight() - (imageView.getMeasuredHeight()/2));
        }
    };


    View.OnClickListener measureBtnListiner = new View.OnClickListener() {
        public void onClick(View v) {
            RectF scaleDownImage = getImageBounds(imageView);
            int scaleDownImageHeight=(int) (scaleDownImage.bottom - scaleDownImage.top);
            int orignalHeight = imageView.getDrawable().getIntrinsicHeight();
            int lineHeight = (int) (lineview.getY() - scaleDownImage.top);

            int value = (lineHeight * orignalHeight) / scaleDownImageHeight;
        }
    };

}
