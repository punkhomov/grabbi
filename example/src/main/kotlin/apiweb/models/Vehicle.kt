package punkhomov.grabbi.example.apiweb.models

@JvmInline
value class Vehicle private constructor(val value: String) {
    companion object {
        val METRO = Vehicle("103456789") // "Метро"
        val TROLLEYBUS = Vehicle("203456789") // "Троллейбус"
        val TRAM = Vehicle("303456789") // "Трамвай"
        val CITY_BUS = Vehicle("403456789") // "Автобус гор."
        val REGIONAL_BUS = Vehicle("603456789") // "Автобус обл."
        val TRAIN = Vehicle("803456789") // "Поезд"

        fun from(vehicle: String) = when (vehicle) {
            METRO.value -> METRO
            TROLLEYBUS.value -> TROLLEYBUS
            TRAM.value -> TRAM
            CITY_BUS.value -> CITY_BUS
            REGIONAL_BUS.value -> REGIONAL_BUS
            TRAIN.value -> TRAIN
            else -> Vehicle(vehicle)
        }
    }
}