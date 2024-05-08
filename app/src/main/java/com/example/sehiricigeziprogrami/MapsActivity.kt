package com.example.sehiricigeziprogrami

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sehiricigeziprogrami.databinding.FragmentMapsActivityBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
//import com.google.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsActivityBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentMapsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // intent ile enlem ve boylamı alırız

        val intent = intent
        var konumum : LatLng? = intent.getParcelableExtra("lastLocation")

        if (konumum!!.longitude == 0.0 || konumum!!.latitude == 0.0 && konumum == null){
            konumum = LatLng(39.7066489, 37.0270416) // varsayılan bir konum

        }



        // Başlangıç konumu için konum işaretleyicisi oluşturma
        val baslangicMarker = mMap.addMarker(MarkerOptions().position(konumum).title("Başlangıç Konumu"))

        // Başlangıç konumu
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                val myKonum = LatLng(p0.latitude, p0.longitude)
                mMap.clear() // mevcut işaretçileri temizle
                mMap.addMarker(MarkerOptions().position(myKonum).title("Mevcut Konumum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myKonum, 16f))
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // izin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            // izin verilmiş kullanıcı
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5f, locationListener)

            // Intent'ten seçilen konumları al
   /*         val selectedLocations: Array<LatLng>? = intent.getParcelableArrayExtra("selectedLocations")?.map { it as LatLng }?.toTypedArray()
            if (selectedLocations != null) {
                println(selectedLocations.get(1))
            }else{
                println("veriler gelmedi")
            }

    */
         //   val selectedLocations = intent.getParcelableArrayListExtra<LatLng>("selectedLocations")

           // val intent = intent // Eğer MapsActivity içindeyken intent'e erişiyorsanız bu satırı kullanabilirsiniz, aksi takdirde requireActivity() veya requireContext()'i kullanmanız gerekebilir.

            val selectedLocations = intent.getParcelableArrayListExtra<LatLng>("selectedLocations")

            if (selectedLocations != null) {
                // placelistForArrayList boş değilse, verileri alabilirsiniz
                println("MapsActivity:")
                for (item in selectedLocations) {
                    println(item)
                }
            } else {
                println("Veriler gelmedi")
            }


            if (selectedLocations != null && !selectedLocations.isNullOrEmpty()) {
                // Seçili konumları başlangıç konumu ile sırala
                val sortedLocations = listOf(konumum) + selectedLocations.sortedBy { calculateDistance(konumum, it) }

                // En yakından en uzağa doğru rota çiz
                for (i in 0 until sortedLocations.size - 1) {
                    if (selectedLocations == null){
                        println("Herhangi ifade yok")
                    }else{
                        drawRoute(sortedLocations[i], sortedLocations[i + 1])
                    }
                }

                // İşaretçileri ekle
                addMarkers(selectedLocations + listOf(konumum))
            }
        }
    }

    private fun calculateDistance(location1: LatLng, location2: LatLng): Float {
        val result = floatArrayOf(0f)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            result
        )
        return result[0] / 1000 // Metreyi kilometreye çevir
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val apiKey = "AIzaSyDyUrdvMerwvp-XkD1xwE9ZALUBMLid1aY"

        println("Origin : ${origin} Destination : ${destination}")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val directionsResult = DirectionsApi.newRequest(getGeoApiContext(apiKey))
                    .mode(TravelMode.DRIVING)
                    .origin("${origin.latitude},${origin.longitude}")
                    .destination("${destination.latitude},${destination.longitude}")
                    .await()

                val polylineOptions = PolylineOptions()
                for (step in directionsResult.routes[0].legs[0].steps) {
                    val points = step.polyline.decodePath()
                    for (point in points) {
                        polylineOptions.add(LatLng(point.lat, point.lng))
                    }
                }

                launch(Dispatchers.Main) {
                    mMap.addPolyline(polylineOptions)

                    val bounds = LatLngBounds.builder().include(origin).include(destination).build()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getGeoApiContext(apiKey: String): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //izin verildi
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
                }
            }
        }
    }

    private fun addMarkers(locations: List<LatLng>) {
        for (location in locations) {
            mMap.addMarker(MarkerOptions().position(location).title("Sivas"))

        }
    }
}
