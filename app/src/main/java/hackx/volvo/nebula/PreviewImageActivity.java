package hackx.volvo.nebula;

import android.Manifest;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static hackx.volvo.nebula.Helper.Image.GetRotatedImage;
import static hackx.volvo.nebula.Helper.Image.getImageBounds;

public class PreviewImageActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private View lineview;
    private ImageView imageView;
    final int smallStepValue = 2;
    final int longStepValue = 5;
    int bitMapHeight,bitMapWidth;
    String imageLocation;
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
        imageLocation = getIntent().getBundleExtra("camBundle").getString("pictureLocation");
        File imgFile = new File(imageLocation);
        if(imgFile.exists()){
            ImageView myImage = (ImageView) findViewById(R.id.imageview);
            myImage.setImageBitmap(GetRotatedImage(imageLocation));

            imageView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            final Button downButton = findViewById(R.id.btn_down);
            downButton.setOnClickListener(downButtonListiner);
            downButton.setOnTouchListener(downButtonLongTouchListiner);

            final Button upButton = findViewById(R.id.btn_up);
            upButton.setOnClickListener(upButtonListiner);
            upButton.setOnTouchListener(upButtonLongTouchListiner);

            final Button measureButton = findViewById(R.id.btn_measure);
            measureButton.setOnClickListener(measureBtnListiner);
        }
        else
        {
            Toast.makeText(PreviewImageActivity.this, "Sorry!!!, No image found", Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener downButtonListiner = new View.OnClickListener() {
        public void onClick(View v) {
            restrictMotionToImageBottom(smallStepValue);
        }
    };

    View.OnTouchListener downButtonLongTouchListiner = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            restrictMotionToImageBottom(longStepValue);
            return true;
        }
    };

    View.OnClickListener upButtonListiner = new View.OnClickListener() {
        public void onClick(View v) {
            restrictMotionToImageTop(smallStepValue);
        }
    };

    View.OnTouchListener upButtonLongTouchListiner = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            restrictMotionToImageTop(longStepValue);
            return true;
        }
    };

    void restrictMotionToImageBottom(int stepValue){
        RectF insideImage = getImageBounds(imageView);
        if(insideImage.bottom >= lineview.getY() + stepValue)
            lineview.setY(lineview.getY() + stepValue);
    }
    void restrictMotionToImageTop(int stepValue){
        RectF insideImage = getImageBounds(imageView);
        if(insideImage.top <= lineview.getY() - stepValue)
            lineview.setY(lineview.getY() - stepValue);
    }

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

            int predictedY = (lineHeight * orignalHeight) / scaleDownImageHeight;

            Intent intent = new Intent(PreviewImageActivity.this, ResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("pictureLocation",imageLocation);
            bundle.putString("type", getIntent().getBundleExtra("camBundle").getString("type"));
            bundle.putInt("predictedY", predictedY);
            intent.putExtra("camBundle",bundle);
            startActivity(intent);
        }
    };

}
