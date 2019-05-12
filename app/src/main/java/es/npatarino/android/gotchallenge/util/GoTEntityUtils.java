package es.npatarino.android.gotchallenge.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;

public class GoTEntityUtils {

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

    @Nullable
    public static String getGoTEntityName(GoTEntity gotEntity) {
        String name = null;

        if (gotEntity instanceof GoTCharacter) {
            GoTCharacter gotChar = (GoTCharacter) gotEntity;
            name = gotChar.getName();
        } else {
            GoTCharacter.GoTHouse goTHouse = (GoTCharacter.GoTHouse) gotEntity;
            name = goTHouse.getHouseName();
        }

        return name;
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
}
