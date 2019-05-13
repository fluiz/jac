package es.npatarino.android.gotchallenge.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import es.npatarino.android.gotchallenge.interfaces.GoTResultsInterface;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;

public class GoTEntityUtils {

    private static String TAG = "GoTEntityUtils";

    @NonNull
    public static URL getGoTEntityImage(GoTEntity gotEntity) throws MalformedURLException {
        URL url = null;

        if (gotEntity instanceof GoTCharacter) {
            GoTCharacter gotChar = (GoTCharacter) gotEntity;
            url = new URL(gotChar.getImageUrl());
        } else {
            GoTCharacter.GoTHouse goTHouse = (GoTCharacter.GoTHouse) gotEntity;
            url = new URL(goTHouse.getHouseImageUrl());
        }

        return url;
    }

    @NonNull
    public static List<GoTEntity> filterCharactersByHouse(List<GoTEntity> characters, String houseId) {
        List<GoTEntity> filteredCharacters = new ArrayList<>();

        for (int index = 0; index < characters.size(); index++) {
            GoTCharacter currentChar = (GoTCharacter) characters.get(index);
            if (currentChar.getHouseId().equals(houseId)) {
                filteredCharacters.add(currentChar);
            }
        }

        return filteredCharacters;
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
            doc = Jsoup.connect(searchQuery).timeout(1000).get();

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
