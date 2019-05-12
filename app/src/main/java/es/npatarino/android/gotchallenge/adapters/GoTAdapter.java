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

import es.npatarino.android.gotchallenge.ui.activities.DetailActivity;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.model.GoTCharacter;

public class GoTAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<GoTCharacter> gcs;
    private Activity activity;

    public GoTAdapter(Activity delegateActivity) {
        this.gcs = new ArrayList<>();
        activity = delegateActivity;
    }

    /*public void addAll(Collection<GoTCharacter> collection) {
        for (int i = 0; i < collection.size(); i++) {
            gcs.add((GoTCharacter) collection.toArray()[i]);
        }
    }*/

    //Done
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GotCharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.got_character_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        GotCharacterViewHolder gotCharacterViewHolder = (GotCharacterViewHolder) holder;
        gotCharacterViewHolder.render(gcs.get(position));
        ((GotCharacterViewHolder) holder).characterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(((GotCharacterViewHolder) holder).itemView.getContext(), DetailActivity.class);
                intent.putExtra("description", gcs.get(position).getDescription());
                intent.putExtra("name", gcs.get(position).getName());
                intent.putExtra("imageUrl", gcs.get(position).getImageUrl());
                ((GotCharacterViewHolder) holder).itemView.getContext().startActivity(intent);
            }
        });
    }

    //Done
    @Override
    public int getItemCount() {
        return gcs.size();
    }

    class GotCharacterViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "GotCharacterViewHolder";
        ImageView characterImage;
        TextView tvName;

        public GotCharacterViewHolder(View itemView) {
            super(itemView);
            characterImage = (ImageView) itemView.findViewById(R.id.ivBackground);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void render(final GoTCharacter goTCharacter) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL(goTCharacter.getImageUrl());
                        final Uri uri = Uri.parse(url.toString());
                        //final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //characterImage.setImageBitmap(bmp);
                                Picasso.with(activity).load(uri).placeholder(R.mipmap.got_characters).into(characterImage);
                                tvName.setText(goTCharacter.getName());
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
