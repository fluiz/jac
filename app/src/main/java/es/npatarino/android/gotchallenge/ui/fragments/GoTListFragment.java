package es.npatarino.android.gotchallenge.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
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
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterfaceImpl;
import es.npatarino.android.gotchallenge.model.GoTEntity;
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

        GoTResultsInterface gotResultsInterface = new GoTResultsInterfaceImpl() {
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
        };

        if (currentListDisplayed == ListType.Characters) {
            GoTDataSource.getCharacters(getContext(), gotResultsInterface);
        } else {
            GoTDataSource.getHouses(getContext(), gotResultsInterface);
        }
    }
}
