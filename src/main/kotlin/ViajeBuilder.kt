class ViajeBuilder(private val viaje: Viaje) {

    fun conDescuentoDeEdad(): ViajeBuilder {
        viaje.agregarEstrategiaDeCobro(DescuentoDeEdad)
        return this
    }

    fun conCostoHoraPico(): ViajeBuilder {
        viaje.agregarEstrategiaDeCobro(HoraPico(viaje.timestamp.hour))
        return this
    }

    fun conTracking(tracking: TrackingDeViajes): ViajeBuilder {
        viaje.agregarObserver(TrackingObserver(tracking))
        return this
    }

    fun conClienteObserver(): ViajeBuilder{
        viaje.agregarObserver(ClienteObserver)
        return this
    }

    fun build(): Viaje = viaje
}
