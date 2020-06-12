package com.truckdriverco.truckdriver;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chootdev.recycleclick.RecycleClick;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.truckdriverco.truckdriver.API_Calls.GeoLocationClient;
import com.truckdriverco.truckdriver.API_Calls.OrderClient;
import com.truckdriverco.truckdriver.CallBacks.FirestoreCallBack;
import com.truckdriverco.truckdriver.CallBacks.FirestoreCallBackBakery;
import com.truckdriverco.truckdriver.CallBacks.FirestoreCallBackFlourMill;
import com.truckdriverco.truckdriver.Location.FlourMillsLocation;
import com.truckdriverco.truckdriver.Location.PolylineData;
import com.truckdriverco.truckdriver.Location.User;
import com.truckdriverco.truckdriver.Location.UserLocation;
import com.truckdriverco.truckdriver.MapClusterModel.ClusterMarker;
import com.truckdriverco.truckdriver.MapClusterModel.ClusterMarkerFlourMill;
import com.truckdriverco.truckdriver.MapClusterModel.MyClusterManagerRenderer;
import com.truckdriverco.truckdriver.MapClusterModel.MyClusterManagerRendererFlourMill;
import com.truckdriverco.truckdriver.Model.FinishOrder;
import com.truckdriverco.truckdriver.Model.Order;
import com.truckdriverco.truckdriver.Model.UserOrder;
import com.truckdriverco.truckdriver.RecycleViews.RecycleViewAdapterOrderHistory;
import com.truckdriverco.truckdriver.util.ViewWeightAnimationWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.truckdriverco.truckdriver.Constants.MAPVIEW_BUNDLE_KEY;


public class NewOrdersFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnPolylineClickListener {
    private static final int LOCATION_UPDATE_INTERVAL = 3000;
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    RecycleViewAdapterOrderHistory orderHistoryAdapter;
    Retrofit retrofit;
    Fragment selected;
    OrderClient orderClient;
    GeoLocationClient geoLocationClient;
    LinearLayoutManager layoutManager;
    List<Order> orderList;
    Bundle bundle2;
    MapView mMapView;
    ImageView finsihV;
    FirebaseFirestore mDB;
    UserLocation userLocation;
    FirebaseAuth firebaseAuth;
    FusedLocationProviderClient fusedLocationProviderClient;
    int bid = 0;
    int aid = 0;
    String latitude;
    String longtude;
    View v;
    String durationD;
    String distanceD;
    TextView itemCount, DurationT, DistanceT, AddressT;
    String globalLatitude;
    String globalLongitude;
    View wizardView;
    private Handler handler = new Handler();
    private int MapLayoutState = 0;
    private GoogleMap googleMap;
    private ArrayList<FlourMillsLocation> FlourMillsAfterQuery = new ArrayList<>();
    private ArrayList<UserLocation> LocationsList = new ArrayList<>();
    private ArrayList<UserLocation> LocationsListBakery = new ArrayList<>();
    private ArrayList<FlourMillsLocation> FlourMills = new ArrayList<>();
    private ArrayList<ClusterMarker> markerClusters = new ArrayList<>();
    private ArrayList<ClusterMarker> markersBakerys = new ArrayList<>();
    private ArrayList<ClusterMarkerFlourMill> markerClustersFlourMills = new ArrayList<>();
    private LatLngBounds Boundery;
    private UserLocation userPosition;
    private List<UserLocation> temp1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences p3;
    private SharedPreferences.Editor editor;
    private ClusterManager TruckDriverCluster;
    private ClusterManager FlourMillCluster;
    private ClusterManager BakeryCluster;
    private MyClusterManagerRenderer clusterManagerRenderer;
    private MyClusterManagerRenderer BakeryRenderer;
    private MyClusterManagerRendererFlourMill RendererCluster;
    private Runnable runnable;
    private RelativeLayout mapContianer;
    private GeoApiContext geoApiContext = null;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private String mParam1;
    private String mParam2;
    private int newOrderId = 0;
    private Marker SelectedMaker = null;
    private ArrayList<Marker> TripMakers = new ArrayList<>();

    private double newLat, newLng, newWaypointLat, newWayPointLng;
ImageButton kl;
    public NewOrdersFragment() {

    }

