package terranovaproductions.newcomicreader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.koushikdutta.ion.Ion;

/**
 * Activity manages the zooming of fullscreen comics
 */
public class FullZoom extends AppCompatActivity {
    public String url;
    public String number;
    public TouchImageView iv;
    boolean hasLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullimage);
        iv = (TouchImageView) findViewById(R.id.fullScreenImage);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("IMAGE_LOCATION");
            number = extras.getString("IMAGE_TITLE");
            Ion.with(this)
                    .load(url)
                    .withBitmap()
                    .fadeIn(true)
                    .fitCenter()
                    .intoImageView(iv);
        }

    }

//    //Detects to see if a larger version of this comic exists by starting the GetResponse AsyncTask
//    public void hasLarge(String lnum) {
//        try {
//            final URL responseUrl = new URL("http://xkcd.com/" + lnum + "/large/");
//            GetResponse task = new GetResponse();
//            task.execute(responseUrl);
//        } catch (MalformedURLException mue) {
//            Log.d(getPackageName(), "Malformed Url in hasLarge");
//        }
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private class GetResponse extends AsyncTask<URL, Void, Integer> {
//        int responseCode = 0;
//
//
//        @Override
//        protected Integer doInBackground(URL... params) {
//            try {
//                HttpURLConnection huc = (HttpURLConnection) params[0].openConnection();
//                huc.setRequestMethod("HEAD");
//                responseCode = huc.getResponseCode();
//            } catch (IOException e) {
//                Log.d(getPackageName(), "IOException in Get Response");
//            }
//            return responseCode;
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            if (integer != 404 && integer != 0) {
//                hasLarge = true;
//            } else {
//                hasLarge = false;
//            }
//        }
//
//
//    }
//
//    public String getHtml() throws Exception{
//        String u = url;
//        String html;
//
//            URL url = new URL(u);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            InputStream in = new BufferedInputStream(conn.getInputStream());
//
//            BufferedReader r = new BufferedReader(new InputStreamReader(in));
//            StringBuilder total = new StringBuilder();
//            String line;
//            while ((line = r.readLine()) != null) {
//                total.append(line);
//            }
//            html = total.toString();
//
//            String imgRegex = "<[iI][mM][gG][^>]+[sS][rR][cC]\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
//
//            Pattern p = Pattern.compile(imgRegex);
//            Matcher m = p.matcher(html);
//
//            String imgSrc;
//            if (m.find()) {
//                imgSrc = m.group(1);
//            }
//        return html;
//    }


}
