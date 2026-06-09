class ViajeBuilder(private var viaje: IViaje) {

    fun conDescuentoDeEdad(): ViajeBuilder{
        this.viaje = DescuentoDeEdad(viaje)
        return this
    }

    fun conCostoHoraPico(): ViajeBuilder {
        this.viaje = HoraPico(viaje)
        return this
    }

    fun build(): IViaje {
        return viaje;
    }
}
