package es.npatarino.android.gotchallenge.interfaces;

import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTEntity;

public interface GoTResults {
    void onSuccess(List<GoTEntity> entities);
    void onResult(String result);
    void onFailure();
}
