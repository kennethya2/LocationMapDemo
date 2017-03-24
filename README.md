# LocationMapDemo

## 類別:
- [MainActivity](https://github.com/kennethya2/LocationMapDemo#mainactivity) 
- [LocationMapActivity](https://github.com/kennethya2/LocationMapDemo#locationmapactivity) 
- [LocationUpdateService](https://github.com/kennethya2/LocationMapDemo#locationupdateservice) 
- [FusedLocationUpdate](https://github.com/kennethya2/LocationMapDemo#fusedlocationupdate) 

### MainActivity
----

進入LocationMap入口與定位權限檢查。

<img src="https://raw.githubusercontent.com/kennethya2/LocationMapDemo/master/images/locationmap-MainActivity.png" width="216" height="384">

#### 1. 開啟地圖前先進行定位權限檢查
在android 6.0以上版本 (API level 23)，對於危險權限的存取需要經過使用者同意。

參考：[Normal and Dangerous Permissions](https://developer.android.com/guide/topics/security/permissions.html#normal-dangerous)

<pre><code>
if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(mContext, LOCATION_FINE_PERMS, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    Log.i(TAG, "requestPermissions !!!");
}else{ // permission Granted
    openLocationMap();
}
</code></pre>

<img src="https://raw.githubusercontent.com/kennethya2/LocationMapDemo/master/images/locationmap-permission.jpg" width="216" height="384">

#### 2. 權限取得結果

 ``onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)``
<<<<<<< HEAD

若使用者勾選不再提示時```showRationale==false```做後續處理，以取得定位權限 。
<pre><code>
=======
 
若使用者勾選不再提示時```showRationale==false```做後續處理，以取得定位權限 。
<pre><code> 
>>>>>>> develop_kenneth
boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
if (!showRationale) { // user also CHECKED "never ask again"
    showToast();
    showDialog();
}
 </code></pre>

#### 3. 前往app設定開啟定位權限

未開啟權限導致無法正常功能時，建議使用者前往設定頁面開啟。
<pre><code>
private void goResetAppPermission(){
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", getPackageName(), null);
    intent.setData(uri);
    startActivity(intent);
}
</code></pre>

<img src="https://raw.githubusercontent.com/kennethya2/LocationMapDemo/master/images/locationmap-permission-denied.png" width="216" height="384">
<img src="https://raw.githubusercontent.com/kennethya2/LocationMapDemo/master/images/locationmap-permission-setting.png" width="216" height="384">

### LocationMapActivity
----

#### 1. 開啟&關閉存取所在地點service

<pre><code>
private void startLocationUpdateService(){
    Log.d(TAG, "startLocationUpdateService");
    Intent intent = new Intent();
    intent.setAction(LocationUpdateService.ACTION_NAME);
    intent.setPackage(getPackageName());
    startService(intent);
}
private void stopLocationUpdateService(){
    Log.d(TAG, "stopLocationUpdateService");
    Intent intent = new Intent();
    intent.setAction(LocationUpdateService.ACTION_NAME);
    intent.setPackage(getPackageName());
    stopService(intent);
}
</code></pre>

#### 2. 開啟&關閉接收位置reciever

<pre><code>
private void registerLocationReceiver(){
    IntentFilter filter = new IntentFilter(FusedLocationUpdate.ACTION);
    mContext.registerReceiver(lrsReceiver, filter);
    Log.d(TAG, "registerReceiver");
}
private void unregisterLocationReceiver(){
    try {
        mContext.unregisterReceiver(lrsReceiver);// avoid memory leak
    } catch (Exception e) {Log.w(TAG, "unregisterLRSReceiver:"+e.toString());}
    Log.d(TAG, "unregisterReceiver");
}
</code></pre>

#### 3. 地圖載入設定

當地圖載入時```void onMapReady(GoogleMap map)```初始設定

- 開啟縮放按鈕&指南針元件
<pre><code>
mMap.getUiSettings().setZoomControlsEnabled(true);
mMap.getUiSettings().setCompassEnabled(true);
</code></pre>

- 取得前次最後定位位置
<pre><code>
FusedLocationUpdate.LocationInfo mLocationInfo = mLocationPrefUtil.getLatestLocationInfo();
</code></pre>

- 地圖上顯示位置
<pre><code>
MarkerOptions markerOpt = new MarkerOptions();
markerOpt.title(title);
markerOpt.snippet(snippet);
markerOpt.position(location);
mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
currentPositionMarker = mMap.addMarker(markerOpt);
currentPositionMarker.showInfoWindow();
</code></pre>

<img src="https://raw.githubusercontent.com/kennethya2/LocationMapDemo/master/images/locationmap-location.png" width="216" height="384">

- 更新目前位置
當reciever取得更新位置時，呼叫
```updateCurrentPosition(FusedLocationUpdate.LocationInfo mLocationInfo)```
已更新目前位置。


### LocationUpdateService
----

#### 啟動&停止目前所在經緯度定位

利用FusedLocationUpdate類別物件```onStartFusedLocationUpdate()```與```onStopFusedLocationUpdate()```方法，啟動&停止經緯度定位。

<pre><code>
/**
 * Get Current Location
 * **/
private void goTraceLocation(){
    mFusedLocationUpdate = new FusedLocationUpdate(getApplicationContext());
    mFusedLocationUpdate.onStartFusedLocationUpdate();
}
@Override
public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
    mFusedLocationUpdate.onStopFusedLocationUpdate();
}
</code></pre>


### FusedLocationUpdate
----


#### 實作介面
- GoogleApiClient.ConnectionCallbacks 
監聽連線狀態
- GoogleApiClient.OnConnectionFailedListener 監聽連線異常狀態
- LocationListener 監聽位置異動


#### 1. 發出目前位置請求

<pre><code>
/**
 * trace location
 * **/
public void startPeriodicUpdates() {
    ...
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
}
</code></pre>

#### 2. 取得更新位置

<pre><code>
@Override
public void onLocationChanged(Location location) {
    ...
    setFuseLocation(location);
}
</code></pre>

#### 3. 送出目前位置更新廣播
取得更新位置後發出廣播通知，並且寫入SharedPreferences。

<pre><code>
private synchronized void setFuseLocation(Location currentLocation){
    ....
    Intent intent = new Intent(ACTION);
    Bundle bundle = new Bundle();
    bundle.putSerializable("LocationInfo",mLocationInfo);
    intent.putExtras(bundle);
    mContext.sendBroadcast(intent);
    
    mLocationPrefUtil.setLatestLocationInfo(mLocationInfo);
}
</code></pre>





