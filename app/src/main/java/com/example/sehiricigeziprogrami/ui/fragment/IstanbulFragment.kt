package com.example.sehiricigeziprogrami.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sehiricigeziprogrami.MapsActivity
import com.example.sehiricigeziprogrami.data.entity.Place
import com.example.sehiricigeziprogrami.databinding.FragmentIstanbulBinding
import com.example.sehiricigeziprogrami.ui.adapter.CardAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


//import com.google.maps.model.LatLng
//bu ifade çakışmaya yol açıyor
// bu kütüphaneyle değiştirmek zorunda kaldım çünkü diğer kütüphane hata veriyordu
// dönüşüme izin vermiyordu

import com.google.android.gms.maps.model.LatLng


class IstanbulFragment : Fragment() {

    private lateinit var binding : FragmentIstanbulBinding
    private var permissionControl =0
    private var enlem : Double =0.0
    private var boylam : Double =0.0
    private lateinit var flpc : FusedLocationProviderClient
    private lateinit var locationTask : Task<Location>
    val mekanlistesi = mutableListOf<Place>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var binding = FragmentIstanbulBinding.inflate(inflater, container, false)

        flpc= LocationServices.getFusedLocationProviderClient(requireContext())

        binding.recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        val databaseReference = FirebaseDatabase.getInstance().getReference("Places")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mekanlistesi.clear() // Eski verileri temizle
                for (i in snapshot.children){
                    val isim = i.child("isim").getValue(String::class.java)
                    val metin = i.child("metin").getValue(String::class.java)
                    val enlem = i.child("enlem").getValue(Double::class.java)
                    val boylam = i.child("boylam").getValue(Double::class.java)
                    val resim = i.child("resim").getValue(String::class.java)
                    val adres = i.child("adres").getValue(String::class.java)

                    if (isim != null && metin != null && enlem != null && boylam != null && resim != null && adres != null) {
                        val konum = LatLng(enlem, boylam)
                        val place = Place(isim, metin, konum, resim, adres)
                        mekanlistesi.add(place)
                    }
                }
                // RecyclerView'i güncelle
                binding.recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase Error", "Veritabanı hatası: ${error.message}")
            }
        })



        Log.e("MEKANLİSTESİ" , "mekanlistesi ${mekanlistesi}")
        val cardAdapter = CardAdapter(requireContext(),mekanlistesi)
        binding.recycler.adapter = cardAdapter

        binding.gotoroad.setOnClickListener{
            permissionControl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

            if (permissionControl == PackageManager.PERMISSION_GRANTED){
                locationTask = flpc.lastLocation

                Log.e("DİKKAT","LOCATİON = ${locationTask}")
                GetLocation()
                Log.e( "Selected Locations" , "${cardAdapter.selectedPlaces}")
                Log.e("AFTER GET LOCATİON", "ENLEM:${enlem} , BOYLAM ${boylam}")

                val placelistFor: Array<LatLng> = cardAdapter.selectedPlaces.toTypedArray()

                val intent = Intent(requireContext(), MapsActivity::class.java)

                // burda listeye attım yoksa olmuyordu

                val placelistForArrayList = ArrayList(placelistFor!!.toList())

                println("IstanbulFragment:")
                for (item in placelistForArrayList) {
                    println(item)
                }

                //
                intent.putExtra("selectedLocations" ,placelistForArrayList) //Kullanıcının seçtiği mekanların konumlarını tutan Array<Latlng> gönderir
                intent.putExtra("lastLocation" , LatLng(enlem,boylam)) //Kullanıcı rota oluştur butonun bastığındaki konumunu gönderir
                startActivity(intent)


            }
            else{
                ActivityCompat.requestPermissions(requireActivity()
                    , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100
                )
            }
        }






        return binding.root
    }
    fun GetLocation() {

        locationTask.addOnSuccessListener {
            if (it != null){
                enlem = it.latitude
                boylam = it.longitude
                Log.e("GETLOCATİON FUNCTİON", "ENLEM:${enlem} , BOYLAM ${boylam}") // yukarıdaki ile aynı ise aynı lokasyonu bulup yollayacaz



            }
        }

    }

}