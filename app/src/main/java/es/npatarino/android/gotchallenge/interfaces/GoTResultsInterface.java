package es.npatarino.android.gotchallenge.interfaces;

import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTEntity;

public abstract class GoTResultsInterface implements GoTResults {
    @Override
    public void onSuccess(List<GoTEntity> entities) {

    }

    @Override
    public void onResult(String result) {

    }

    @Override
    public void onFailure() {

    }
}
