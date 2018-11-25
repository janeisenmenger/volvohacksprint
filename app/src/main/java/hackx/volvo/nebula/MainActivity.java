package hackx.volvo.nebula;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public class MainActivity extends AppCompatActivity {

    enum SparesParts{
        TrackLink, TripleGrouserShoe, DoubleGrouserShoe, BottomRoller, TopRoller, Idler, Sprocket;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public boolean next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get the spinner from the xml.
        final CheckBox box1 = findViewById(R.id.cracks);
        final CheckBox box2 = findViewById(R.id.flat_spots);
        final CheckBox box3 = findViewById(R.id.box3);
        final ImageView example_imgView = findViewById(R.id.example_img);
        final Button btn_takePicture = findViewById(R.id.takePicture);
        final Spinner dropdown = findViewById(R.id.spinner1);
        final Button btn_save  = findViewById(R.id.btn_save);
        final Button btn_example = findViewById(R.id.btn_flat_example);

        example_imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (next)
                {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.good);
                    example_imgView.setImageBitmap(bm);
                    next = false;
                }
                else {
                    example_imgView.setVisibility(View.INVISIBLE);
                    box1.setVisibility(View.VISIBLE);
                    box2.setVisibility(View.VISIBLE);
                    box3.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_example.setVisibility(View.VISIBLE);
                    dropdown.setVisibility(View.VISIBLE);
                }
            }
        });
        //create a list of items for the spinner.
        String[] items = new String[]{
                SparesParts.TrackLink.toString(),
                SparesParts.TripleGrouserShoe.toString(),
                SparesParts.DoubleGrouserShoe.toString(),
                SparesParts.BottomRoller.toString(),
                SparesParts.TopRoller.toString(),
                SparesParts.Idler.toString(),
                SparesParts.Sprocket.toString()
        };


        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);


        btn_takePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, cam_activity.class);
                intent.putExtra("type",dropdown.getSelectedItem().toString());
                startActivity(intent);
                finish();

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Toast.makeText(MainActivity.this, "Saved" , Toast.LENGTH_LONG).show();

            }
        });


        btn_example.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bad);
                example_imgView.setImageBitmap(bm);
                example_imgView.setVisibility(View.VISIBLE);
                box1.setVisibility(View.INVISIBLE);
                box2.setVisibility(View.INVISIBLE);
                box3.setVisibility(View.INVISIBLE);
                btn_save.setVisibility(View.INVISIBLE);
                btn_example.setVisibility(View.INVISIBLE);
                dropdown.setVisibility(View.INVISIBLE);
                next = true;
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position == 3){
                    box1.setVisibility(View.VISIBLE);
                    box2.setVisibility(View.VISIBLE);
                    box3.setVisibility(View.VISIBLE);
                    btn_takePicture.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_example.setVisibility(View.VISIBLE);
                }
                else {
                    box1.setVisibility(View.INVISIBLE);
                    box2.setVisibility(View.INVISIBLE);
                    box3.setVisibility(View.INVISIBLE);
                    btn_save.setVisibility(View.INVISIBLE);
                    btn_takePicture.setVisibility(View.VISIBLE);
                    btn_example.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}
