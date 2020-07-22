package ca.stclaircollege.fitgrind;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import java.io.File;

import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Progress;

public class FullScreenImageActivity extends AppCompatActivity {

    private PhotoView mImageView;
    private Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // get the action bar
        ActionBar bar = getSupportActionBar();
        // create a back button for the action bar
        bar.setDisplayHomeAsUpEnabled(true);

        mImageView = (PhotoView) findViewById(R.id.full_image);

        // get intent retrieved data
        Bundle extras = getIntent().getExtras();

        // check if an intent has passed
        if (extras != null) {
            progress = extras.getParcelable("progress");
            // if it does set the new imageview
            File file = new File(progress.getResource());
            Picasso.with(this).load(file).into(mImageView);

            // create an event listener
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActionBar bar = getSupportActionBar();
                    if (bar.isShowing()) {
                        bar.hide();
                    } else {
                        bar.show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // retrieve item
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_delete) {
            // if it's delete then we need to delete
            // create first a db handler
            DatabaseHandler db = new DatabaseHandler(this);
            // next we want to delete the item
            boolean result = db.deleteProgress(progress.getId());

            if (result) {
                Toast.makeText(getApplicationContext(), R.string.db_delete_success, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.db_error, Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
