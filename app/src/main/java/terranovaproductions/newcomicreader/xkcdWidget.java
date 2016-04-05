package terranovaproductions.newcomicreader;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Implementation of App Widget functionality.
 */

public class xkcdWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.xkcd_widget);

        Ion.with(context)
                .load("http://xkcd.com/info.0.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonElement jurl = result.get("img");
                            String url = jurl.getAsString();
                            JsonElement jdescription = result.get("alt");
                            final String description = jdescription.getAsString();

                            Ion.with(context).load(url).withBitmap().asBitmap()
                                    .setCallback(new FutureCallback<Bitmap>() {
                                        @Override
                                        public void onCompleted(Exception e, Bitmap result) {
                                            views.setImageViewBitmap(R.id.widget_comic, result);
                                            views.setTextViewText(R.id.widget_alt, description);

                                            // Instruct the widget manager to update the widget
                                            appWidgetManager.updateAppWidget(appWidgetId, views);
                                        }
                                    });

                        }

                    }
                });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(final Context context) {
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.xkcd_widget);

        Ion.with(context)
                .load("http://xkcd.com/info.0.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonElement jurl = result.get("img");
                            JsonElement jdescription = result.get("alt");

                            String url = jurl.getAsString();
                            String description = jdescription.getAsString();


                            Ion.with(context).load(url).withBitmap().asBitmap()
                                    .setCallback(new FutureCallback<Bitmap>() {
                                        @Override
                                        public void onCompleted(Exception e, Bitmap result) {
                                            views.setImageViewBitmap(R.id.widget_comic, result);
                                        }
                                    });
                            views.setTextViewText(R.id.widget_alt, description);

                        }

                    }
                });
    }
}

