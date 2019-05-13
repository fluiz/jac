package es.npatarino.android.gotchallenge.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.api.GoTDataSource;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;

public class DetailActivity extends AppCompatActivity {


    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final AppCompatActivity thisActivity = this;

        FrameLayout fl = findViewById(R.id.detail_container);
        final ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) findViewById(R.id.pb);

        final ImageView imageView = (ImageView) findViewById(R.id.iv_photo);
        final TextView tvn = (TextView) findViewById(R.id.tv_name);
        final TextView tvd = (TextView) findViewById(R.id.tv_description);

        final String description = getIntent().getStringExtra("description");
        final String name = getIntent().getStringExtra("name");
        final String imageUrl = getIntent().getStringExtra("imageUrl");

        Toolbar toolbar = (Toolbar) findViewById(R.id.t);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.show();
                try {
                    GoTDataSource.getRandomPlaceholder(name, new GoTResultsInterface() {
                        @Override
                        public void onResult(String result) {
                            final Uri uri = Uri.parse(result);
                            DetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.with(thisActivity).load(uri).placeholder(R.mipmap.got_poster).into(imageView);
                                    tvn.setText(name);
                                    tvd.setText(description);
                                    progressBar.hide();
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            super.onFailure();
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();
    }
}
