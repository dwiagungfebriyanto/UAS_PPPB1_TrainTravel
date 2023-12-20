package com.example.traintravel.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.traintravel.R
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.ActivityTicketFormBinding
import com.example.traintravel.ticket.TicketPricing
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TicketFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketFormBinding
    private var price = 0
    private var trainPrice = 0
    private var stationPrice = 0
    private var classPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            var trainName = ""
            var departureStation = ""
            var destinationStation = ""
            var classType = ""
            var departureTime = ""
            var arrivalTime = ""

            val trainNameArray = resources.getStringArray(R.array.train_name)
            val trainAdapter = ArrayAdapter<String>(this@TicketFormActivity, R.layout.spinner_item, trainNameArray)
            spinnerTrainName.adapter = trainAdapter

            val station = resources.getStringArray(R.array.station)
            val stationAdapter = ArrayAdapter<String>(this@TicketFormActivity, R.layout.spinner_item, station)
            spinnerDeparture.adapter = stationAdapter
            spinnerDestination.adapter = stationAdapter

            val classTypeArray = resources.getStringArray(R.array.class_type)
            val classAdapter = ArrayAdapter<String>(this@TicketFormActivity, R.layout.spinner_item, classTypeArray)
            spinnerClass.adapter = classAdapter

            spinnerTrainName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    trainName = trainNameArray[position]
                    trainPrice = TicketPricing.getTrainPrice(trainName)
                    priceChanged()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerDeparture.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    departureStation = station[position]
                    stationPrice = TicketPricing.getStationPrice(departureStation, destinationStation)
                    if (stationPrice == 0) {
                        Toast.makeText(this@TicketFormActivity, "Departure and destination station is not valid.", Toast.LENGTH_SHORT).show()
                    }
                    priceChanged()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerDestination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    destinationStation = station[position]
                    stationPrice = TicketPricing.getStationPrice(departureStation, destinationStation)
                    if (stationPrice == 0) {
                        Toast.makeText(this@TicketFormActivity, "Departure and destination station is not valid.", Toast.LENGTH_SHORT).show()
                    }
                    priceChanged()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    classType = classTypeArray[position]
                    classPrice = TicketPricing.getClassPrice(classType)
                    priceChanged()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val calendarDate = Calendar.getInstance()
            val year = calendarDate.get(Calendar.YEAR)
            val month = calendarDate.get(Calendar.MONTH)
            val dayOfMonth = calendarDate.get(Calendar.DAY_OF_MONTH)

            btnDepartureDate.setOnClickListener {
                val datePicker = DatePickerDialog(this@TicketFormActivity, { _, year, month, dayOfMonth ->
                    btnDepartureDate.text = "$dayOfMonth/${month + 1}/$year"
                }, year, month, dayOfMonth)

                datePicker.show()
            }

            val calendarTime = Calendar.getInstance()
            val currentHour = calendarTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendarTime.get(Calendar.MINUTE)

            btnDepartureTime.setOnClickListener {
                val timePicker = TimePickerDialog(this@TicketFormActivity, { _, hourOfDay, minute ->
                    departureTime = String.format("%02d:%02d", hourOfDay, minute)
                    btnDepartureTime.text = departureTime
                    if (arrivalTime != "") {
                        txtTripDuration.text = calculateTripDuration(departureTime, arrivalTime)
                    }
                }, currentHour, currentMinute, true)

                timePicker.show()
            }

            btnArrivalTime.setOnClickListener {
                val timePicker = TimePickerDialog(this@TicketFormActivity, { _, hourOfDay, minute ->
                    arrivalTime = String.format("%02d:%02d", hourOfDay, minute)
                    btnArrivalTime.text = arrivalTime
                    txtTripDuration.text = calculateTripDuration(departureTime, arrivalTime)
                }, currentHour, currentMinute, true)

                timePicker.show()
            }

            if (intent.hasExtra("ticket")) {
                val ticket = intent.getSerializableExtra("ticket") as Ticket

                price = ticket.price
                edtImageUrl.setText(ticket.imageUrl)
                btnDepartureDate.text = ticket.departureDate
                departureTime = ticket.departureTime
                btnDepartureTime.text = ticket.departureTime
                arrivalTime = ticket.arrivalTime
                btnArrivalTime.text = ticket.arrivalTime
                txtTripDuration.text = ticket.tripDuration

                val selectedTrain = trainNameArray.indexOf(ticket.trainName)
                spinnerTrainName.setSelection(selectedTrain)
                val selectedDepartureStation = station.indexOf(ticket.departureStation)
                spinnerDeparture.setSelection(selectedDepartureStation)
                val selectedDestinationStation = station.indexOf(ticket.destinationStation)
                spinnerDestination.setSelection(selectedDestinationStation)
                val selectedClass = classTypeArray.indexOf(ticket.classType)
                spinnerClass.setSelection(selectedClass)

                btnSave.setOnClickListener {
                    if (stationPrice != 0) {
                        val updatedTicket = Ticket(
                            id = ticket.id,
                            trainName = trainName,
                            imageUrl = edtImageUrl.text.toString(),
                            departureDate = btnDepartureDate.text.toString(),
                            price = price,
                            classType = classType,
                            departureStation = departureStation,
                            departureTime = departureTime,
                            destinationStation = destinationStation,
                            arrivalTime = arrivalTime,
                            tripDuration = txtTripDuration.text.toString()
                        )
                        Firebase.updateTicket(updatedTicket)
                        finish()
                    }
                }
            } else {
                btnSave.setOnClickListener {
                    if (stationPrice != 0 && btnDepartureDate.text != "" && btnDepartureTime.text != "" && btnArrivalTime.text != "") {
                        val newTicket = Ticket(
                            trainName = trainName,
                            imageUrl = edtImageUrl.text.toString(),
                            departureDate = btnDepartureDate.text.toString(),
                            price = price,
                            classType = classType,
                            departureStation = departureStation,
                            departureTime = departureTime,
                            destinationStation = destinationStation,
                            arrivalTime = arrivalTime,
                            tripDuration = txtTripDuration.text.toString()
                        )
                        Firebase.addTicket(newTicket)
                        finish()
                    } else {
                        Toast.makeText(this@TicketFormActivity, "Please fill out the form correctly!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun calculateTripDuration(departureTime: String, arrivalTime: String): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Konversi string waktu menjadi objek Date
        val departureDate = format.parse(departureTime)
        val arrivalDate = format.parse(arrivalTime)

        // Cek jika departureTime > arrivalTime, tambahkan 1 hari ke arrivalDate
        if (departureDate.after(arrivalDate)) {
            val calendar = Calendar.getInstance()
            calendar.time = arrivalDate
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            arrivalDate.time = calendar.timeInMillis
        }

        // Menghitung selisih waktu dalam milidetik
        val durationInMillis = arrivalDate.time - departureDate.time

        // Konversi selisih waktu ke format jam dan menit
        val hours = durationInMillis / (1000 * 60 * 60)
        val minutes = (durationInMillis % (1000 * 60 * 60)) / (1000 * 60)

        return String.format("%d hour(s) %d minute(s)", hours, minutes)
    }

    private fun priceChanged() {
        price = trainPrice + stationPrice + classPrice
        binding.txtPrice.text = NumberFormat.getNumberInstance(Locale("id")).format(price)
    }
}