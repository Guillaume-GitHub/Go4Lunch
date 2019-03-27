package com.android.guillaume.go4launch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestoResult {
    @SerializedName("restoGeometry")
    @Expose
    private RestoGeometry restoGeometry;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private RestoOpeningHours restoOpeningHours;
    @SerializedName("restoPhotos")
    @Expose
    private List<RestoPhoto> restoPhotos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("plus_code")
    @Expose
    private RestoPlusCode restoPlusCode;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("price_level")
    @Expose
    private Integer priceLevel;

    public RestoGeometry getRestoGeometry() {
        return restoGeometry;
    }

    public void setRestoGeometry(RestoGeometry restoGeometry) {
        this.restoGeometry = restoGeometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestoOpeningHours getRestoOpeningHours() {
        return restoOpeningHours;
    }

    public void setRestoOpeningHours(RestoOpeningHours restoOpeningHours) {
        this.restoOpeningHours = restoOpeningHours;
    }

    public List<RestoPhoto> getRestoPhotos() {
        return restoPhotos;
    }

    public void setRestoPhotos(List<RestoPhoto> restoPhotos) {
        this.restoPhotos = restoPhotos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public RestoPlusCode getRestoPlusCode() {
        return restoPlusCode;
    }

    public void setRestoPlusCode(RestoPlusCode restoPlusCode) {
        this.restoPlusCode = restoPlusCode;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(Integer priceLevel) {
        this.priceLevel = priceLevel;
    }

}
