import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDateTime

class ItinerarioTest : DescribeSpec({

    val plazaMiserere = Lugar("Plaza Miserere", floatArrayOf(-34.6092f, -58.4173f))
    val rosedal = Lugar("Rosedal", floatArrayOf(-34.5697f, -58.4144f))
    val corrientesYMaipu = Lugar("Corrientes y Maipú", floatArrayOf(-34.6010f, -58.3816f))
    val avSanMartinYCarlosLopez = Lugar("Av. San Martín y Carlos López", floatArrayOf(-34.5763f, -58.4459f))

    fun cliente(saldo: Double, antiguedadEnMeses: Long): Cliente {
        val cliente = Cliente(edad = 30, email = "pasajero@mail.com")
        cliente.tarjeta = TarjetaViajera(
            fechaDeAlta = LocalDateTime.now().minusMonths(antiguedadEnMeses),
            saldo = saldo
        )
        return cliente
    }

    describe("agregar pasajeros a un itinerario") {

        it("permite agregar un pasajero con saldo suficiente y tarjeta de más de 6 meses de antigüedad") {
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)

            shouldNotThrowAny { itinerario.agregarPasajero(cliente(saldo = 100.0, antiguedadEnMeses = 7)) }
        }

        it("no permite agregar un pasajero sin saldo suficiente para cubrir el costo del viaje") {
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)

            shouldThrow<BusinessException> { itinerario.agregarPasajero(cliente(saldo = 0.0, antiguedadEnMeses = 7)) }
        }

        it("no permite agregar un pasajero cuya tarjeta tiene menos de 6 meses de antigüedad") {
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)

            shouldThrow<BusinessException> { itinerario.agregarPasajero(cliente(saldo = 100.0, antiguedadEnMeses = 3)) }
        }
    }

    describe("anidamiento de itinerarios") {

        it("permite construir un itinerario cuyos puntos intermedios son a su vez otros itinerarios") {
            val tramoCorrientes = Itinerario(origen = corrientesYMaipu, destino = avSanMartinYCarlosLopez)

            shouldNotThrowAny {
                Itinerario(
                    origen = plazaMiserere,
                    destino = rosedal,
                    puntosIntermedios = mutableListOf(tramoCorrientes)
                )
            }
        }
    }
})
