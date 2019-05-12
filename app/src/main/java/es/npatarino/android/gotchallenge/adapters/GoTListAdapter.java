package es.npatarino.android.gotchallenge.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;
import es.npatarino.android.gotchallenge.ui.activities.DetailActivity;
import es.npatarino.android.gotchallenge.ui.activities.HomeActivity;
import es.npatarino.android.gotchallenge.ui.fragments.GoTListFragment;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.util.GoTEntityUtils;

/**
 * Created by Admin on 15/12/2016.
 */

public class GoTListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    GoTListFragment.ListType type;
    Context context;

   private List<GoTEntity> gotEntities;

    public GoTListAdapter(GoTListFragment.ListType type, Context context){
        this.type = type;
        this.context = context;
        gotEntities = new ArrayList<>();
    }

    public void addAll(Collection<GoTEntity> collection) {
        for (int i = 0; i < collection.size(); i++) {
            gotEntities.add((GoTEntity) collection.toArray()[i]);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 1) {
            itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_character_row, parent, false);

            /*
          GotCharacterViewHolder vh = new GotCharacterViewHolder (itemView);
            return vh;
            */
        }
        else {
            itemView = LayoutInflater.from (parent.getContext()).
                    inflate (R.layout.got_house_row, parent, false);

            /*
          GotHouseViewHolder vh = new GotHouseViewHolder (itemView);
            return vh;
            */
        }
        return new GotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final GotViewHolder gotViewHolder = (GotViewHolder) holder;
        gotViewHolder.render(gotEntities.get(position));
        View.OnClickListener listener;
        if (type == GoTListFragment.ListType.Characters) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(gotViewHolder.itemView.getContext(), DetailActivity.class);
                    GoTCharacter gotChar = ((GoTCharacter) gotEntities.get(holder.getAdapterPosition()));
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
                            .replace(R.id.container, listFragment)
                            .commitAllowingStateLoss();
                }
            };
        }
        gotViewHolder.imageView.setOnClickListener(listener);
        gotViewHolder.render(gotEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return gotEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (type == GoTListFragment.ListType.Characters) ? 1 : 0;
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
                        //final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        final Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //characterImage.setImageBitmap(bmp);
                                int placeholder;
                                String name;
                                if (type == 1) {
                                    placeholder = R.mipmap.got_characters;
                                    name = ((GoTCharacter) gotEntity).getName();
                                } else {
                                    placeholder = R.mipmap.got_houses;
                                    name = ((GoTCharacter.GoTHouse) gotEntity).getHouseName();
                                }
                                Picasso.with(activity).load(uri).placeholder(placeholder).into(imageView);
                                tvName.setText(name);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }).start();
        }
    }

    /*
    class GotCharacterViewHolder extends GotViewHolder {

        private static final String TAG = "GotCharacterViewHolder";
        public GotCharacterViewHolder(View itemView) {
            super(itemView);
        }
    }

    class GotHouseViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotHouseViewHolder";
        public GotHouseViewHolder(View itemView) {
            super(itemView);
        }
    }
    */
}
