interface ServicioDeDistancias {
    fun distanciaEnMilla(x: Float, y: Float, rand: Int): FloatArray
}

class ServicioDistancia(private val servicio: ServicioDeDistancias) {
    fun distanciaEnKm(puntoUno: Float, puntoDos: Float): Double {
        val millas = servicio.distanciaEnMilla(puntoUno, puntoDos, 18)
        val enteros = millas[0]
        val decimales = millas[1]
        return (enteros + decimales / 100.0) * 1.609344
    }
}