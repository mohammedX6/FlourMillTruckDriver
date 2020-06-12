package com.truckdriverco.truckdriver.CallBacks;


import com.truckdriverco.truckdriver.Location.UserLocation;

import java.util.List;

public interface FirestoreCallBack {

    void onCallBack(List<UserLocation> userLocations);

}
