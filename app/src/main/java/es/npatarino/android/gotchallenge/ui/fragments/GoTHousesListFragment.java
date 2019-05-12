package es.npatarino.android.gotchallenge.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.adapters.GoTHouseAdapter;
import es.npatarino.android.gotchallenge.adapters.GoTListAdapter;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;

public class GoTHousesListFragment extends Fragment {

    private static final String TAG = "GoTHousesListFragment";

    public GoTHousesListFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        final ContentLoadingProgressBar pb = (ContentLoadingProgressBar) rootView.findViewById(R.id.pb);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);

        //final GoTHouseAdapter adp = new GoTHouseAdapter(getActivity());
        final GoTListAdapter adp = new GoTListAdapter(GoTListFragment.ListType.Houses, getContext());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(false);
        rv.setAdapter(adp);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String url = "https://project-8424324399725905479.firebaseio.com/characters.json?print=pretty";

                URL obj = null;
                try {
                    obj = new URL(url);
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
                    }.getType();
                    final List<GoTCharacter> characters = new Gson().fromJson(response.toString(), listType);
                    GoTHousesListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<GoTEntity> hs = new ArrayList<>();
                            for (int i = 0; i < characters.size(); i++) {
                                boolean b = false;
                                for (int j = 0; j < hs.size(); j++) {
                                    GoTCharacter.GoTHouse gotHouse = (GoTCharacter.GoTHouse) hs.get(j);
                                    if (gotHouse.getHouseName().equalsIgnoreCase(characters.get(i).getHouseName())) {
                                        b = true;
                                    }
                                }
                                if (!b) {
                                    if (characters.get(i).getHouseId() != null && !characters.get(i).getHouseId().isEmpty()) {
                                        GoTCharacter.GoTHouse h = new GoTCharacter.GoTHouse();
                                        h.setHouseId(characters.get(i).getHouseId());
                                        h.setHouseName(characters.get(i).getHouseName());
                                        h.setHouseImageUrl(characters.get(i).getHouseImageUrl());
                                        hs.add(h);
                                        b = false;
                                    }
                                }
                            }
                            adp.addAll(hs);
                            adp.notifyDataSetChanged();
                            pb.hide();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();
        return rootView;
    }
}
