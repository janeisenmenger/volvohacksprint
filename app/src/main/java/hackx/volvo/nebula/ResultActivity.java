package hackx.volvo.nebula;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static hackx.volvo.nebula.Helper.Image.GetRotatedImage;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String imageLocation = getIntent().getBundleExtra("camBundle").getString("pictureLocation");
        File imgFile = new File(imageLocation);
        if(imgFile.exists()){
            ImageView myImage = (ImageView) findViewById(R.id.imageview);
            myImage.setImageBitmap(GetRotatedImage(imageLocation));

            TextView textView = findViewById(R.id.measure_text_view);
            double measureValue = getIntent().getBundleExtra("camBundle").getDouble("result");
            StringBuilder sb = new StringBuilder();

            sb.append(" Estimated Height: " + String.format("%.2f", measureValue) + " mm");
            sb.append("\n Percentage life Remaining: "+ String.format("%.2f", getestimatedLife(measureValue))+" %");
            String textViewString = sb.toString();
            textView.setText(textViewString);

            final Button downButton = findViewById(R.id.btn_retry);
            downButton.setOnClickListener(retryButtonListiner);
        }
        else
        {
            Toast.makeText(ResultActivity.this, "Sorry!!!, No image found", Toast.LENGTH_LONG).show();
        }
    }


    View.OnClickListener retryButtonListiner  = new View.OnClickListener() {
        public void onClick(View v) {
            // Code here executes on main thread after user presses button
            Intent intent = new Intent(ResultActivity.this, cam_activity.class);
            intent.putExtra("type",getIntent().getBundleExtra("camBundle").getString("type"));
            startActivity(intent);
            finish();
        }
    };

    double getestimatedLife(double measureValue){
        switch (getIntent().getBundleExtra("camBundle").getString("type")){
            case "TripleGrouserShoe":
                return (measureValue - 16) * 10;
            case "DoubleGrouserShoe":
                return (measureValue - 15) * 5;
            default:
                return 0;
        }

    }
}
