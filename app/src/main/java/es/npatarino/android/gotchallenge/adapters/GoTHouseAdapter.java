package es.npatarino.android.gotchallenge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.ui.activities.HomeActivity;

public class GoTHouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<GoTCharacter.GoTHouse> gotHousesList;
    private Activity activity;

    public GoTHouseAdapter(Activity delegateActivity) {
        this.gotHousesList = new ArrayList<>();
        activity = delegateActivity;
    }

    public void addAll(Collection<GoTCharacter.GoTHouse> collection) {
        for (int i = 0; i < collection.size(); i++) {
            gotHousesList.add((GoTCharacter.GoTHouse) collection.toArray()[i]);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GotHouseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.got_house_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        GotHouseViewHolder gotCharacterViewHolder = (GotHouseViewHolder) holder;
        gotCharacterViewHolder.render(gotHousesList.get(position));
        ((GotHouseViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(((GotHouseViewHolder) holder).imageView.getContext(), HomeActivity.class);
                intent.putExtra("house", gotHousesList.get(position).getHouseId());
                ((GotHouseViewHolder) holder).imageView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gotHousesList.size();
    }

    class GotHouseViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotHouseViewHolder";
        ImageView imageView;
        TextView tvHouseName;

        public GotHouseViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivBackground);
            tvHouseName = (TextView) itemView.findViewById((R.id.tv_name));
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
                                tvHouseName.setText(goTHouse.getHouseName());
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
