package es.npatarino.android.gotchallenge.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.model.GoTCharacter;

public class GoTHouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<GoTCharacter.GoTHouse> gcs;
    private Activity activity;

    public GoTHouseAdapter(Activity delegateActivity) {
        this.gcs = new ArrayList<>();
        activity = delegateActivity;
    }

    public void addAll(Collection<GoTCharacter.GoTHouse> collection) {
        for (int i = 0; i < collection.size(); i++) {
            gcs.add((GoTCharacter.GoTHouse) collection.toArray()[i]);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GotCharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.got_house_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        GotCharacterViewHolder gotCharacterViewHolder = (GotCharacterViewHolder) holder;
        gotCharacterViewHolder.render(gcs.get(position));
    }

    @Override
    public int getItemCount() {
        return gcs.size();
    }

    class GotCharacterViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotCharacterViewHolder";
        ImageView imageView;

        public GotCharacterViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivBackground);
        }

        public void render(final GoTCharacter.GoTHouse goTHouse) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL(goTHouse.getHouseImageUrl());
                        final Uri uri = Uri.parse(url.toString());
                        //final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //imageView.setImageBitmap(bmp);
                                Picasso.with(activity).load(uri).placeholder(R.mipmap.got_houses).into(imageView);
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
