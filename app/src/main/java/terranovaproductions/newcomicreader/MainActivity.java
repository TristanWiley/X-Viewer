package terranovaproductions.newcomicreader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    public int latest;
    Typeface altFont;
    FloatingActionButton browserButton;
    CoordinatorLayout coordinatorLayoutView;
    String description;
    FloatingActionButton downloadButton;
    List first_queries;
    List latest_queries;
    MenuItem mSearchMenuItem;
    ViewPager mViewPager;
    int num;
    SharedPreferences prefs;
    FloatingActionButton randomButton;
    SearchView sv;
    String title;
    String url;
    Typeface xkcdFont;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case (100):
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted, continue to save le comic!
                    shareComic(url);
                } else {
                    //Permission Denied, let the user know.
                    Snackbar.make(coordinatorLayoutView, "In order to save the comic or view saved comics you need to give access to the 'WRITE_EXTERNAL_STORAGE' permission", Snackbar.LENGTH_SHORT);
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//Handles the item clicks in the ActionBar, the id is equal to the id of the item in the action bar
        int id = item.getItemId();

        if (id == R.id.action_gallery) {
            int hasStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    final View.OnClickListener clickListener = new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:terranovaproductions.newcomicreader"));
                            startActivity(i);
                        }
                    };

                    Snackbar
                            .make(coordinatorLayoutView, "You need to allow the permission to view saved comics!", Snackbar.LENGTH_LONG)
                            .setAction("Okay", clickListener)
                            .show();

                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                String dirPath = Environment.getExternalStorageDirectory().toString() + "/Comics/";
                File dir = new File(dirPath);
                if (dir.exists()) {
                    //Create intent to move to the ImageGrid class
                    Intent i = new Intent(getApplicationContext(), ImageGrid.class);
                    //Start the intent
                    startActivity(i);
                } else {
                    Snackbar.make(coordinatorLayoutView, "You have not saved a comic yet, save one to get started!", Snackbar.LENGTH_SHORT);
                }
            }

        } else if (id == R.id.action_search) {
            //Expand the SearchView so you can search in it
            MenuItemCompat.expandActionView(mSearchMenuItem);
//            sv.setIconified(false);

            //Listener for when text is typed in the SearchView
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {

                    //Set the two Lists to the Arrays from the Resources
                    first_queries = Arrays.asList(getResources().getStringArray(R.array.queries_first));
                    latest_queries = Arrays.asList(getResources().getStringArray(R.array.queries_latest));

                    if (TextUtils.isDigitsOnly(query)) {
                        //Check to see if it is just a number
                        mViewPager.setCurrentItem(Integer.parseInt(query));

                    } else if (first_queries.contains(query)) {
                        //Check to see if it is a phrase in the array that goes to the first comic (1)
                        mViewPager.setCurrentItem(1);

                    } else if (latest_queries.contains(query)) {
                        //Check to see if it is a phrase in the array that goes to the latest comic
                        mViewPager.setCurrentItem(latest);

                    } else if (!TextUtils.isDigitsOnly(query)) {
                        //Check to see if the query is not digits only, if it is then convert the word to a number and go to the comic
                        WordToNumber testInstance = new WordToNumber();
                        int numbers = testInstance.inNumerals(query);
                        mViewPager.setCurrentItem(numbers);
                    } else {
                        Snackbar.make(coordinatorLayoutView, "I didn't understand that request", Snackbar.LENGTH_SHORT).show();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    return false;
                }
            });
        } else if (id == R.id.action_share) {

            int hasStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    final View.OnClickListener clickListener = new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:terranovaproductions.newcomicreader"));
                            startActivity(i);

                        }
                    };

                    Snackbar
                            .make(coordinatorLayoutView, "You need to allow the permission to share a comic!", Snackbar.LENGTH_LONG)
                            .setAction("Okay", clickListener)
                            .show();

                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                shareComic(url);
            }

        } else if (id == R.id.explainxkcd) {
            //Get the String of the ExplainXKCD article for the comic
            String explainURL = "http://www.explainxkcd.com/wiki/index.php/" + num;

            //Create an Intent to view the explainxkcd url
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(explainURL));

            //Start the Intent
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);


        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            mViewPager = (ViewPager) findViewById(R.id.viewPager);
            MyAdapter pagerAdapter = new MyAdapter(getSupportFragmentManager());
            latestComic(pagerAdapter);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setCurrentItem(1);

            coordinatorLayoutView = (CoordinatorLayout) findViewById(R.id.snackbar);

            //Get the SharedPreferences saved null){
            prefs = getSharedPreferences(
                    "terranovaproductions.newcomicreader", Context.MODE_PRIVATE);

            //Initialize the FloatingActionButton for
            browserButton = (FloatingActionButton) findViewById(R.id.fab_browser);
            randomButton = (FloatingActionButton) findViewById(R.id.fab_random);
            downloadButton = (FloatingActionButton) findViewById(R.id.fab_download);

            //Initialize the Typeface for the official font of xkcd.
            xkcdFont = Typeface.createFromAsset(getAssets(), "xkcdFont.otf");

            //Initialize the DejaVuSans font for the hover text
            altFont = Typeface.createFromAsset(getAssets(), "DejaVuSans.ttf");

            //Set a click listener to the FloatingActionButton that opens the comic in a browser.
            browserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Make sure the activity isQ1 not null
                    if (getApplicationContext() != null) {
                        //Make a String to go to the xkcd on the site (xkcd,com)
                        String comicXKCD = "http://xkcd.com/" + num + "/";

                        //Create an Intent to open the browser to the xkcd site.
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(comicXKCD));

                        //Start the Intent
                        startActivity(browserIntent);

                        //Close the FloatingActionButton menu so it does not remain open.
                    }
                }
            });

            //OnClickListener to change to a random comic
            randomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Create a new random
                    Random r = new Random();

                    //Generate a new random number
                    int randomNum = r.nextInt(latest - 1) + 1;

                    //Get the absolute value of the number so it is not negative
                    randomNum = Math.abs(randomNum);
                    //Make sure the activity is not null again
                    if (getApplicationContext() != null) {
                        //Make sure the number is not #404 as it does not exist, if it is just go to comic 506
                        if (randomNum != 404) {
                            //Load the random comic
                            mViewPager.setCurrentItem(randomNum);
                        } else {
                            //Load comic 506 since 404 doesn't exist
                            mViewPager.setCurrentItem(506);
                        }
                        //Close the FloatingActionMenu
                    }
                }
            });

            final View.OnClickListener clickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:terranovaproductions.newcomicreader"));
                    startActivity(i);
                }
            };

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hasStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            Snackbar
                                    .make(coordinatorLayoutView, "You need to allow the permission to save a comic!", Snackbar.LENGTH_LONG)
                                    .setAction("Okay", clickListener)
                                    .show();

                        }
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    } else {
                        downloadComic(url);
                    }
                }
            });
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new FailedWifiFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);

        //Initialize the SearchView
        sv = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        //Add Submit Button to SearchView
        sv.isSubmitButtonEnabled();
        return super.onCreateOptionsMenu(menu);
    }

    public void downloadComic(String url) {
        createComicDir();

        //Make sure the url to download exists
        if (url != null) {
            //Get the comic as a bitmap from the url and set the callback to do something with said bitmap.
            Ion.with(getApplicationContext()).load(url).withBitmap().asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            //Get height and width of the comic bitmap
                            int h = result.getHeight();
                            int w = result.getWidth();

                            //Create and set the TextPaint for the StaticLayout (Alt Text)
                            TextPaint tp = new TextPaint(Color.BLACK);
                            //Set the font to the official xkcd font.
                            tp.setTypeface(xkcdFont);
                            //Set the text size
                            tp.setTextSize(14);
                            //Center the text
                            tp.setTextAlign(Paint.Align.CENTER);

                            //Set alt text to StaticLayout (and all other attribute.
                            //description is a String with the Alt Text, tp is the TextPaint, w is the width.
                            StaticLayout sl = new StaticLayout(description, tp, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

                            //Create a bitmap for the comic to be drawn on along with the text
                            Bitmap b = Bitmap.createBitmap(w, h + sl.getHeight() + 10, Bitmap.Config.ARGB_8888);

                            //Create a Canvas to draw text on with the temporary Bitmap
                            Canvas c = new Canvas(b);

                            //Draw the background of the canvas full White
                            c.drawColor(Color.WHITE);

                            //Create a Paint with color of White.
                            Paint p = new Paint(Color.WHITE);

                            //Draw Bitmap on Canvas
                            c.drawBitmap(result, 0, 0, p);

                            //Move the canvas down to below the comic
                            c.translate(w / 2, c.getHeight() - sl.getHeight());

                            //Draw the StaticLayout
                            sl.draw(c);

                            //Store to sdcard
                            try {
                                //Create path
                                String path = Environment.getExternalStorageDirectory().toString() + "/Comics";
                                //New File in path
                                File myFile = new File(path, title + ".jpg");
                                FileOutputStream out = new FileOutputStream(myFile);
                                //Output file
                                b.compress(Bitmap.CompressFormat.PNG, 90, out);
                                //Tell the user it is saved
                                Snackbar.make(coordinatorLayoutView, "Saved!", Snackbar.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    });
        }
    }

    public void latestComic(@Nullable final MyAdapter adapter) {
        Ion.with(getApplicationContext())
                .load("http://xkcd.com/info.0.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        //Get the number of the latest comic as a JSON element
                        JsonElement jnum = result.get("num");

                        //The latest comic number as an integer
                        latest = jnum.getAsInt();

                        if (adapter != null) {
                            adapter.setLatest(latest);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


    }

    public void shareComic(String url) {
        createComicDir();
        //Get the comic as a Bitmap
        Ion.with(this).load(url).withBitmap().asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        //Get height and width of the comic bitmap
                        int h = result.getHeight();
                        int w = result.getWidth();

                        //Create and set the TextPaint for the StaticLayout (Alt Text)
                        TextPaint tp = new TextPaint(Color.BLACK);
                        //Set the font to the official xkcd font.
                        tp.setTypeface(xkcdFont);
                        //Set the font size
                        tp.setTextSize(14);
                        //Center the text
                        tp.setTextAlign(Paint.Align.CENTER);

                        //Set alt text to StaticLayout (and all other attribute.
                        //description is a String with the Alt Text, tp is the TextPaint, w is the width.
                        StaticLayout sl = new StaticLayout(description, tp, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        //Create a bitmap for the comic to be drawn on along with the text
                        Bitmap b = Bitmap.createBitmap(w, h + sl.getHeight() + 10, Bitmap.Config.ARGB_8888);
                        //Create a Canvas to draw text on with the temporary Bitmap
                        Canvas c = new Canvas(b);
                        //Draw the background of the canvas full White
                        c.drawColor(Color.WHITE);
                        //Create a Paint with color of White.
                        Paint p = new Paint(Color.WHITE);
                        //Draw Bitmap on Canvas
                        c.drawBitmap(result, 0, 0, p);
                        //Move the canvas down to below the comic
                        c.translate(w / 2, c.getHeight() - sl.getHeight());
                        //Draw the StaticLayout
                        sl.draw(c);

                        try {
                            //Create String for path+
                            String path;

                            //Make sure there is an External Cache Directory
                            if (getExternalCacheDir() != null) {
                                //If there is an external cache directory then set it to the path
                                path = getExternalCacheDir().toString();
                            } else {
                                //If there isn't an external cache directory then set it to the path
                                path = Environment.getExternalStorageDirectory().toString();
                            }

                            //Create a file to store the comic temporarily
                            File myFile = new File(path, "temporary_comic.jpg");

                            //Create FileOutputStream to save the file to
                            FileOutputStream out = new FileOutputStream(myFile);

                            //Compress the bitmap and output it to the path
                            b.compress(Bitmap.CompressFormat.PNG, 90, out);

                            //Create an Intent for sharing
                            Intent shareIntent = new Intent();

                            //Set the action of the Intent for sharing
                            shareIntent.setAction(Intent.ACTION_SEND);

                            //Put the default text to share with the comic "Shared with X-Viewer..." etc.
                            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.default_share));

                            //Put the saved comic in the Intent to be shared
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myFile));

                            //Set the type to be shared as a JPEG image
                            shareIntent.setType("image/jpeg");

                            //Start the sharing Intent.
                            startActivity(Intent.createChooser(shareIntent, "send"));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public void createComicDir() {
        //New file located on the SDCard for saved Comics to be put in
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Comics/");

        //Check to see if the directory exists, should not exist on the first run and if the cache was cleared
        if (!f.exists() && !f.isDirectory()) {
            //Make the directory if it doesn't exist
            f.mkdirs();
        }
    }
}

class MyAdapter extends FragmentStatePagerAdapter {
    int latest = Integer.MAX_VALUE;

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ComicFragment comicfragment = new ComicFragment();
        comicfragment.setPosition(position);
        Log.e("AdapterGetItem", (new StringBuilder()).append("Position is ").append(position).toString());
        return comicfragment;
    }

    @Override
    public int getCount() {
        return latest;
    }

    public void setLatest(int l) {
        latest = l;
    }
}



