package uz.abdulla.weatherapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.abdulla.weatherapp.OnItemClickListener
import uz.abdulla.weatherapp.R
import uz.abdulla.weatherapp.adapters.CitiesNameAdapter
import uz.abdulla.weatherapp.databinding.FragmentMainBinding
import uz.abdulla.weatherapp.databinding.FragmentSearchBinding
import uz.abdulla.weatherapp.model.City
import java.io.IOException

class SearchFragment : Fragment(R.layout.fragment_search), OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var listOfCitiesName: MutableList<City> = ArrayList()
    private var listOfSearchedCitiesName: MutableList<City> = ArrayList()

    inline fun <reified T> Gson.parseJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        requireActivity().window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.background_color)

        initList()
        val adapter = CitiesNameAdapter(listOfSearchedCitiesName)

        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(searchedText: Editable?) {
                listOfSearchedCitiesName.clear()
                if (searchedText!!.isNotEmpty()){
                    binding.editTextCleaner.visibility = View.VISIBLE
                    listOfSearchedCitiesName.addAll(
                        listOfCitiesName.filter { name ->
                            name.city.contains(searchedText)
                        }
                    )
                }
                else
                    binding.editTextCleaner.visibility = View.INVISIBLE
                adapter.notifyDataSetChanged()
            }

        })

        binding.recyclerView.adapter = adapter
        adapter.setOnClickListener(this)
        binding.icBack.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.editTextCleaner.setOnClickListener {
            binding.editTextSearch.text.clear()
        }

    }


    private fun initList() {
        val gson = Gson()
        listOfCitiesName = getJsonDataFromAsset(requireContext(),"world_cities.json")?.let {
            gson.parseJson<List<City>>(
                it
            )
        } as MutableList<City>
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(position: Int) {
        val cityName = listOfSearchedCitiesName[position].city
        val directions = SearchFragmentDirections.actionSearchFragmentToMainFragment(cityName)
        findNavController().navigate(directions)
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


}