interface EstrategiaDeCobro {
    fun aplicar(costoAcumulado: Double, pasajero: Cliente): Double
}

object DescuentoDeEdad : EstrategiaDeCobro {
    private val DESCUENTO_TREINTA_OFF = 0.30
    private val DESCUENTO_CINCUENTA_OFF = 0.50

    override fun aplicar(costoAcumulado: Double, pasajero: Cliente): Double {
        var porcentaje = 0.0

        if (pasajero.esMenorDeEdad()) porcentaje = DESCUENTO_TREINTA_OFF
        if (pasajero.esJubilado()) porcentaje = DESCUENTO_CINCUENTA_OFF

        val descuento = costoAcumulado * porcentaje
        return costoAcumulado - descuento
    }
}

class HoraPico(val hora: Int) : EstrategiaDeCobro {

    override fun aplicar(costoAcumulado: Double, pasajero: Cliente): Double {
        val esHoraPico = (this.hora in 7..10) || (this.hora in 16..19)
        return if (esHoraPico) costoAcumulado + 2.0 else costoAcumulado
    }
}
