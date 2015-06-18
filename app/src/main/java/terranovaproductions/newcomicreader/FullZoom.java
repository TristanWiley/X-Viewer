package terranovaproductions.newcomicreader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Activity manages the zooming of fullscreen comics
 */
public class FullZoom extends AppCompatActivity {
    public String url;
    public String number;
    public ImageView iv;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullimage);
        iv = (ImageView) findViewById(R.id.fullScreenImage);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                boolean fromMain = extras.getBoolean("FROM_MAIN");
                    url = extras.getString("IMAGE_LOCATION");
                if (fromMain = true) {
                    number = extras.getString("IMAGE_TITLE");
                }
                mAttacher = new PhotoViewAttacher(iv);
                    getSupportActionBar().setTitle(number);
                    Ion.with(this)
                            .load(url)
                            .withBitmap()
                            .fadeIn(true)
                            .fitCenter()
                            .intoImageView(iv);
                mAttacher.update();
            }

        }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
