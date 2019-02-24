package com.ysn.mapboxsample

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap
    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.map_box_access_token))
        setContentView(R.layout.activity_main)
        initMapView(savedInstanceState)
        initPermissions()
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_change_style -> {
                val items = arrayOf("Mapbox Street", "Outdoor", "Light", "Dark", "Satellite", "Satellite Street", "Traffic Day", "Traffic Night")
                val alertDialogChangeStyleMaps = AlertDialog.Builder(this)
                    .setItems(items) { dialog, item ->
                        when (item) {
                            0 -> {
                                mapboxMap.setStyle(Style.MAPBOX_STREETS)
                                dialog.dismiss()
                            }
                            1 -> {
                                mapboxMap.setStyle(Style.OUTDOORS)
                                dialog.dismiss()
                            }
                            2 -> {
                                mapboxMap.setStyle(Style.LIGHT)
                                dialog.dismiss()
                            }
                            3 -> {
                                mapboxMap.setStyle(Style.DARK)
                                dialog.dismiss()
                            }
                            4 -> {
                                mapboxMap.setStyle(Style.SATELLITE)
                                dialog.dismiss()
                            }
                            5 -> {
                                mapboxMap.setStyle(Style.SATELLITE_STREETS)
                                dialog.dismiss()
                            }
                            6 -> {
                                mapboxMap.setStyle(Style.TRAFFIC_DAY)
                                dialog.dismiss()
                            }
                            7 -> {
                                mapboxMap.setStyle(Style.TRAFFIC_NIGHT)
                                dialog.dismiss()
                            }
                        }
                    }
                    .setTitle(getString(R.string.change_style_maps))
                    .create()
                alertDialogChangeStyleMaps.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMapView(savedInstanceState: Bundle?) {
        map_view.onCreate(savedInstanceState)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS)
        this.mapboxMap.addOnMapClickListener {
            markers.add(
                mapboxMap.addMarker(
                    MarkerOptions().position(it)
                )
            )
        }
        this.mapboxMap.setOnMarkerClickListener {
            for (marker in markers) {
                if (marker.position == it.position) {
                    markers.remove(marker)
                    mapboxMap.removeMarker(marker)
                    break
                }
            }
            true
        }
    }

    private fun initPermissions() {
        val permissionListener = object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                /* Nothing to do in here */
            }

            override fun onPermissionResult(granted: Boolean) {
                if (granted) {
                    syncMapbox()
                } else {
                    val alertDialogInfo = AlertDialog.Builder(this@MainActivity)
                        .setTitle(getString(R.string.info))
                        .setCancelable(false)
                        .setMessage(getString(R.string.permissions_denied))
                        .setPositiveButton(getString(R.string.dismiss)) { _, _ ->
                            finish()
                        }
                        .create()
                    alertDialogInfo.show()
                }
            }
        }
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            syncMapbox()
        } else {
            permissionsManager = PermissionsManager(permissionListener)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun syncMapbox() {
        map_view.getMapAsync(this)
    }
}
