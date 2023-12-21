package uz.abdulla.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.abdulla.weatherapp.OnItemClickListener
import uz.abdulla.weatherapp.databinding.CityNameItemBinding
import uz.abdulla.weatherapp.model.City

class CitiesNameAdapter(private val list: List<City>): RecyclerView.Adapter<CitiesNameAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener
    inner class ViewHolder(private val binding: CityNameItemBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(city: City){
            binding.cityName.text = city.city
            binding.countryName.text = city.country
            binding.root.setOnClickListener {
                listener.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CityNameItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

}