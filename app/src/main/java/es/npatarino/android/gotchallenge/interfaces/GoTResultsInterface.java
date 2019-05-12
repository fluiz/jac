package es.npatarino.android.gotchallenge.interfaces;

import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTEntity;

public interface GoTResultsInterface {
    void onSuccess(List<GoTEntity> entities);
    void onFailure();
}
