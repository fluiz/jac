package es.npatarino.android.gotchallenge.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.npatarino.android.gotchallenge.R;


public class GoTListFragment extends Fragment {

    private static final String TYPE_LIST = "type_list";

    public enum ListType{
        Characters,
        Houses
    }

    private ListType currentListDisplayed;

    public static GoTListFragment newInstance(ListType type) {
        GoTListFragment fragment = new GoTListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_LIST, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentListDisplayed = ListType.values()[getArguments().getInt(TYPE_LIST)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
