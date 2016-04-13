package terranovaproductions.newcomicreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Main fragment containing all Comic stuff. Tristan Wiley
 */

public class ComicFragment extends Fragment {
    TextView titleView;
    TextView altText;

    ImageView iv;

    int num;
    int latest;
    int position = 1;

    String title;
    String url;
    String description;

    SharedPreferences prefs;

    Typeface xkcdFont;
    Typeface altFont;

    SearchView sv;
    MenuItem mSearchMenuItem;

    CoordinatorLayout coordinatorLayoutView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle bundle) {
        super.onCreateView(inflater, null, bundle);

        //Inflate the layout
        return inflater.inflate(R.layout.fragment_comic, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayoutView = (CoordinatorLayout) getActivity().findViewById(R.id.snackbar);

        //Get the SharedPreferences saved null){
        prefs = getActivity().getSharedPreferences(
                "terranovaproductions.newcomicreader", Context.MODE_PRIVATE);


        //Initialize the TextView for the title of the comic
        titleView = (TextView) getActivity().findViewById(R.id.title);
        //Initialize the TextView for the hover text of the comic
        altText = (TextView) getActivity().findViewById(R.id.alt);

        //Initialize the ImageView for the comic to be put in
        iv = (ImageView) getActivity().findViewById(R.id.imageView);


        //Initialize the Typeface for the official font of xkcd.
        xkcdFont = Typeface.createFromAsset(getActivity().getAssets(), "xkcdFont.otf");

        //Set the title TextView to the official xkcd font.
        titleView.setTypeface(xkcdFont);

        //Initialize the DejaVuSans font for the hover text
        altFont = Typeface.createFromAsset(getActivity().getAssets(), "DejaVuSans.ttf");

        //Set the altText TextView's font to DejaVuSans
        altText.setTypeface(altFont);

        //Set an OnLongClickListener so when it is long pressed the option to copy the hover text appears in an AlertDialog
        altText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Create a new AlertDialog Builder
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

                //Set the title of the AlertDialog
                adb.setTitle("Copy Hover Text");

                //Set the message of the AlertDialog to ask if the user wants to copy the text
                adb.setMessage("Are you sure you want to copy the hover text of this comic?");

                //If the user chooses yes, copies the hover text
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Create a ClipboardManager to copy the text
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                        //Copy the hover text to the clipboard
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Alt Text", altText.getText());
                        clipboard.setPrimaryClip(clip);

                        //Tell the user that the text was copied.
                        Snackbar.make(coordinatorLayoutView, "Hover text copied to clipboard", Snackbar.LENGTH_SHORT).show();
                    }
                });

                //If the users decides not to copy the text
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel the dialog and close it
                        dialog.cancel();
                    }
                });
                //Show this dialog
                adb.show();
                return false;
            }
        });

        //Set an OnClickListener for the ImageView to open it in FullScreen on click
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Create a new Intent to go to the FullScreen, Zoomable activity
                Intent intent = new Intent(getActivity(), FullZoom.class);

                //Tell the FullZoom activity it is not from the Adapter (The adapter shows the saved comics)
                intent.putExtra("FROM_ADAPTER", false);

                //Add the url location (Url) of the comic
                intent.putExtra("IMAGE_LOCATION", url);

                //Send the title of the comic to the FullZoom activity for in the ActionBar
                intent.putExtra("IMAGE_TITLE", Integer.toString(num));

                //Start the Intent
                startActivity(intent);
            }
        });

        //Set the movement method of the altText textView so it can scroll
        altText.setMovementMethod(new ScrollingMovementMethod());
        //TODO kopwetko
        loadComic("http://xkcd.com/" + position + "/info.0.json");

    }

    @Override
    public void onPause() {
        super.onPause();
        //When the app is paused (closed, etc) add the current latest comic to the SharedPreferences,
        prefs.edit().putInt("COMIC_LATEST", latest).apply();
    }

    public void loadComic(final String nextUrl) {
        //TODO add icons and functionality for news and links when available
        //Initialize a Connectivity Manager and get the Network Information
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getActiveNetworkInfo();

        if (sv != null) {
            //Close the SearchView
            MenuItemCompat.collapseActionView(mSearchMenuItem);
        }

        //Make sure there is an internet connection before fetching comics.
        if (mWifi.isConnected()) {

            //Load the next comic the user wants to see
            Ion.with(getActivity())
                    .load(nextUrl)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                //Get the number of the comic as a JSONElement
                                JsonElement jnum = result.get("num");

                                //Get the title of the comic as a JSONElement
                                JsonElement jtitle = result.get("title");

                                JsonElement jdescription = result.get("alt");

                                JsonElement jurl = result.get("img");

                                //Get the number of the comic as an integer
                                num = jnum.getAsInt();

                                //Get the Title of the comic as a String
                                title = jtitle.getAsString();

                                //Get the hover text of the comic as a String
                                description = jdescription.getAsString();

                                //Get the url of the comic image as a String
                                url = jurl.getAsString();

                                //Set the title to the comic to it's TextView with the number of the comic
                                titleView.setText(title + " - #" + num);

                                //Set the hover text of the comic to it's TextView
                                altText.setText(description);

                                //Make sure the activity is loaded and set the image to the ImageView
                                if (getActivity() != null) {
                                    Ion.with(getActivity())
                                            .load(url)
                                            .withBitmap()
                                            .fadeIn(true)
                                            .intoImageView(iv);
                                }

                                //Make sure the hover text is scrolled to the top
                                altText.scrollTo(0, 0);

                                //Save the current comic in the share preferences for the next open
                                prefs.edit().putInt("COMIC_CURRENT", num).apply();

                            } else {
                                Snackbar.make(coordinatorLayoutView, "This comic does not exist! Try again!", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });

        } else {
            //If there is no internet then replace the Fragment with the failed wifi Fragment.
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new FailedWifiFragment())
                    .commit();
        }
    }

    public void setPosition(int p) {
        position = p;
    }

}