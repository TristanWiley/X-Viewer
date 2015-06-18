package terranovaproductions.newcomicreader;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying CardView
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{
    Context context;
    List<ComicItem> mItems;

    public GridAdapter() {
        super();
        mItems = new ArrayList<ComicItem>();
        ComicItem comic = new ComicItem();

        String dirPath = Environment.getExternalStorageDirectory().toString()+"/Comics/";
        File dir = new File(dirPath);
        final File[] filelist = dir.listFiles();

        for (int i = 0; i < filelist.length; ++i){
            comic = new ComicItem();
            comic.setName(filelist[i].getName());
            comic.setThumbnail(filelist[i].getAbsolutePath());
            mItems.add(comic);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        context = v.getContext();
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ComicItem comic = mItems.get(i);
        viewHolder.tvspecies.setText(comic.getName());
        Ion.with(context)
                .load(comic.getThumbnail())
                .intoImageView(viewHolder.imgThumbnail);

    }


    @Override
    public int getItemCount() {
        return mItems.size();
            }

    class ViewHolder extends RecyclerView.ViewHolder implements GestureDetector.OnGestureListener{
        private GestureDetectorCompat GesterDetect;

        public ImageView imgThumbnail;
        public TextView tvspecies;
        public ViewHolder(View itemView) {
            super(itemView);
            GesterDetect = new GestureDetectorCompat(context, this);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    GesterDetect.onTouchEvent(event);
                    return true;
                }
            });
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvspecies = (TextView)itemView.findViewById(R.id.tv_species);
        }
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Intent i = new Intent(context, FullZoom.class);
            i.putExtra("FROM_MAIN", false);
            i.putExtra("IMAGE_LOCATION", mItems.get(getPosition()).getThumbnail());
            context.startActivity(i);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }


    }
}