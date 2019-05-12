package es.npatarino.android.gotchallenge.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;
import es.npatarino.android.gotchallenge.ui.fragments.GoTListFragment;

public class GoTDataSource {

    final private static String TAG = "GoTDataSource";
    final private static String apiUrl = "https://project-8424324399725905479.firebaseio.com/characters.json?print=pretty";

    @NonNull
    public static void getCharacters(@NonNull GoTResultsInterface callback) {
        Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
        }.getType();

        getDataList(listType, GoTListFragment.ListType.Characters, callback);

        /*List<GoTEntity> characters = new ArrayList<>();
        try {
            characters = getDataList(listType).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return characters;*/
    }

    @NonNull
    public static void getHouses(@NonNull GoTResultsInterface callback) {
        Type listType = new TypeToken<ArrayList<GoTCharacter.GoTHouse>>() {
        }.getType();

        getDataList(listType, GoTListFragment.ListType.Houses, callback);

        /*List<GoTEntity> houses = new ArrayList<>();
        try {
            houses = getDataList(listType).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return houses;*/
    }

    private static void getDataList(@NonNull final Type classType, @NonNull final GoTListFragment.ListType entityType, @NonNull final GoTResultsInterface callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                URL obj = null;
                try {
                    obj = new URL(apiUrl);
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
                    }.getType();
                    List<GoTEntity> entitiesList = new Gson().fromJson(response.toString(), listType);
                    //entities.complete(entitiesList);
                    
                    if (entityType == GoTListFragment.ListType.Houses) {
                        ArrayList<GoTEntity> hs = new ArrayList<>();
                        for (int i = 0; i < entitiesList.size(); i++) {
                            GoTCharacter character = (GoTCharacter) entitiesList.get(i);
                            boolean b = false;
                            for (int j = 0; j < hs.size(); j++) {
                                GoTCharacter.GoTHouse gotHouse = (GoTCharacter.GoTHouse) hs.get(j);
                                if (gotHouse.getHouseName().equalsIgnoreCase(character.getHouseName())) {
                                    b = true;
                                }
                            }
                            if (!b) {
                                if (character.getHouseId() != null && !character.getHouseId().isEmpty()) {
                                    GoTCharacter.GoTHouse h = new GoTCharacter.GoTHouse();
                                    h.setHouseId(character.getHouseId());
                                    h.setHouseName(character.getHouseName());
                                    h.setHouseImageUrl(character.getHouseImageUrl());
                                    hs.add(h);
                                    b = false;
                                }
                            }
                        }
                        entitiesList = hs;
                    }
                    
                    callback.onSuccess(entitiesList);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    callback.onFailure();
                }
            }
        }).start();
    }

    /*private static CompletableFuture<List<GoTEntity>> getDataList(final Type listType) {
        final CompletableFuture<List<GoTEntity>> entities = new CompletableFuture<>();

        new Thread(new Runnable() {

            @Override
            public void run() {
                URL obj = null;
                try {
                    obj = new URL(apiUrl);
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
                    }.getType();
                    List<GoTEntity> entitiesList = new Gson().fromJson(response.toString(), listType);
                    entities.complete(entitiesList);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();

        return entities;
    }*/
}
