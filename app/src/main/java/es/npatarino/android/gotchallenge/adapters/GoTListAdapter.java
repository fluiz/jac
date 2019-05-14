package es.npatarino.android.gotchallenge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.npatarino.android.gotchallenge.api.GoTDataSource;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;
import es.npatarino.android.gotchallenge.ui.activities.DetailActivity;
import es.npatarino.android.gotchallenge.ui.fragments.GoTListFragment;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.util.GoTEntityUtils;

/**
 * Created by Admin on 15/12/2016.
 */

public class GoTListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    GoTListFragment.ListType type;
    Fragment callingFragment;

   private List<GoTEntity> gotEntitiesFiltered;
   private List<GoTEntity> gotEntities;

    public GoTListAdapter(GoTListFragment.ListType type, Fragment fragment){
        this.type = type;
        this.callingFragment = fragment;
        gotEntitiesFiltered = new ArrayList<>();
        gotEntities = new ArrayList<>();
    }

    public void addAll(Collection<GoTEntity> collection) {
        for (int i = 0; i < collection.size(); i++) {
            gotEntities.add((GoTEntity) collection.toArray()[i]);
        }
        gotEntitiesFiltered = gotEntities;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 1) {
            itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_character_row, parent, false);
        }
        else {
            itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_house_row, parent, false);
        }
        return new GotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final GotViewHolder gotViewHolder = (GotViewHolder) holder;
        gotViewHolder.render(gotEntitiesFiltered.get(position));
        View.OnClickListener listener;
        if (type == GoTListFragment.ListType.Characters) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(gotViewHolder.itemView.getContext(), DetailActivity.class);
                    GoTCharacter gotChar = ((GoTCharacter) gotEntitiesFiltered.get(holder.getAdapterPosition()));
                    intent.putExtra("description", gotChar.getDescription());
                    intent.putExtra("name", gotChar.getName());
                    intent.putExtra("imageUrl", gotChar.getImageUrl());
                    gotViewHolder.itemView.getContext().startActivity(intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        callingFragment.getActivity().overridePendingTransition(R.anim.bounce, R.anim.bounce);
                    }
                }
            };
        } else {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoTCharacter.GoTHouse gotHouse = (GoTCharacter.GoTHouse) gotEntitiesFiltered.get(holder.getAdapterPosition());
                    GoTListFragment listFragment = GoTListFragment.newInstance(GoTListFragment.ListType.Characters, gotHouse.getHouseId());
                    FragmentManager cfm = callingFragment.getChildFragmentManager();
                    cfm.popBackStack();
                    cfm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top)
                            .add(R.id.fragment_list, listFragment)
                            .commitAllowingStateLoss();

                }
            };
        }
        gotViewHolder.imageView.setOnClickListener(listener);
        gotViewHolder.render(gotEntitiesFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return gotEntitiesFiltered.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (type == GoTListFragment.ListType.Characters) ? 1 : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    gotEntitiesFiltered = gotEntities;
                } else {
                    List<GoTEntity> filterList = new ArrayList<>();
                    for (GoTEntity item : gotEntities) {
                        String name;
                        if (type == GoTListFragment.ListType.Characters) {
                            name = ((GoTCharacter) item).getName();
                        } else {
                            name = ((GoTCharacter.GoTHouse) item).getHouseName();
                        }

                        if (name.toLowerCase().contains(charString.toLowerCase())) {
                            filterList.add(item);
                        }
                    }
                    gotEntitiesFiltered = filterList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = gotEntitiesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                gotEntitiesFiltered = (List<GoTEntity>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class GotViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "GotViewHolder";
        ImageView imageView;
        TextView tvName;
        int type;

        public GotViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivBackground);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            type = GoTListAdapter.this.getItemViewType(this.getAdapterPosition());
        }

        public void render(final GoTEntity gotEntity) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = GoTEntityUtils.getGoTEntityImage(gotEntity);
                        final Uri uri = Uri.parse(url.toString());
                        final int placeholder;
                        String name;
                        if (type == 1) {
                            placeholder = R.mipmap.got_characters;
                            name = ((GoTCharacter) gotEntity).getName();
                        } else {
                            placeholder = R.mipmap.got_houses;
                            name = ((GoTCharacter.GoTHouse) gotEntity).getHouseName();
                        }
                        final String entityName = name;
                        final Activity activity = callingFragment.getActivity();

                        final Picasso picasso = new Picasso.Builder(activity).listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                                GoTDataSource.getRandomPlaceholder(entityName, new GoTResultsInterface() {
                                    @Override
                                    public void onResult(final String result) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Picasso.with(activity)
                                                        .load(result)
                                                        .fit()
                                                        .centerCrop()
                                                        .placeholder(placeholder)
                                                        .into(imageView);
                                            }
                                        });
                                    }
                                });
                            }
                        }).build();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                picasso.load(uri)
                                        .fit()
                                        .centerCrop()
                                        .placeholder(placeholder)
                                        .into(imageView);
                                tvName.setText(entityName);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }).start();
        }
    }
}
