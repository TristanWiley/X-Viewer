package terranovaproductions.newcomicreader.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import terranovaproductions.newcomicreader.ImageGrid;
import terranovaproductions.newcomicreader.R;

/**
 * Created by Tristan on 12/25/2014.
 */

public class FailedWifiFragment extends Fragment {
    Button wifiSettingsButton;
    Button retryButton;
    Button savedComics;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        super.onCreateView(inflater, null, bundle);
        return inflater.inflate(R.layout.fragment_wififail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wifiSettingsButton = (Button) getActivity().findViewById(R.id.wifiSettings);
        retryButton = (Button) getActivity().findViewById(R.id.retryButton);
        savedComics = (Button) getActivity().findViewById(R.id.savedComics);

        wifiSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new ComicFragment())
                            .commit();

                }
            }
        });

        savedComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ImageGrid.class);
                startActivity(i);
            }
        });
    }
}
