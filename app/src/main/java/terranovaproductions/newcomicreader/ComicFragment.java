package terranovaproductions.newcomicreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

/**
 * Main fragment containing all Comic stuff. Tristan Wiley
 */

public class ComicFragment extends Fragment {
    TextView titleView;
    TextView altText;
    ImageView iv;
    int num;
    String title;
    String url;
    String description;
    int latest;
    ImageButton searchButton;
    ImageButton randomButton;
    ImageButton browserButton;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        super.onCreateView(inflater, null, bundle);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity())
                .setActionBarTitle("XKCD");
        return inflater.inflate(R.layout.fragment_comic, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_first) {
            loadComic("http://xkcd.com/1/info.0.json");
        } else if (id == R.id.action_previous) {
                if (num == 1) {
                Toast.makeText(getActivity(), "No more comics!", Toast.LENGTH_SHORT).show();
            } else {
                int nextNum;
                if (num != 405) {
                    nextNum = num - 1;
                } else {
                    nextNum = 403;
                }
                String nextUrl = "http://xkcd.com/" + String.valueOf(nextNum) + "/info.0.json";
                loadComic(nextUrl);
            }
        } else if (id == R.id.action_next) {
            int nextNum;
            if (num != 403) {
                nextNum = num + 1;
            } else {
                nextNum = 405;
            }
            String nextUrl = "http://xkcd.com/" + String.valueOf(nextNum) + "/info.0.json";
            loadComic(nextUrl);

        } else if (id == R.id.action_latest) {
            loadComic("http://xkcd.com/info.0.json");
        } else if (id == R.id.action_download) {
            if (url != null) {

                Ion.with(this).load(url).withBitmap().asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                int h = result.getHeight();
                                int w = result.getWidth();
                                TextPaint tp = new TextPaint(Color.WHITE);
                                StaticLayout sl = new StaticLayout(description, tp, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                Bitmap b = Bitmap.createBitmap(w, h + sl.getHeight()+10, Bitmap.Config.ARGB_8888);
                                Canvas c = new Canvas(b);
                                Paint p = new Paint(Color.WHITE);
                                c.drawBitmap(result, 0, 0, p);
                                c.translate(0, c.getHeight() - sl.getHeight());
                                sl.draw(c);
                                iv.setImageBitmap(b);
                            }


//                        });
//                Paint p = new Paint();
//                p.setColor(Color.WHITE);
//                p.setStyle(Paint.Style.FILL);
//                Canvas c = new Canvas(image);
//                c.drawPaint(p);
////                view.draw(c);
//                //Store to sdcard
//                try {
//                    String path = Environment.getExternalStorageDirectory().toString() + "/Comics";
//                    File myFile = new File(path, title + ".png");
//                    FileOutputStream out = new FileOutputStream(myFile);
//
//                    image.compress(Bitmap.CompressFormat.PNG, 90, out); //Output
//                    Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                        });
            } else {
                Toast.makeText(getActivity(), "An error occured, cannot get comic", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.action_gallery){
            Intent i = new Intent(getActivity(), ImageGrid.class);
            startActivity(i);
        }else if (id == R.id.action_share) {

            //TODO Fix the share function

            //Find the view we are after
            RelativeLayout view = (RelativeLayout) getActivity().findViewById(R.id.downloadChild);
            //Create a Bitmap with the same dimensions
            Bitmap image = Bitmap.createBitmap(view.getWidth(),
                    view.getHeight(),
                    Bitmap.Config.RGB_565);
            //Draw the view inside the Bitmap
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL);
            Canvas c = new Canvas(image);
            c.drawPaint(p);
            view.draw(c);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();

            Intent share = new Intent(Intent.ACTION_SEND);
            // If you want to share a png image only, you can do:
            // setType("image/png"); OR for jpeg: setType("image/jpeg");
            share.setType("image/*");
            // Make sure you put example png image named myImage.png in your
            // directory
            share.putExtra(Intent.EXTRA_STREAM, bytes);
            share.putExtra(Intent.EXTRA_TEXT, description);
            startActivity(Intent.createChooser(share, "Share image to..."));

        } else if (id == R.id.explainxkcd) {
            String explainURL = "http://www.explainxkcd.com/wiki/index.php/" + num;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(explainURL));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Comics/");
        if (!f.exists() && !f.isDirectory()) {
            f.mkdirs();
        }
        AdView adView = (AdView) getActivity().findViewById(R.id.adMob);
        //request TEST ads to avoid being disabled for clicking your own ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                        //test mode on DEVICE (this example code must be replaced with your device uniquq ID)
                .addTestDevice("970E0F4DAD9C66416B9106AFD31C87F5") // Nexus 5
                .build();
        adView.loadAd(adRequest);
        prefs = getActivity().getSharedPreferences(
                "terranovaproductions.newcomicreader", Context.MODE_PRIVATE);
        int lastComic = prefs.getInt("COMIC_CURRENT", 0);
        if (lastComic != 0){
            loadComic("http://xkcd.com/" + String.valueOf(lastComic) + "/info.0.json");
        }else{
            loadComic("http://xkcd.com/info.0.json");
        }
        titleView = (TextView) getActivity().findViewById(R.id.title);
        iv = (ImageView) getActivity().findViewById(R.id.imageView);
        altText = (TextView) getActivity().findViewById(R.id.alt);
        searchButton = (ImageButton) getActivity().findViewById(R.id.searchButton);
        randomButton = (ImageButton) getActivity().findViewById(R.id.randomButton);
        browserButton = (ImageButton) getActivity().findViewById(R.id.browserButton);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (getActivity() != null) {
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.searchprompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String nextURL = "http://xkcd.com/" + userInput.getText() + "/info.0.json";
                                            loadComic(nextURL);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();


                }
            }
        });
        browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    String comicXKCD = "http://xkcd.com/" + num + "/";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(comicXKCD));
                    startActivity(browserIntent);
                }
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Random r = new Random();
                    int randomNum = r.nextInt(latest - 1) + 1;
                    randomNum = Math.abs(randomNum);
                    if (getActivity() != null) {
                        if (randomNum != 404) {
                            loadComic("http://xkcd.com/" + String.valueOf(randomNum) + "/info.0.json");
                        } else {
                            loadComic("http://xkcd.com/" + 506 + "/info.0.json");
                        }
                    }
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), FullZoom.class);
                intent.putExtra("FROM_MAIN", true);
                intent.putExtra("IMAGE_LOCATION", url);
                intent.putExtra("IMAGE_TITLE", Integer.toString(num));
                startActivity(intent);
            }
        });

        altText.setMovementMethod(new ScrollingMovementMethod());

        iv.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeRight() {
                if (num == 1) {
                    Toast.makeText(getActivity(), "No more comics!", Toast.LENGTH_SHORT).show();
                } else {
                    int nextNum;
                    if (num != 405) {
                        nextNum = num - 1;
                    } else {
                        nextNum = 403;
                    }
                    String nextUrl = "http://xkcd.com/" + String.valueOf(nextNum) + "/info.0.json";
                    loadComic(nextUrl);

                }
                return true;
            }

            public boolean onSwipeLeft() {
                int nextNum;
                if (num != 403) {
                    nextNum = num + 1;
                } else {
                    nextNum = 405;
                }
                String nextUrl = "http://xkcd.com/" + String.valueOf(nextNum) + "/info.0.json";

                loadComic(nextUrl);
                return true;
            }
        });


    }
    public void loadComic(String nextUrl){
        Ion.with(getActivity())
                .load("http://xkcd.com/info.0.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonElement jnum = result.get("num");
                        latest = jnum.getAsInt();
                    }
                });
        Ion.with(getActivity())
                .load(nextUrl)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonElement jtitle = result.get("title");
                            JsonElement jdescription = result.get("alt");
                            JsonElement jurl = result.get("img");
                            JsonElement jnum = result.get("num");

                            title = jtitle.getAsString();

                            description = jdescription.getAsString();
                            url = jurl.getAsString();
                            num = jnum.getAsInt();
                            titleView.setText(title + " #" + num);
                            altText.setText(description);
                            if (getActivity() != null) {
                                Ion.with(getActivity())
                                        .load(url)
                                        .withBitmap()
                                        .fadeIn(true)
                                        .intoImageView(iv);
                            }
                            altText.scrollTo(0, 0);

                            prefs.edit().putInt("COMIC_CURRENT", num).apply();

                        } else {
                            Toast.makeText(getActivity(), "Either you are not connect to the internet or the comic does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        }
}

