interface IObserver {
    fun nuevoViajeCompletado(viaje: Viaje, pasajeros: MutableList<Cliente>)
}

data class Mail(val para:String, val de: String, val asunto: String, val cuerpo: String) {}
object MailSender {
    fun enviar(email: Mail) = println("Esto es un envio de email :D")
}

object ClienteObserver: IObserver {
    override fun nuevoViajeCompletado(viaje: Viaje, pasajeros: MutableList<Cliente>) {
        pasajeros
            .filter { it.saldo() < viaje.topeDeDeuda }
            .forEach { MailSender.enviar(Mail(it.email,"adorni@gob.com.ar","","")) }
    }
}

class TrackingObserver(val herramientaDeTrackeo: TrackingDeViajes): IObserver{
    override fun nuevoViajeCompletado(viaje: Viaje, pasajeros: MutableList<Cliente>){
        pasajeros.forEach { this.herramientaDeTrackeo.guardarViaje(it, viaje) }
    }
}
