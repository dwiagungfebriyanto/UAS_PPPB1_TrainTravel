package com.example.traintravel.ticket

// Object TicketPricing berisi data harga tiket dan paket tambahan.
object TicketPricing {
    // Map yang berisi pasangan stasiun ke harga tiketnya
    val ticketPriceMap = mapOf(
        Pair("Tugu Yogyakarta", "Gambir") to 150000,
        Pair("Tugu Yogyakarta", "Sudirman") to 160000,
        Pair("Tugu Yogyakarta", "Jatinegara") to 170000,
        Pair("Tugu Yogyakarta", "Tawang") to 100000,
        Pair("Tugu Yogyakarta", "Purwokerto") to 110000,
        Pair("Tugu Yogyakarta", "Kroya") to 120000,

        Pair("Lempuyangan", "Gambir") to 100000,
        Pair("Lempuyangan", "Sudirman") to 110000,
        Pair("Lempuyangan", "Jatinegara") to 120000,
        Pair("Lempuyangan", "Tawang") to 130000,
        Pair("Lempuyangan", "Purwokerto") to 140000,
        Pair("Lempuyangan", "Kroya") to 90000,

        Pair("Sentolo", "Gambir") to 155000,
        Pair("Sentolo", "Sudirman") to 165000,
        Pair("Sentolo", "Jatinegara") to 175000,
        Pair("Sentolo", "Tawang") to 105000,
        Pair("Sentolo", "Purwokerto") to 115000,
        Pair("Sentolo", "Kroya") to 125000,

        Pair("Gambir", "Tawang") to 95000,
        Pair("Gambir", "Purwokerto") to 85000,
        Pair("Gambir", "Kroya") to 75000,

        Pair("Sudirman", "Tawang") to 165000,
        Pair("Sudirman", "Purwokerto") to 175000,
        Pair("Sudirman", "Kroya") to 185000,

        Pair("Jatinegara", "Tawang") to 105000,
        Pair("Jatinegara", "Purwokerto") to 105000,
        Pair("Jatinegara", "Kroya") to 105000
    )

    // Map yang berisi paket tambahan dan harganya
    val additionalPackages = mapOf(
        "Lunch box" to 25000,
        "Sit by the window" to 15000,
        "Charging port" to 10000,
        "Wi-Fi" to 12000,
        "Smoking area" to 50000,
        "Pillow & blanket" to 30000,
        "Extra luggage" to 45000,
        "Reclining seat" to 30000
    )

    // Fungsi untuk mendapatkan harga tiket antara dua stasiun.
    fun getStationPrice(departureStation : String, arrivalStation : String) : Int {
        var ticketPrice = ticketPriceMap[Pair(departureStation, arrivalStation)] ?: 0

        if (ticketPrice == 0) {
            ticketPrice = ticketPriceMap[Pair(arrivalStation, departureStation)] ?: 0
        }

        return ticketPrice
    }

    // Fungsi untuk mendapatkan harga tiket berdasarkan nama kereta.
    fun getTrainPrice(trainName : String) : Int {
        return when(trainName) {
            "Arlina Express"        -> 10000
            "Argo Bromo Anggrek"    -> 9000
            "Argo Lawu"             -> 5000
            "Malioboro Express"     -> 11000
            "Sancaka"               -> 8500
            else                    -> 0
        }
    }

    // Fungsi untuk mendapatkan harga tiket berdasarkan jenis kelas.
    fun getClassPrice(classType : String) : Int {
        return when(classType) {
            "Economy"   -> 2000
            "Business"  -> 20000
            "Executive" -> 50000
            else        -> 0
        }
    }
}