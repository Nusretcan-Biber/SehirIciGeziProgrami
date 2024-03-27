package com.example.sehiricigeziprogrami.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sehiricigeziprogrami.data.entity.Place
import com.example.sehiricigeziprogrami.databinding.ListedCardsBinding
import com.google.maps.model.LatLng

class CardAdapter (var mContext: Context, var placeList: List<Place>) : RecyclerView.Adapter<CardAdapter.ListedCardHolder>() {

    public var selectedPlaces = mutableListOf<LatLng>() // çeştiğimiz mekanları takip eden liste


    inner class ListedCardHolder(var tasarim : ListedCardsBinding) : RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListedCardHolder {
        var binding = ListedCardsBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return ListedCardHolder(binding)
    }

    override fun getItemCount(): Int {
       return placeList.size
    }

    override fun onBindViewHolder(holder: ListedCardHolder, position: Int) {
        var place = placeList.get(position)
        var t = holder.tasarim

        t.cardMekanResmi.setImageResource(mContext.resources.getIdentifier(place.resim,"drawable",mContext.packageName))
        t.cardMekanAdi.text = "${place.isim}"

        t.card.setOnClickListener{  // burada seçilen mekanların konumları Latlng(enlem,boylam) şeklinde bir listede tutuluyor
            if (!selectedPlaces.contains(place.konum)) {
                selectedPlaces.add(place.konum) // Seçilen mekanı listeye ekle
                Log.e("dikkat", "${selectedPlaces}")
                t.selected.bringToFront()
                // Seçilen öğeyi görsel olarak işaretle (örneğin, arkaplan rengini değiştir)
            } else {
                selectedPlaces.remove(place.konum) // Seçilen mekanı listeden çıkar
                // Seçilen öğeyi görsel olarak işareti kaldır
            }
            true



        }
    }


}