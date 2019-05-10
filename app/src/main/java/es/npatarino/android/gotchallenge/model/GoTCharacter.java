package es.npatarino.android.gotchallenge.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nicolás Patarino on 21/02/16.
 */
public class GoTCharacter {

    @SerializedName("name")
    private String name;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("description")
    private String description;
    @SerializedName("houseImageUrl")
    private String houseImageUrl;
    @SerializedName("houseName")
    private String houseName;
    @SerializedName("houseId")
    private String houseId;

    public String getHouseImageUrl() {
        return houseImageUrl;
    }

    public void setHouseImageUrl(final String s) {
        this.houseImageUrl = s;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(final String s) {
        this.houseName = s;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(final String s) {
        this.houseId = s;
    }

    public String getName() {
        return name;
    }

    public void setName(final String s) {
        this.name = s;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String s) {
        this.imageUrl = s;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String s) {
        this.description = s;
    }

    /**
     * Created by Nicolás Patarino on 21/02/16.
     */
    public static class GoTHouse {

        @SerializedName("houseImageUrl")
        String houseImageUrl;
        @SerializedName("houseName")
        String houseName;
        @SerializedName("houseId")
        String houseId;

        public String getHouseImageUrl() {
            return houseImageUrl;
        }

        public void setHouseImageUrl(final String houseImageUrl) {
            this.houseImageUrl = houseImageUrl;
        }

        public String getHouseName() {
            return houseName;
        }

        public void setHouseName(final String houseName) {
            this.houseName = houseName;
        }

        public String getHouseId() {
            return houseId;
        }

        public void setHouseId(final String houseId) {
            this.houseId = houseId;
        }

    }
}
