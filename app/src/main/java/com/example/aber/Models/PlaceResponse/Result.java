package com.example.aber.Models.PlaceResponse;

import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.model.Review;

import java.util.ArrayList;

public class Result {
    private ArrayList<Object> addressComponents = new ArrayList<>();
    private String adrAddress;
    private String businessStatus;
    private CurrentOpeningHours currentOpeningHours;
    private String formattedAddress;
    private String formattedPhoneNumber;
    private Geometry geometry;
    private String icon;
    private String iconBackgroundColor;
    private String iconMaskBaseUri;
    private String internationalPhoneNumber;
    private String name;
    private OpeningHours openingHours;
    private ArrayList<Object> photos = new ArrayList<>();
    private String placeId;
    private PlusCode plusCode;
    private float rating;
    private String reference;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Object> types = new ArrayList<>();
    private String url;
    private float userRatingsTotal;
    private float utcOffset;
    private String vicinity;
    private String website;
    private boolean wheelchairAccessibleEntrance;

    public ArrayList<Object> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(ArrayList<Object> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public CurrentOpeningHours getCurrentOpeningHours() {
        return currentOpeningHours;
    }

    public void setCurrentOpeningHours(CurrentOpeningHours currentOpeningHours) {
        this.currentOpeningHours = currentOpeningHours;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public void setIconBackgroundColor(String iconBackgroundColor) {
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public String getIconMaskBaseUri() {
        return iconMaskBaseUri;
    }

    public void setIconMaskBaseUri(String iconMaskBaseUri) {
        this.iconMaskBaseUri = iconMaskBaseUri;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public ArrayList<Object> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Object> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Object> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Object> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(float userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public float getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(float utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isWheelchairAccessibleEntrance() {
        return wheelchairAccessibleEntrance;
    }

    public void setWheelchairAccessibleEntrance(boolean wheelchairAccessibleEntrance) {
        this.wheelchairAccessibleEntrance = wheelchairAccessibleEntrance;
    }
}

