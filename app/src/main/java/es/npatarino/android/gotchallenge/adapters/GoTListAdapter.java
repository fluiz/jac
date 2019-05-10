package es.npatarino.android.gotchallenge.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.npatarino.android.gotchallenge.ui.fragments.GoTListFragment;
import es.npatarino.android.gotchallenge.R;

/**
 * Created by Admin on 15/12/2016.
 */

public class GoTListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static GoTListFragment.ListType type;
    Context context;

   // private final List<GoTCharacter> gcs;

    public GoTListAdapter(GoTListFragment.ListType type, Context context){
        this.type = type;
        this.context = context;
        //gcs = new List<GoTCharacter>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == 1) {
            View itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_character_row, parent, false);

          GotCharacterViewHolder vh = new GotCharacterViewHolder (itemView);
            return vh;
        }
        else {
            View itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_house_row, parent, false);

          GotHouseViewHolder vh = new GotHouseViewHolder (itemView);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (type == GoTListFragment.ListType.Characters) ? 1 : 0;
    }

    class GotCharacterViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotCharacterViewHolder";
        ImageView imp;
        TextView tvn;

        public GotCharacterViewHolder(View itemView) {
            super(itemView);
            imp = (ImageView) itemView.findViewById(R.id.ivBackground);
            tvn = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    class GotHouseViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotCharacterViewHolder";
        ImageView imp;

        public GotHouseViewHolder(View itemView) {
            super(itemView);
            imp = (ImageView) itemView.findViewById(R.id.ivBackground);
        }
    }
}
