package com.truckdriverco.truckdriver.MapClusterModel;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.truckdriverco.truckdriver.Location.UserFlourMill;

public class ClusterMarkerFlourMill implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private UserFlourMill user;

    public ClusterMarkerFlourMill(LatLng position, String title, String snippet, int iconPicture, UserFlourMill user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.user = user;
    }


    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public UserFlourMill getUser() {
        return user;
    }

    public void setUser(UserFlourMill user) {
        this.user = user;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
