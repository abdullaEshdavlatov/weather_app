package uz.abdulla.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.abdulla.weatherapp.databinding.HourlyWeatherItemsBinding
import uz.abdulla.weatherapp.model.HourlyWeather

class HourlyWeatherAdapter(private val list: List<HourlyWeather>): RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: HourlyWeatherItemsBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(hourlyWeather: HourlyWeather){
            binding.textHour.text = hourlyWeather.hour
            Picasso.get().load(hourlyWeather.icon).into(binding.imageWeather)
            binding.textTemperature.text = hourlyWeather.temperature + "°"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HourlyWeatherItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }
}