package com.example.sehiricigeziprogrami.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.sehiricigeziprogrami.MapsActivity
import com.example.sehiricigeziprogrami.R
import com.example.sehiricigeziprogrami.databinding.FragmentSivasBinding
import com.google.android.gms.maps.model.LatLng

class SivasFragment : Fragment() {

    private var binding: FragmentSivasBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSivasBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the button by its ID
        val button: Button = view.findViewById(R.id.button2)

        // Set OnClickListener
        button.setOnClickListener {
            onaylaBas()
        }
    }

    private fun onaylaBas(){
        val intent = Intent(requireContext(), MapsActivity::class.java)
        val selectedLocations = mutableListOf<LatLng>()

        // CheckBox'lardan seçilen konumları listeye ekle
        binding?.apply {
            if (checkBox1.isChecked) {
                selectedLocations.add(LatLng(39.3710811, 38.1191316))
                // divriği ulu camii
            }
            if (checkBox2.isChecked) {
                selectedLocations.add(LatLng(38.6568784, 37.3043599))
                // gökpınar gölü
            }
            if (checkBox3.isChecked) {
                selectedLocations.add(LatLng(39.7442689, 37.0142986))
                // gök medrese
            }
            if (checkBox4.isChecked) {
                selectedLocations.add(LatLng(39.7490268, 37.0129122))
                // buruciye medresesi
            }
            if (checkBox5.isChecked) {
                selectedLocations.add(LatLng(39.7509012, 37.0144841))
                // hükümet konağı
            }
            if (checkBox6.isChecked) {
                selectedLocations.add(LatLng(39.7579168, 37.0218068))
                // susamışlar konağı
            }
        }

        // Diğer şehirleri eklemeye devam edebilirsiniz...

        // Listeyi intent'e ekleyerek MapsActivity'e iletiyoruz
        intent.putParcelableArrayListExtra("selectedLocations", ArrayList(selectedLocations))
        startActivity(intent)
    }
}
