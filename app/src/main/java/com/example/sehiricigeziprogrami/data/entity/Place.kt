package com.example.sehiricigeziprogrami.data.entity

import com.google.maps.model.LatLng
import java.io.Serializable

data class Place (val id : Int, val isim : String, val metin : String, val konum : LatLng, val resim : String, val adres : String) :
    Serializable {
}