package com.example.sehiricigeziprogrami.data.entity

import java.io.Serializable

data class Place(val isim: String, val metin: String, val konum: com.google.android.gms.maps.model.LatLng, val resim: String, val adres: String) :
    Serializable {
}