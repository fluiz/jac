package es.npatarino.android.gotchallenge.api;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import es.npatarino.android.gotchallenge.db.GoTDataBase;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterfaceImpl;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;
import es.npatarino.android.gotchallenge.ui.fragments.GoTListFragment;

public class GoTDataSource {

    final private static String TAG = "GoTDataSource";
    final private static String apiUrl = "https://project-8424324399725905479.firebaseio.com/characters.json?print=pretty";

    @NonNull
    public static void getCharacters(@NonNull final Context context, @NonNull final GoTResultsInterface callback) {
        Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
        }.getType();

        getDataList(listType, GoTListFragment.ListType.Characters, new GoTResultsInterfaceImpl() {
            @Override
            public void onSuccess(List<GoTEntity> entities) {
                GoTDataBase dataBase = GoTDataBase.getInstance(context);
                dataBase.saveCharactersList(entities);
                callback.onSuccess(entities);
            }

            @Override
            public void onFailure() {
                GoTDataBase dataBase = GoTDataBase.getInstance(context);
                callback.onSuccess(dataBase.getAllCharacters());
            }
        });
    }

    @NonNull
    public static void getHouses(@NonNull final Context context, @NonNull final GoTResultsInterface callback) {
        Type listType = new TypeToken<ArrayList<GoTCharacter.GoTHouse>>() {
        }.getType();

        getDataList(listType, GoTListFragment.ListType.Houses, new GoTResultsInterfaceImpl() {
            @Override
            public void onSuccess(List<GoTEntity> entities) {
                GoTDataBase dataBase = GoTDataBase.getInstance(context);
                dataBase.saveHousesList(entities);
                callback.onSuccess(entities);
            }

            @Override
            public void onFailure() {
                GoTDataBase dataBase = GoTDataBase.getInstance(context);
                callback.onSuccess(dataBase.getAllHouses());
            }
        });
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
                    con.setConnectTimeout(1000);
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

    @Nullable
    public static void getRandomPlaceholder(String characterName, GoTResultsInterface callback) {
        String searchUrl1 = "https://www.google.com/search?biw=1871&bih=1289&tbm=isch&sa=1&ei=zE_YXJPyJobF5OUP0O2pqAo&q=";
        String searchUrl2 = "&gs_l=img.3..0l3j0i67j0l6.4635886.4644329..4644770...0.0..0.474.1043.0j5j4-1......0....1..gws-wiz-img.......0i10i24.rlM5B4Tz-ZU";
        String name = characterName.replace(" ", "+");
        String toSearch = searchUrl1 + name + searchUrl2;

        getImageUrl(toSearch, callback);
    }

    @Nullable
    private static void getImageUrl(final String url, final GoTResultsInterface callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String result = getUrl(url);
                if (result != null) {
                    callback.onResult(result);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    @Nullable
    private static String getUrl(String searchQuery) {
        int min = 0;
        int max = 3;
        String imgUrl = null;

        Document doc = null;
        try {
            doc = Jsoup.connect(searchQuery).timeout(2000).get();

            Elements elemnts = doc.select("img");
            ArrayList<String> imgs = new ArrayList<>();
            for (Element el : elemnts) {
                String candidate = el.attr("data-src");
                if (!candidate.isEmpty()) {
                    imgs.add(el.attr("data-src"));
                }
                if (imgs.size() >= max) break;
            }
            Random ran = new Random();
            int random = ran.nextInt(imgs.size()-min) + min;
            imgUrl = imgs.get(random);
            //Log.i("TAG","Random url: "+ imgUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return imgUrl;
    }
}