    public static NewOrdersFragment newInstance(String param1, String param2) {


        NewOrdersFragment fragment = new NewOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mDB = FirebaseFirestore.getInstance();
        recyclerView = getActivity().findViewById(R.id.recyclerview90);
        finsihV = getActivity().findViewById(R.id.fisnish);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("location", MODE_PRIVATE);
        p3 = getActivity().getSharedPreferences("secretcode", MODE_PRIVATE);
        Log.d("importent ", p3.getString("token", ""));
        editor = sharedPreferences.edit();
        mapContianer = getActivity().findViewById(R.id.map_container);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging).
                        connectTimeout(5, TimeUnit.MINUTES).
                        writeTimeout(5, TimeUnit.MINUTES).
                        readTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://floutmill-jo.azurewebsites.net/").
                addConverterFactory(GsonConverterFactory.create()).client(client);
        retrofit = builder.build();
        orderClient = retrofit.create(OrderClient.class);
        geoLocationClient = retrofit.create(GeoLocationClient.class);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    private void getOrders() {
        try {


            Call<List<Order>> call = orderClient.getAllOrders("Bearer " + p3.getString("token", ""));
            call.enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {

                    if (response.isSuccessful()) {
                        orderList = response.body();
                        Log.d("rCode", response.code() + "");

                        if (orderList.isEmpty()) {
                            Toasty.success(getActivity(), "Sorry you don't have any orders yet. ", Toast.LENGTH_SHORT).show();


                        } else {

                            for (Order f : orderList) {

                                Log.d("getas", f.getAdministratorID() + " hi");

                            }
                            orderHistoryAdapter = new RecycleViewAdapterOrderHistory(getActivity(), orderList);
                            recyclerView.setAdapter(orderHistoryAdapter);

                        }

                    } else
                        Toasty.error(getActivity(), "Orders history not Loaded  ", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Error");
        }
    }

    private void getLastKnownLocation() {
        try {


            Log.d(TAG, "getLastKnownLocation: called.");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    if (task.isSuccessful()) {

                        try {


                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                        Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                        userLocation.setGeo_point(geoPoint);
                        userLocation.setTimestamp(null);
                        saveUserLocation();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Error");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_orders, container, false);
        recyclerView = v.findViewById(R.id.recyclerview90);
        kl=  v.findViewById(R.id.fullscreen);
        kl.setOnClickListener(this);
        v.findViewById(R.id.fisnish).setOnClickListener(this);
        v.findViewById(R.id.resetmap).setOnClickListener(this);

        mapContianer = v.findViewById(R.id.map_container);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderHistoryAdapter);
        RecycleClick.addTo(recyclerView).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (orderList.get(position).getOrderStatues() == 2) {
                    Toasty.info(getContext(), "Order in Progress !");

                }
                if (orderList.get(position).getOrderStatues() == 3) {
                    Toasty.info(getContext(), "Order Delivered !");
                } else {
                    try {


                        bundle2 = new Bundle();
                        bundle2.putInt("orderId", orderList.get(position).getId());
                        bundle2.putString("payment", String.valueOf(orderList.get(position).getTotalPayment()));
                        bundle2.putString("tons", String.valueOf(orderList.get(position).getTotalTons()));
                        bundle2.putString("customer", orderList.get(position).getCustomerName());
                        bundle2.putString("adminid", String.valueOf(orderList.get(position).getAdministratorID()));
                        bundle2.putString("bakeryid", String.valueOf(orderList.get(position).getBakeryID()));
                        bundle2.putString("comment", orderList.get(position).getOrderComment());
                        bundle2.putString("truckid", orderList.get(position).getTruckDriverID());
                        bundle2.putString("date", String.valueOf(orderList.get(position).getOrder_Date()));
                        bundle2.putString("statues", String.valueOf(orderList.get(position).getOrderStatues()));
                        bundle2.putString("destination", orderList.get(position).getDestination());
                        bundle2.putInt("shipment", orderList.get(position).getShipmentPrice());

                        Log.d("kil", String.valueOf(orderList.get(position).getAdministratorID()));
                        Intent myIntent = new Intent(getContext(), OrderHistoryDetailsActivity.class);
                        myIntent.putExtras(bundle2);
                        startActivityForResult(myIntent, 100);
                    } catch (Exception e) {
                        Log.d(TAG, "Error");
                    }
                }
            }
        });
        mMapView = v.findViewById(R.id.user_list_map);


        initGoogleMap(savedInstanceState);
        getUserDetails();
        getOrders();

        return v;
    }

    private void getUserDetails() {
        try {


            if (userLocation == null) {
                userLocation = new UserLocation();
                DocumentReference userRef = mDB.collection("Users")
                        .document(FirebaseAuth.getInstance().getUid());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successfully set the user client.");
                            User user = task.getResult().toObject(User.class);
                            userLocation.setUser(user);
                            Log.d(TAG, "onComplete: successfully set the user client." + firebaseAuth.getUid());
                            getLastKnownLocation();
                        }
                    }
                });
            } else {
                getLastKnownLocation();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error");
        }
    }


    private void saveUserLocation() {

        try {
            if (userLocation != null) {
                DocumentReference locationRef = mDB.collection("user_locations").document(FirebaseAuth.getInstance().getUid());

                locationRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "latitude: " + userLocation.getGeo_point().getLatitude() +
                                    " longitude: " + userLocation.getGeo_point().getLongitude());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        try {
            mapViewBundle = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_map_api_key)).build();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = null;
        try {
            mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {

        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {


        googleMap = map;

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {

                addMapMakerers();
                addMapMakerersFlourMill();
                addMapMakerersBakerys();

            }
        }, 1500);

        googleMap.setOnInfoWindowLongClickListener(this);
        googleMap.setOnPolylineClickListener(this);


        googleMap.setMyLocationEnabled(true);


        try {


            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Call<JsonElement> call2 = orderClient.ReturnPreviousJob("Bearer " + p3.getString("token", ""));
                    call2.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            Log.d("codetrucke", response.code() + "");
                            if (response.code() == 400) {

                            } else {

                                try {


                                    recyclerView.setVisibility(View.GONE);
                                    LinearLayout dynamicContent = getActivity().findViewById(R.id.dynamic_content);
                                    dynamicContent.setVisibility(View.VISIBLE);

                                    wizardView = getLayoutInflater()
                                            .inflate(R.layout.afterviewf, dynamicContent, false);

                                    dynamicContent.addView(wizardView);


                                    ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContianer);
                                    ObjectAnimator mapAnimantion = ObjectAnimator.ofFloat(mapAnimationWrapper, "weight", 50, 150);
                                    mapAnimantion.setDuration(200);

                                    mapAnimantion.start();
                                    addLisnter();


                                    itemCount = wizardView.findViewById(R.id.itemcount3);
                                    DurationT = wizardView.findViewById(R.id.duration2);
                                    DistanceT = wizardView.findViewById(R.id.distanceroad2);
                                    AddressT = wizardView.findViewById(R.id.distanceaddress2);


                                    mapAnimantion.start();


                                    aid = Integer.parseInt(response.body().getAsJsonObject().get("adminid").getAsString());
                                    bid = Integer.parseInt(response.body().getAsJsonObject().get("bakeryid").getAsString());
                                    newOrderId = Integer.parseInt(response.body().getAsJsonObject().get("orderID").getAsString());
                                    String destination = response.body().getAsJsonObject().get("dest").getAsString();
                                    StartpolyAfterResult();

                                    AddressT.setText("Delivery Address: " + destination);
                                    //  wizardView.findViewById(R.id.findmap).setOnClickListener(this);
                                 /*   Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            itemCount.setText("Item Count: " + 2);
                                            DurationT.setText("Duration: " + durationD);
                                            DistanceT.setText("Disatance: " + distanceD);

                                        }
                                    }, 4000);*/
                                } catch (Exception e) {

                                }
                                kl.performClick();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {

                        }
                    });


                }
            }, 4000);
        } catch (Exception e) {
            Log.d(TAG, "ERROR");
        }


    }

    void addLisnter() {
        wizardView.findViewById(R.id.findmap).setOnClickListener(this);

    }


    @Override
    public void onPause() {
        mMapView.onPause();
        stopLocationUpdates();
        super.onPause();

    }

    private void stopLocationUpdates() {
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void readData(FirestoreCallBack firestoreCallBack) {

        try {

            CollectionReference c = mDB.collection("user_locations");
            c.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot doc : task.getResult()) {
                        UserLocation updaed = doc.toObject(UserLocation.class);
                        LocationsList.add(updaed);
                    }

                    firestoreCallBack.onCallBack(LocationsList);
                    //   firestoreCallBack
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void readDataFlourMills(FirestoreCallBackFlourMill firestoreCallBackFlourMill) {

        try {
            CollectionReference c = mDB.collection("flourmill_location");
            c.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot doc : task.getResult()) {
                        FlourMillsLocation updaed = doc.toObject(FlourMillsLocation.class);
                        Log.d("clusterfsf", updaed.getLocation() + "");
                        Log.d("clusterfsf", updaed.getTimestamp() + "");
                        Log.d("clusterfsf", updaed.getUser().getEmail() + "");
                        Log.d("clusterfsf", updaed.getUser().getUsername() + "");
                        FlourMills.add(updaed);


                    }


                    firestoreCallBackFlourMill.onCallBack2(FlourMills);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void readDataBakerys(FirestoreCallBackBakery firestoreCallBack2) {

        try {
            CollectionReference c = mDB.collection("Bakerys_Location");
            c.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot doc : task.getResult()) {
                        try {


                            UserLocation updaed = doc.toObject(UserLocation.class);
                            LocationsListBakery.add(updaed);
                            Log.d("checkbakery", updaed.getGeo_point().getLatitude() + " ");
                            Log.d("checkbakery", updaed.getUser().getUser_id() + " ");
                        } catch (Exception e) {

                        }
                    }

                    firestoreCallBack2.onCallBack(LocationsListBakery);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void addMapMakerersBakerys() {

        try {
            if (googleMap != null) {
                if (BakeryCluster == null) {
                    BakeryCluster = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), googleMap);
                }
                if (BakeryRenderer == null) {
                    BakeryRenderer = new MyClusterManagerRenderer(
                            getActivity(),
                            googleMap,
                            BakeryCluster
                    );
                }
                BakeryCluster.setRenderer(BakeryRenderer);
            }

            readDataBakerys(new FirestoreCallBackBakery() {
                @Override
                public void onCallBack(List<UserLocation> userLocations) {
                    Log.d("checkbakery", userLocations.size() + " ");
                    for (UserLocation u : userLocations) {
                        String snippet = "";
                        //  Log.d("cluster", u.getUser().getUser_id());

                        try {


                            snippet = "For more information " + u.getUser().getUsername() + "?";

                        } catch (NullPointerException e) {
                            Log.d("addmapmarker:", "nullnew " + e.getMessage());
                        }
                        try {
                            int avatar = R.drawable.bakeryi;
                            ClusterMarker newClusterMarker = new ClusterMarker(
                                    new LatLng(u.getGeo_point().getLatitude(), u.getGeo_point().getLongitude()), u.getUser().getUsername(), snippet, avatar, u.getUser());
                            BakeryCluster.addItem(newClusterMarker);
                            markerClusters.add(newClusterMarker);
                        } catch (NullPointerException e) {
                            Log.d("addmapmarker:", "null pointer " + e.getMessage());

                        }
                    }
                    BakeryCluster.cluster();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMapMakerers() {
        resetMap();
        try {
            if (googleMap != null) {
                if (TruckDriverCluster == null) {
                    TruckDriverCluster = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), googleMap);
                }
                if (clusterManagerRenderer == null) {
                    clusterManagerRenderer = new MyClusterManagerRenderer(
                            getActivity(),
                            googleMap,
                            TruckDriverCluster
                    );
                }
                TruckDriverCluster.setRenderer(clusterManagerRenderer);
            }

            readData(new FirestoreCallBack() {
                @Override
                public void onCallBack(List<UserLocation> userLocations) {
                    Log.d("clustersize", userLocations.size() + " ");
                    for (UserLocation u : userLocations) {
                        String snippet = "";
                        //  Log.d("cluster", u.getUser().getUser_id());

                        try {


                            snippet = "Determine route to " + u.getUser().getUsername() + "?";

                        } catch (NullPointerException e) {
                            Log.d("addmapmarker:", "nullnew " + e.getMessage());
                        }
                        try {
                            int avatar = R.drawable.truckic;
                            ClusterMarker newClusterMarker = new ClusterMarker(
                                    new LatLng(u.getGeo_point().getLatitude(), u.getGeo_point().getLongitude()), u.getUser().getUsername(), snippet, avatar, u.getUser());
                            TruckDriverCluster.addItem(newClusterMarker);
                            markerClusters.add(newClusterMarker);
                        } catch (NullPointerException e) {
                            Log.d("addmapmarker:", "null pointer " + e.getMessage());

                        }
                    }
                    TruckDriverCluster.cluster();


                    createCap();


                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addMapMakerersFlourMill() {

        try {
            if (googleMap != null) {
                if (FlourMillCluster == null) {
                    FlourMillCluster = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), googleMap);
                }

                if (RendererCluster == null) {
                    RendererCluster = new MyClusterManagerRendererFlourMill(
                            getActivity(),
                            googleMap,
                            FlourMillCluster
                    );
                }
                FlourMillCluster.setRenderer(RendererCluster);
            }

            readDataFlourMills(new FirestoreCallBackFlourMill() {
                @Override
                public void onCallBack2(List<FlourMillsLocation> userLocations) {
                    Log.d("newclustersize", FlourMills.size() + " ");
                    for (FlourMillsLocation u2 : userLocations) {
                        String snippet = "";

                        Log.d("idnsds", u2.getUser().getId() + " ");
                        try {


                            snippet = "Flour Mill " + u2.getUser().getUsername() + "";

                        } catch (NullPointerException e) {
                            Log.d("newaddmapmarker:", "nullnew " + e.getMessage());
                        }
                        try {
                            int avatar = R.drawable.wiconw;
                            ClusterMarkerFlourMill newClusterMarker = new ClusterMarkerFlourMill(
                                    new LatLng(u2.getLocation().getLatitude(), u2.getLocation().getLongitude()), u2.getUser().getUsername(), snippet, avatar, u2.getUser());
                            FlourMillCluster.addItem(newClusterMarker);
                            markerClustersFlourMills.add(newClusterMarker);
                        } catch (NullPointerException e) {
                            Log.d("newaddmapmarker:", "null pointer " + e.getMessage());

                        }
                    }
                    FlourMillCluster.cluster();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createCap() {


        try {
            String uid = FirebaseAuth.getInstance().getUid();
            readData(new FirestoreCallBack() {
                @Override
                public void onCallBack(List<UserLocation> userLocations) {
                    Log.d("CHECKTRUCK", userLocations.size() + " ");


                    for (UserLocation u : userLocations) {
                        if (u.getUser() != null) {
                            if (u.getUser().getUser_id().trim().equals(uid.trim())) {

                                Log.d("checkbakery", u.getUser().getUser_id() + "/ " + uid);
                                userPosition = u;
                            }
                        }
                    }
                    if (userPosition != null) {
                        double bottomBoundary = userPosition.getGeo_point().getLatitude() - .1;
                        double leftBoundary = userPosition.getGeo_point().getLongitude() - .1;
                        double topBoundary = userPosition.getGeo_point().getLatitude() + .1;
                        double rightBoundary = userPosition.getGeo_point().getLongitude() + .1;

                        Boundery = new LatLngBounds(
                                new LatLng(bottomBoundary, leftBoundary),
                                new LatLng(topBoundary, rightBoundary)
                        );

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Boundery, 0));
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void startUserLocationsRunnable() {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations2();
                retrieveUserLocations4();
                retrieveUserLocations5();
                handler.postDelayed(runnable, LOCATION_UPDATE_INTERVAL);
                Log.d("runnable", "called");
            }

        }, LOCATION_UPDATE_INTERVAL);
    }


    private void retrieveUserLocations2() {

        try {
            try {
                for (ClusterMarker clusterMarker : markerClusters) {

                    DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                            .collection("user_locations")
                            .document(clusterMarker.getUser().getUser_id());
                    Log.e("uil", "retrieveUserLocations: " + clusterMarker.getUser().getUser_id());

                    userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);
                                //    Log.e("userid", "retrieveUserLocations: " + updatedUserLocation.getUser().getUser_id());

                                for (int i = 0; i < markerClusters.size(); i++) {
                                    try {
                                        if (markerClusters.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {
                                            LatLng updatedLatLng = new LatLng(
                                                    updatedUserLocation.getGeo_point().getLatitude(),
                                                    updatedUserLocation.getGeo_point().getLongitude()
                                            );
                                            markerClusters.get(i).setPosition(updatedLatLng);
                                            clusterManagerRenderer.setUpdateMarker(markerClusters.get(i));
                                        }
                                    } catch (NullPointerException e) {
                                        Log.e("HomeFragmnet", "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    });
                }

            } catch (IllegalStateException e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void retrieveUserLocations4() {
        Log.e("calleddd", "retrieveUserLocations: ");


        try {
            for (final ClusterMarkerFlourMill clusterMarker : markerClustersFlourMills) {


                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection("flourmill_location")
                        .document(String.valueOf(clusterMarker.getUser().getId()));
                Log.e("newuil", "retrieveUserLocations: " + clusterMarker.getUser().getId());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            FlourMillsLocation updatedUserLocation = task.getResult().toObject(FlourMillsLocation.class);
                            Log.e("userid", "retrieveUserLocations: " + updatedUserLocation.getUser().getId());

                            for (int i = 0; i < markerClustersFlourMills.size(); i++) {
                                try {
                                    if (String.valueOf(markerClustersFlourMills.get(i).getUser().getId()).equals(String.valueOf(updatedUserLocation.getUser().getId()))) {
                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getLocation().getLatitude(),
                                                updatedUserLocation.getLocation().getLongitude()
                                        );
                                        markerClustersFlourMills.get(i).setPosition(updatedLatLng);
                                        RendererCluster.setUpdateMarker(markerClustersFlourMills.get(i));
                                    }
                                } catch (NullPointerException e) {
                                    Log.e("newHomeFragmnet", "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //   retrieveUserLocations5();
    }


    private void retrieveUserLocations5() {

        try {
            for (final ClusterMarker clusterMarker : markersBakerys) {

                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection("Bakerys_Location")
                        .document(clusterMarker.getUser().getUser_id());
                Log.e("checkbakery", "retrieveUserLocations: " + clusterMarker.getUser().getUser_id());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);
                            Log.e("checkbakery", "retrieveUserLocations: " + updatedUserLocation.getUser().getUser_id());

                            for (int i = 0; i < markersBakerys.size(); i++) {
                                try {
                                    if (markersBakerys.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {
                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getGeo_point().getLatitude(),
                                                updatedUserLocation.getGeo_point().getLongitude()
                                        );
                                        markersBakerys.get(i).setPosition(updatedLatLng);
                                        BakeryRenderer.setUpdateMarker(markersBakerys.get(i));
                                    }
                                } catch (NullPointerException e) {
                                    Log.e("HomeFragmnet", "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
            }
        } catch (IllegalStateException ignored) {
        }
    }


    private void expandMapAnimation() {


        try {
            ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContianer);
            ObjectAnimator mapAnimantion = ObjectAnimator.ofFloat(mapAnimationWrapper, "weight", 50, 100);
            mapAnimantion.setDuration(800);

            ViewWeightAnimationWrapper recycleViewAnimationWrraper = new ViewWeightAnimationWrapper(recyclerView);
            ObjectAnimator recycleViewAnimation = ObjectAnimator.ofFloat(recycleViewAnimationWrraper, "weight", 50, 0);
            recycleViewAnimation.setDuration(800);

            recycleViewAnimation.start();
            mapAnimantion.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void reverseMapAnimation() {

        try {
            ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContianer);
            ObjectAnimator mapAnimantion = ObjectAnimator.ofFloat(mapAnimationWrapper, "weight", 100, 50);
            mapAnimantion.setDuration(800);

            ViewWeightAnimationWrapper recycleViewAnimationWrraper = new ViewWeightAnimationWrapper(recyclerView);
            ObjectAnimator recycleViewAnimation = ObjectAnimator.ofFloat(recycleViewAnimationWrraper, "weight", 0, 50);
            recycleViewAnimation.setDuration(800);

            recycleViewAnimation.start();
            mapAnimantion.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fullscreen: {

                if (MapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    MapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                } else if (MapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    MapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    reverseMapAnimation();
                }
                break;
            }
            case R.id.findmap: {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Open Google Maps ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                String latitude = globalLatitude;
                                String longitude = globalLongitude;
                                Uri googlemapURL = Uri.parse("google.navigation:q=" + newLat + "," + newLng);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, googlemapURL);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                try {
                                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "error" + e.getMessage());
                                    Toast.makeText(getActivity(), "could not open map", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();


                break;
            }
            case R.id.resetmap: {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    public void run() {

                        addMapMakerers();
                        addMapMakerersFlourMill();
                        addMapMakerersBakerys();

                    }
                }, 1500);


                break;
            }
            case R.id.fisnish: {

                try {
                    FinishOrder finishOrder = new FinishOrder();
                    finishOrder.setOrderid(newOrderId);
                    finishOrder.setOrderStatues(3);

                    Log.d("uids", "myuid " + FirebaseAuth.getInstance().getUid());

                    DocumentReference updateStatues = mDB.collection("full_order").document(FirebaseAuth.getInstance().getUid());
                    updateStatues.update(
                            FieldPath.of("order", "orderStatues"), 3
                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("firestoreupdate", "updated");
                            } else {
                                Log.d("firestoreupdate", " notupdated");

                            }
                        }
                    });


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            Call<ResponseBody> finishCall = orderClient.FinishOrder(finishOrder);
                            finishCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    if (response.isSuccessful()) {


                                        //    recyclerView.setVisibility(View.VISIBLE);
                                        //     LinearLayout dynamicContent = getActivity().findViewById(R.id.dynamic_content);
                                        //      dynamicContent.setVisibility(View.GONE);
                                        //        dynamicContent.removeView(dynamicContent);

                                        selected = new NewOrdersFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.frame1, selected).commit();


                                        Log.d("finsiho", "Order Finished");
                                    } else {
                                        Log.d("finsiho", "Order not Finished");
                                    }


                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    Log.d("finsiho", "error in connection");

                                }
                            });


                        }
                    }, 1500);

                    getOrders();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to determine the route ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        resetSelectedMarker();
                        CalculateDirections(marker);
                        SelectedMaker = marker;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void CalculateDirections(Marker marker) {

        Log.d(TAG, "Calculatedireaction");
        try {
            com.google.maps.model.LatLng mydestination = new com.google.maps.model.LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(geoApiContext);
            directionsApiRequest.alternatives(true);
            directionsApiRequest.origin(
                    new com.google.maps.model.LatLng(
                            userPosition.getGeo_point().getLatitude(),
                            userPosition.getGeo_point().getLongitude()
                    )
            );

            Log.d(TAG, "Calcualtedirection destination" + mydestination.toString());
            directionsApiRequest.destination(mydestination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {

                    Log.d("mki", "calculateDirections: route " + result.routes[0].toString());
                    Log.d("mki", "calculateDirections: duration " + result.routes[0].legs[0].duration);
                    Log.d("mki", "calculateDirections: distance " + result.routes[0].legs[0].distance);
                    Log.d("mki", "calculateDirections: geocodedwaypoints " + result.geocodedWaypoints.toString());
    /*
                    Log.d("mki", "2: route " + result.routes[1].toString());
                    Log.d("mki", "2: duration " + result.routes[1].legs[1].duration);
                    Log.d("mki", "2: distance " + result.routes[1].legs[1].distance);
                    Log.d("mki", "2: geocodedwaypoints " + result.geocodedWaypoints.toString());


                    Log.d("mki", "3: route " + result.routes[0].toString());
                    Log.d("mki", "3: duration " + result.routes[0].legs[1].duration);
                    Log.d("mki", "3: distance " + result.routes[0].legs[1].distance);
                    Log.d("mki", "3: geocodedwaypoints " + result.geocodedWaypoints.toString());
                    */

                    AddPolyLinesToMap(result);

                }

                @Override
                public void onFailure(Throwable e) {
                    Log.d(TAG, "calculateDirections: failyer " + e.getMessage());


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void AddPolyLinesToMap(final DirectionsResult result) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("poly", "runing " + result.routes.length);
                    if (mPolyLinesData.size() > 0) {
                        for (PolylineData polylineData : mPolyLinesData) {
                            polylineData.getPolyline().remove();
                        }
                        mPolyLinesData.clear();
                        mPolyLinesData = new ArrayList<>();

                    }
                    double duration = 99999999;
                    for (DirectionsRoute route : result.routes) {
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                        List<LatLng> newDecodedPath = new ArrayList<>();
                        for (com.google.maps.model.LatLng latLng : decodedPath) {
                            newDecodedPath.add(new LatLng(
                                    latLng.lat,
                                    latLng.lng
                            ));
                        }
                        Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                        polyline.setClickable(true);


                        mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));


                        double tempDuration = route.legs[0].duration.inSeconds;
                        if (tempDuration < duration) {
                            duration = tempDuration;
                            onPolylineClick(polyline);
                            zoomRoute(polyline.getPoints());
                        }
                        if (SelectedMaker == null) {
                            return;
                        }
                        SelectedMaker.setVisible(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        try {
            int index = 0;
            for (PolylineData polylineData : mPolyLinesData) {
                index++;
                Log.d("polykj", polylineData.toString());
                if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                    polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                    polylineData.getPolyline().setZIndex(1);

                    LatLng endLocaction = new LatLng(polylineData.getLeg().endLocation.lat, polylineData.getLeg().endLocation.lng);
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(endLocaction).title("Delivery #" + index).snippet("Duration: " + polylineData.getLeg().duration + " Distance: " + polylineData.getLeg().distance));
                    marker.showInfoWindow();
                    TripMakers.add(marker);

                } else {
                    polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polylineData.getPolyline().setZIndex(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String destination;

        try {
            if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                bid = data.getIntExtra("bID", 0);
                aid = data.getIntExtra("aID", 0);
                newOrderId = data.getIntExtra("oID", 0);
                destination = data.getStringExtra("desti");
                Log.e("Result", result);
                Log.d("insidenew", "Integer.parseInt(adminid)" + aid + "  ");
                Log.d("insidenew", "Integer.parseInt(bakeryid)" + bid + "  ");
                Log.d("insidenew", "newOrderId" + newOrderId + "  ");
                //  Toasty.success(getActivity(), "value of intent " + result, Toasty.LENGTH_LONG).show();
                StartpolyAfterResult();

                recyclerView.setVisibility(View.GONE);
                LinearLayout dynamicContent = getActivity().findViewById(R.id.dynamic_content);
                dynamicContent.setVisibility(View.VISIBLE);

                View wizardView = getLayoutInflater()
                        .inflate(R.layout.afterviewf, dynamicContent, false);

                dynamicContent.addView(wizardView);


                ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContianer);
                ObjectAnimator mapAnimantion = ObjectAnimator.ofFloat(mapAnimationWrapper, "weight", 50, 150);
                mapAnimantion.setDuration(200);

                mapAnimantion.start();


                itemCount = wizardView.findViewById(R.id.itemcount3);
                DurationT = wizardView.findViewById(R.id.duration2);
                DistanceT = wizardView.findViewById(R.id.distanceroad2);
                AddressT = wizardView.findViewById(R.id.distanceaddress2);
                wizardView.findViewById(R.id.findmap).setOnClickListener(this);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemCount.setText("Item Count: " + 5);
                        DurationT.setText("Duration: " + durationD);
                        DistanceT.setText("Disatance: " + distanceD);
                        AddressT.setText("Delivery Address: " + destination);
                    }
                }, 2000);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void ReadDataFlourMillsAfter(FirestoreCallBackFlourMill firestoreCallBackFlourMill) {

        try {
            Log.d("issdsdsd ", aid + "ggss");
            CollectionReference collectionReference = mDB.collection("flourmill_location");
            collectionReference.whereEqualTo(FieldPath.of("user", "id"), aid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                FlourMillsLocation flourMillsLocation = queryDocumentSnapshot.toObject(FlourMillsLocation.class);
                                FlourMillsAfterQuery.add(flourMillsLocation);
                                Log.d("mnjb", flourMillsLocation.getLocation().getLatitude() + " ");
                                Log.d("mnjb", flourMillsLocation.getUser().getId() + " ");

                            }
                            firestoreCallBackFlourMill.onCallBack2(FlourMillsAfterQuery);

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void StartpolyAfterResult() {


        try {
            Call<JsonElement> jsonElementCall = geoLocationClient.getGeoLocation(bid);
            jsonElementCall.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {

                        latitude = response.body().getAsJsonObject().get("lati").getAsString();
                        longtude = response.body().getAsJsonObject().get("longi").getAsString();
                        // Toasty.success(getContext(), latitude + " " + longtude, Toasty.LENGTH_LONG).show();
                        Log.d("sucissss", latitude + " " + longtude);
                        ReadDataFlourMillsAfter(new FirestoreCallBackFlourMill() {
                            @Override
                            public void onCallBack2(List<FlourMillsLocation> k) {

                                for (FlourMillsLocation flourMillsLocation : k) {

                                    Log.d("lkm", flourMillsLocation.getLocation().getLatitude() + " " + flourMillsLocation.getLocation().getLongitude());


                                    com.google.maps.model.LatLng mydestination = new com.google.maps.model.LatLng(Double.parseDouble(latitude), Double.parseDouble(longtude));
                                   LatLng marker2 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtude));
                                    globalLatitude = latitude;
                                    globalLongitude = longtude;
                                    DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(geoApiContext);
                                    directionsApiRequest.alternatives(true);


                                    directionsApiRequest.origin(
                                            new com.google.maps.model.LatLng(
                                                    userPosition.getGeo_point().getLatitude(),
                                                    userPosition.getGeo_point().getLongitude()
                                            )
                                    );
                                    directionsApiRequest.waypoints(new com.google.maps.model.LatLng(flourMillsLocation.getLocation().getLatitude(), flourMillsLocation.getLocation().getLongitude()));
                                    MarkerOptions markerOptions = new MarkerOptions();


                                    markerOptions.position(marker2);







                                    markerOptions.title("Bakery location");

                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker2));


                                    googleMap.addMarker(markerOptions);


                                    directionsApiRequest.optimizeWaypoints(true);

                                    Log.d(TAG, "Calcualtedirection destination" + mydestination.toString());
                                    directionsApiRequest.destination(mydestination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                                        @Override
                                        public void onResult(DirectionsResult result) {


                                            durationD = result.routes[0].legs[0].duration.toString();
                                            distanceD = result.routes[0].legs[0].distance.toString();

                                            if (result.routes[0].legs[1].duration != null && result.routes[0].legs[1].distance != null) {
                                                long disatance = result.routes[0].legs[0].duration.inSeconds + result.routes[0].legs[1].duration.inSeconds;
                                                long duration = result.routes[0].legs[0].distance.inMeters + result.routes[0].legs[1].distance.inMeters;
                                                long p1 = disatance % 60;
                                                long p2 = disatance / 60;
                                                long p3 = p2 % 60;
                                                p2 = p2 / 60;
                                                durationD = p2 + " hours " + p3 + " minutes";
                                                distanceD = String.format("%02d", (duration / 1000)) + " Km";
                                                newLat = result.routes[0].legs[1].endLocation.lat;
                                                newLng = result.routes[0].legs[1].endLocation.lng;
                                                newWaypointLat = result.routes[0].legs[0].endLocation.lat;
                                                newWayPointLng = result.routes[0].legs[0].endLocation.lat;
                                                itemCount.setText("Item Count: " + 2);
                                                DurationT.setText("Duration: " + durationD);
                                                DistanceT.setText("Disatance: " + distanceD);


                                            } else {
                                                newLat = result.routes[0].legs[0].endLocation.lat;
                                                newLng = result.routes[0].legs[0].endLocation.lng;
                                                durationD = result.routes[0].legs[0].duration.toString();
                                                distanceD = result.routes[0].legs[0].distance.toString();
                                                itemCount.setText("Item Count: " + 2);
                                                DurationT.setText("Duration: " + durationD);
                                                DistanceT.setText("Disatance: " + distanceD);

                                            }

                                            Log.d("mki", "calculateDirections: ds " + durationD);
                                            Log.d("mki", "calculateDirections: sd " + distanceD);

                                            Log.d("mki", "calculateDirections: route " + result.routes[0].toString());
                                            Log.d("mki", "calculateDirections: duration " + result.routes[0].legs[0].duration);
                                            Log.d("mki", "calculateDirections: distance " + result.routes[0].legs[0].distance);
/*
                    Log.d("mko", "2: route " + result.routes[1].toString());
                    Log.d("mko", "2: duration " + result.routes[1].legs[1].duration);
                    Log.d("mko", "2: distance " + result.routes[1].legs[1].distance);
                    Log.d("mko", "2: geocodedwaypoints " + result.geocodedWaypoints.toString());
*/


                                            Log.d("mkl", "3: route " + result.routes[0].toString());
                                            Log.d("mkl", "3: duration " + result.routes[0].legs[1].duration);
                                            Log.d("mkl", "3: distance " + result.routes[0].legs[1].distance);
                                            Log.d("mkl", "3: geocodedwaypoints " + result.geocodedWaypoints.toString());

                                            AddPolyLinesToMap(result);

                                        }

                                        @Override
                                        public void onFailure(Throwable e) {
                                            Log.d(TAG, "calculateDirections: failyer " + e.getMessage());
                                        }
                                    });

                                }


                            }
                        });

                    } else {
                        //     Toasty.error(getContext(), latitude + " " + longtude, Toasty.LENGTH_LONG).show();
                        Log.d("suci", "errror" + " " + "o");

                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Toasty.error(getContext(), "Error in conenction", Toasty.LENGTH_LONG).show();


                }
            });


            Log.d(TAG, "Calculatedireaction");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zoomRoute(List<LatLng> latLngList) {
        try {
            if (googleMap == null || latLngList == null || latLngList.isEmpty()) {
                return;
            }
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : latLngList) {
                boundsBuilder.include(latLngPoint);
            }
            int routePadding = 120;
            LatLngBounds latLngBounds = boundsBuilder.build();
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                    600,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetMap() {

        try {
            if (googleMap != null) {

                googleMap.clear();
                if (TruckDriverCluster != null) {
                    TruckDriverCluster.clearItems();
                }
                if (FlourMillCluster != null) {
                    FlourMillCluster.clearItems();
                }
                if (BakeryCluster != null) {
                    BakeryCluster.clearItems();
                }
                if (markerClusters.size() > 0) {
                    markerClusters.clear();
                    markerClusters = new ArrayList<>();
                }
                if (markersBakerys.size() > 0) {
                    markersBakerys.clear();
                    markersBakerys = new ArrayList<>();
                }
                if (markerClustersFlourMills.size() > 0) {
                    markerClustersFlourMills.clear();
                    markerClustersFlourMills = new ArrayList<>();
                }
                if (mPolyLinesData.size() > 0) {
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetSelectedMarker() {
        try {
            if (SelectedMaker != null) {
                SelectedMaker.setVisible(true);
                SelectedMaker = null;
                removeTripMakers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeTripMakers() {
        try {
            for (Marker marker : TripMakers) {
                marker.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
