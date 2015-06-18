package terranovaproductions.newcomicreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

/**
 * This file displays the saved images for sharing
 */

public class ShareView extends AppCompatActivity {
    ImageView iv;
    String stringuri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        iv = (ImageView) findViewById(R.id.sharedimage);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            stringuri = extras.getString("FILE_URI");
            Ion.with(getApplicationContext())
                    .load(extras.getString("FILE_URI"))
                    .intoImageView(iv);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_button:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                Uri uri = Uri.parse(stringuri);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share image to..."));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
