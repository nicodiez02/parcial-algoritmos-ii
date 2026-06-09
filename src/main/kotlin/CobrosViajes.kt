class DescuentoDeEdad(private val viaje: IViaje) : IViaje by viaje {
    private val DESCUENTO_TREINTA_OFF = 0.30
    private val DESCUENTO_CINCUENTA_OFF = 0.50

    override fun costoViaje(pasajero: Cliente): Double {
        var porcentajeDescuento = 0.0

        if (pasajero.esMenorDeEdad()) porcentajeDescuento = DESCUENTO_TREINTA_OFF
        else if (pasajero.esJubilado()) porcentajeDescuento = DESCUENTO_CINCUENTA_OFF

        val descuento = porcentajeDescuento * viaje.costoViaje(pasajero)
        return viaje.costoViaje(pasajero) - descuento
    }
}

class HoraPico(private val viaje: IViaje) : IViaje by viaje {
    private fun esHoraPico(): Boolean {
        val hora = viaje.timestamp.hour
        return (hora in 7..10) || (hora in 16..19)
    }

    override fun costoViaje(pasajero: Cliente): Double {
        if (esHoraPico()) return viaje.costoViaje(pasajero) + 2.0
        return viaje.costoViaje(pasajero)
    }
}
