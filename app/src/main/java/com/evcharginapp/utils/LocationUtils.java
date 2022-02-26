package com.evcharginapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;

import com.evcharginapp.apppreferences.AppPreference;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LocationUtils {
    private static final long INTERVAL_DEFAULT = 45000;
    private static final float KILOMETER_TO_METER = 1000.0f;
    private static final float LATITUDE_TO_KILOMETER = 111.133f;
    private static final float LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE = 111.32f;
    private static final String PROVIDER_COARSE = "network";
    private static final String PROVIDER_FINE = "gps";
    private static final String PROVIDER_FINE_PASSIVE = "passive";
    private static final double SQUARE_ROOT_TWO = Math.sqrt(2.0d);
    private static Location mCachedPosition;
    private static Location mCachedPosition1;
    private static final Random mRandom = new Random();
    private int mBlurRadius;
    private FusedLocationProviderClient mFusedLocationClient;
    private final long mInterval;
    /* access modifiers changed from: private */
    public Listener mListener;
    private LocationCallback mLocationCallback;
    private LocationListener mLocationListener;
    private final LocationManager mLocationManager;
    private final boolean mPassive;
    /* access modifiers changed from: private */
    public Location mPosition;
    private final boolean mRequireFine;
    private final boolean mRequireNewLocation;

    public interface Listener {
        void canUpdateLocationToFBDB();

        void canUpdateLocationToServer();

        void onPositionChanged();
    }

    public static class Point implements Parcelable {
        public static final Creator<Point> CREATOR = new Creator<Point>() {
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            public Point[] newArray(int size) {
                return new Point[size];
            }
        };
        public final double latitude;
        public final double longitude;

        public Point(double lat, double lon) {
            this.latitude = lat;
            this.longitude = lon;
        }

        public String toString() {
            return "(" + this.latitude + ", " + this.longitude + ")";
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(this.latitude);
            out.writeDouble(this.longitude);
        }

        private Point(Parcel in) {
            this.latitude = in.readDouble();
            this.longitude = in.readDouble();
        }
    }

    public LocationUtils(Context context) {
        this(context, false);
    }

    public LocationUtils(Context context, boolean requireFine) {
        this(context, requireFine, false);
    }

    public LocationUtils(Context context, boolean requireFine, boolean passive) {
        this(context, requireFine, passive, INTERVAL_DEFAULT);
    }

    public LocationUtils(Context context, boolean requireFine, boolean passive, long interval) {
        this(context, requireFine, passive, interval, false);
    }

    public LocationUtils(Context context, boolean requireFine, boolean passive, long interval, boolean requireNewLocation) {
        this.mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    Location location = locationList.get(locationList.size() - 1);
                    Location unused = LocationUtils.this.mPosition = location;
                    LocationUtils.this.cachePosition();
                    Location location1 = LocationUtils.this.getCachedPosition1();
                    if (!(location1 == null || LocationUtils.calculateDistance(new Point(location1.getLatitude(), location1.getLongitude()), new Point(location.getLatitude(), location.getLongitude())) <= 0.0d || LocationUtils.this.mListener == null)) {
                        LocationUtils.this.mListener.canUpdateLocationToServer();
                        LocationUtils.this.cachePosition1();
                    }
                    if (LocationUtils.this.mListener != null) {
                        LocationUtils.this.mListener.onPositionChanged();
                    }
                    if (LocationUtils.this.mListener != null) {
                        LocationUtils.this.mListener.canUpdateLocationToFBDB();
                    }
                }
            }
        };
        this.mLocationManager = (LocationManager) context.getApplicationContext().getSystemService("location");
        this.mRequireFine = requireFine;
        this.mPassive = passive;
        this.mInterval = interval;
        this.mRequireNewLocation = requireNewLocation;
        if (!requireNewLocation) {
            this.mPosition = getCachedPosition();
            cachePosition();
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public boolean hasLocationEnabled() {
        return hasLocationEnabled(getProviderName());
    }

    private boolean hasLocationEnabled(String providerName) {
        try {
            return this.mLocationManager.isProviderEnabled(providerName);
        } catch (Exception e) {
            return false;
        }
    }

    public void beginUpdates(Activity activity) {
        endUpdates();
        if (!this.mRequireNewLocation) {
            this.mPosition = getCachedPosition();
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(300);
        mLocationRequest.setFastestInterval(300);
        mLocationRequest.setPriority(102);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.mFusedLocationClient = fusedLocationProviderClient;
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, this.mLocationCallback, (Looper) null);
    }

    public void beginUpdates1() {
        if (this.mLocationListener != null) {
            endUpdates();
        }
        if (!this.mRequireNewLocation) {
            this.mPosition = getCachedPosition();
        }
        this.mLocationListener = createLocationListener();
        this.mLocationManager.requestLocationUpdates(getProviderName(), this.mInterval, 0.0f, this.mLocationListener);
    }

    private void endUpdates() {
        FusedLocationProviderClient fusedLocationProviderClient = this.mFusedLocationClient;
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(this.mLocationCallback);
            this.mFusedLocationClient = null;
        }
    }

    public void endUpdates1() {
        LocationListener locationListener = this.mLocationListener;
        if (locationListener != null) {
            this.mLocationManager.removeUpdates(locationListener);
            this.mLocationListener = null;
        }
    }

    private Location blurWithRadius(Location originalLocation) {
        if (this.mBlurRadius <= 0) {
            return originalLocation;
        }
        Location newLocation = new Location(originalLocation);
        double blurMeterLong = ((double) calculateRandomOffset(this.mBlurRadius)) / SQUARE_ROOT_TWO;
        double blurMeterLat = ((double) calculateRandomOffset(this.mBlurRadius)) / SQUARE_ROOT_TWO;
        newLocation.setLongitude(newLocation.getLongitude() + meterToLongitude(blurMeterLong, newLocation.getLatitude()));
        newLocation.setLatitude(newLocation.getLatitude() + meterToLatitude(blurMeterLat));
        return newLocation;
    }

    private static int calculateRandomOffset(int radius) {
        return mRandom.nextInt((radius + 1) * 2) - radius;
    }

    public Point getPosition() {
        Location location = this.mPosition;
        if (location == null) {
            return null;
        }
        Location position = blurWithRadius(location);
        return new Point(position.getLatitude(), position.getLongitude());
    }

    public double getLatitude() {
        Location location = this.mPosition;
        if (location == null) {
            return 0.0d;
        }
        return blurWithRadius(location).getLatitude();
    }

    public double getLongitude() {
        Location location = this.mPosition;
        if (location == null) {
            return 0.0d;
        }
        return blurWithRadius(location).getLongitude();
    }

    public long getTimestampInMilliseconds() {
        Location location = this.mPosition;
        if (location == null) {
            return 0;
        }
        return location.getTime();
    }

    public long getElapsedTimeInNanoseconds() {
        if (this.mPosition == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            return this.mPosition.getElapsedRealtimeNanos();
        }
        return ((SystemClock.elapsedRealtime() + getTimestampInMilliseconds()) - System.currentTimeMillis()) * 1000000;
    }

    public float getSpeed() {
        Location location = this.mPosition;
        if (location == null) {
            return 0.0f;
        }
        return location.getSpeed();
    }

    public double getAltitude() {
        Location location = this.mPosition;
        if (location == null) {
            return 0.0d;
        }
        return location.getAltitude();
    }

    public void setBlurRadius(int blurRadius) {
        this.mBlurRadius = blurRadius;
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            public void onLocationChanged(Location location) {
                Location unused = LocationUtils.this.mPosition = location;
                LocationUtils.this.cachePosition();
                Location location1 = LocationUtils.this.getCachedPosition1();
                if (!(location1 == null || LocationUtils.calculateDistance(new Point(location1.getLatitude(), location1.getLongitude()), new Point(location.getLatitude(), location.getLongitude())) <= 500.0d || LocationUtils.this.mListener == null)) {
                    LocationUtils.this.mListener.canUpdateLocationToServer();
                    LocationUtils.this.cachePosition1();
                }
                if (LocationUtils.this.mListener != null) {
                    LocationUtils.this.mListener.canUpdateLocationToFBDB();
                }
                if (LocationUtils.this.mListener != null) {
                    LocationUtils.this.mListener.onPositionChanged();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    private String getProviderName() {
        return getProviderName(this.mRequireFine);
    }

    private String getProviderName(boolean requireFine) {
        if (requireFine) {
            return this.mPassive ? PROVIDER_FINE_PASSIVE : PROVIDER_FINE;
        }
        if (hasLocationEnabled(PROVIDER_COARSE)) {
            if (!this.mPassive) {
                return PROVIDER_COARSE;
            }
            throw new RuntimeException("There is no passive provider for the coarse location");
        } else if (hasLocationEnabled(PROVIDER_FINE) || hasLocationEnabled(PROVIDER_FINE_PASSIVE)) {
            return getProviderName(true);
        } else {
            return PROVIDER_COARSE;
        }
    }

    private Location getCachedPosition() {
        Location location = mCachedPosition;
        if (location != null) {
            return location;
        }
        try {
            return this.mLocationManager.getLastKnownLocation(getProviderName());
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public Location getCachedPosition1() {
        Location location = mCachedPosition1;
        if (location != null) {
            return location;
        }
        try {
            return this.mLocationManager.getLastKnownLocation(getProviderName());
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void cachePosition() {
        Location location = this.mPosition;
        if (location != null) {
            mCachedPosition = location;
        }
    }

    /* access modifiers changed from: private */
    public void cachePosition1() {
        Location location = this.mPosition;
        if (location != null) {
            mCachedPosition1 = location;
        }
    }

    public static void openSettings(Context context) {
        context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    public static double latitudeToKilometer(double latitude) {
        return 111.13300323486328d * latitude;
    }

    public static double kilometerToLatitude(double kilometer) {
        return kilometer / latitudeToKilometer(1.0d);
    }

    public static double latitudeToMeter(double latitude) {
        return latitudeToKilometer(latitude) * 1000.0d;
    }

    public static double meterToLatitude(double meter) {
        return meter / latitudeToMeter(1.0d);
    }

    public static double longitudeToKilometer(double longitude, double latitude) {
        return 111.31999969482422d * longitude * Math.cos(Math.toRadians(latitude));
    }

    public static double kilometerToLongitude(double kilometer, double latitude) {
        return kilometer / longitudeToKilometer(1.0d, latitude);
    }

    public static double longitudeToMeter(double longitude, double latitude) {
        return longitudeToKilometer(longitude, latitude) * 1000.0d;
    }

    public static double meterToLongitude(double meter, double latitude) {
        return meter / longitudeToMeter(1.0d, latitude);
    }

    public static double calculateDistance(Point start, Point end) {
        return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude);
    }

    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return (double) results[0];
    }



}
