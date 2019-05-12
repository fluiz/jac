package es.npatarino.android.gotchallenge.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.adapters.GoTListAdapter;
import es.npatarino.android.gotchallenge.api.GoTDataSource;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;
import es.npatarino.android.gotchallenge.ui.activities.DetailActivity;
import es.npatarino.android.gotchallenge.util.GoTEntityUtils;


public class GoTListFragment extends Fragment {

    private static final String TAG = "GoTListFragment";

    private static final String TYPE_LIST = "type_list";
    private static final String HOUSE_ID = "house_id";

    GoTListAdapter gotListAdapter;

    public enum ListType{
        Characters,
        Houses
    }

    private ListType currentListDisplayed;
    private String showOnlyHouseId;

    public static GoTListFragment newInstance(ListType type, @Nullable String houseId) {
        GoTListFragment fragment = new GoTListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_LIST, type.ordinal());
        args.putString(HOUSE_ID, houseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentListDisplayed = ListType.values()[getArguments().getInt(TYPE_LIST)];
            showOnlyHouseId = getArguments().getString(HOUSE_ID);
            Log.i(TAG, "House to show: "+ showOnlyHouseId);
        }
        gotListAdapter = new GoTListAdapter(currentListDisplayed, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        /*
        final ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) rootView.findViewById(R.id.pb);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);

        final GoTListAdapter gotListAdapter = new GoTListAdapter(currentListDisplayed, getContext());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(false);
        rv.setAdapter(gotListAdapter);


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
                    final List<GoTEntity> characters = new Gson().fromJson(response.toString(), listType);
                    GoTListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gotListAdapter.addAll(characters);
                            gotListAdapter.notifyDataSetChanged();
                            progressBar.hide();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }


            }
        }).start();
        */

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "view created");

        final ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.pb);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);

        final GoTListAdapter gotListAdapter = new GoTListAdapter(currentListDisplayed, this);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(false);
        rv.setAdapter(gotListAdapter);

        GoTResultsInterface gotResultsInterface = new GoTResultsInterface() {
            @Override
            public void onSuccess(List<GoTEntity> entities) {
                final List<GoTEntity> gotEntities = entities;

                GoTListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<GoTEntity> filteredEntities = gotEntities;
                        if (showOnlyHouseId != null) {
                            filteredEntities = GoTEntityUtils.filterCharactersByHouse(gotEntities, showOnlyHouseId);
                        }
                        Log.i(TAG, "Showing elements: "+ filteredEntities.size());
                        gotListAdapter.addAll(filteredEntities);
                        gotListAdapter.notifyDataSetChanged();
                        progressBar.hide();
                    }
                });
            }

            @Override
            public void onFailure() {

            }
        };

        //view.findViewById(R.id.ivBackground).setOnClickListener();

        //List<GoTEntity> gotEntitiesCandidates;
        if (currentListDisplayed == ListType.Characters) {
            GoTDataSource.getCharacters(gotResultsInterface);
        } else {
            GoTDataSource.getHouses(gotResultsInterface);
        }

        /*final List<GoTEntity> gotEntities = gotEntitiesCandidates;

        GoTListFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gotListAdapter.addAll(gotEntities);
                gotListAdapter.notifyDataSetChanged();
                progressBar.hide();
            }
        });*/

    }

    /*private View.OnClickListener getOnClickListener() {
        View.OnClickListener listener;
        if (currentListDisplayed == GoTListFragment.ListType.Characters) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    GoTCharacter gotChar = ((GoTCharacter) gotEntities.get(holder.getAdapterPosition()));
                    gotListAdapter.get
                    intent.putExtra("description", gotChar.getDescription());
                    intent.putExtra("name", gotChar.getName());
                    intent.putExtra("imageUrl", gotChar.getImageUrl());
                    gotViewHolder.itemView.getContext().startActivity(intent);
                }
            };
        } else {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(gotViewHolder.itemView.getContext(), HomeActivity.class);
                    GoTCharacter.GoTHouse gotHouse = (GoTCharacter.GoTHouse) gotEntities.get(holder.getAdapterPosition());
                    //intent.putExtra("house_id", gotHouse.getHouseId());
                    //gotViewHolder.itemView.getContext().startActivity(intent);
                    //GoTListFragment listFragment = (GoTListFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.container);
                    GoTListFragment listFragment = GoTListFragment.newInstance(GoTListFragment.ListType.Characters, gotHouse.getHouseId());
                    ((AppCompatActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_list, listFragment)
                            .commitAllowingStateLoss();
                }
            };
        }
    }*/
}
