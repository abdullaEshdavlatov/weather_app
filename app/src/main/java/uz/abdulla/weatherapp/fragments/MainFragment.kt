package uz.abdulla.weatherapp.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import uz.abdulla.weatherapp.R
import uz.abdulla.weatherapp.adapters.HourlyWeatherAdapter
import uz.abdulla.weatherapp.databinding.FragmentMainBinding
import uz.abdulla.weatherapp.model.HourlyWeather
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import kotlin.math.roundToInt

class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding:FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val apiKeys = "ac9f85f610652aee775e9c825c0c45ad"

    private lateinit var adapter: HourlyWeatherAdapter
    private lateinit var cityName: String
    private val args by navArgs<MainFragmentArgs>()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        requireActivity().window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.blue)


        if (!args.name.isNullOrEmpty())
            cityName = args.name!!
        else
            cityName = "Samarkand"
        getCurrentWeather(cityName)
        getHourlyWeather(cityName)

        binding.icSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentWeather(city: String){
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${apiKeys}"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            {response ->
                try {
                    val calendar = Calendar.getInstance()
                    val day = LocalDate.now().dayOfWeek.name
                    val simpleDate = SimpleDateFormat("hh:mm")
                    val currentDate = simpleDate.format(calendar.time)

                    val weather = response.getJSONArray("weather").getJSONObject(0)
                    val main = response.getJSONObject("main")
                    val cityName = response.getString("name")

                    val temperature = (main.getDouble("temp") - 273.15).roundToInt()
                    val feelsLike = (main.getDouble("feels_like") - 273.15).roundToInt()
                    val description = weather.getString("description")
                    val icon = weather.getString("icon")
                    val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"

                    binding.cityName.text = cityName
                    binding.textTemperature.text = temperature.toString()
                    binding.textDescription.text = description.capitalize()
                    binding.textTempFeelsLike.text = "Feels like $feelsLike°"
                    binding.dayAndTime.text = "${day.lowercase().capitalize().substring(0,3)}, $currentDate"
                    Picasso.get().load(iconUrl).into(binding.imageTemperature)



                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },
            {error ->
                error.printStackTrace()
            })
        val queue = Volley.newRequestQueue(requireContext())
        queue.add(jsonObjectRequest)
    }
    private fun getHourlyWeather(city: String){
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=${city}&appid=${apiKeys}"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            {reponse ->
                try {

                    val list = reponse.getJSONArray("list")

                    val listHourlyWeather:MutableList<HourlyWeather> = ArrayList()
                    for (i in 0 until 8){
                        val date = list.getJSONObject(i).getString("dt_txt").substring(0,10)

                            val main = list.getJSONObject(i).getJSONObject("main")
                            val weather = list.getJSONObject(i).getJSONArray("weather").getJSONObject(0)

                            val temperature = (main.getDouble("temp") - 273.15).roundToInt()
                            val hour = list.getJSONObject(i).getString("dt_txt").substring(11,16)
                            val icon = weather.getString("icon")
                            val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                            val hourlyWeather = HourlyWeather(hour,iconUrl,temperature.toString())
                            listHourlyWeather.add(hourlyWeather)

                            adapter = HourlyWeatherAdapter(listHourlyWeather)
                            binding.rv.adapter = adapter

                    }

                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            {
                it.printStackTrace()
            })
        val queue = Volley.newRequestQueue(requireContext())
        queue.add(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}