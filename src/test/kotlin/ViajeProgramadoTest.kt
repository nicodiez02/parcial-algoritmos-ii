import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDateTime

class ViajeProgramadoTest : DescribeSpec({

    val obelisco = Lugar("Obelisco", floatArrayOf(-34.6037f, -58.3816f))
    val corrientesYDorrego = Lugar("Corrientes y Dorrego", floatArrayOf(-34.5885f, -58.4565f))

    // saldo "neutro" (mayor al tope deudor de 100) para no interferir con la regla que no se está probando
    fun cliente(saldo: Double = 150.0): Cliente {
        val cliente = Cliente(edad = 30, email = "pasajero@mail.com")
        cliente.tarjeta.saldo = saldo
        return cliente
    }

    fun viajeQueSale(en: LocalDateTime, topeDeGente: Int = 10) = ViajeProgramado(
        origen = obelisco,
        destino = corrientesYDorrego,
        timestamp = en,
        topeDeudor = -100.0,
        topeDeGente = topeDeGente
    )

    describe("agregar pasajeros a un viaje programado") {

        describe("se permite el ingreso hasta 2 horas antes de la partida") {

            it("permite agregar un pasajero cuando faltan más de 2 horas para el viaje") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5))

                shouldNotThrowAny { viaje.agregarPasajero(cliente()) }
            }

            it("no permite agregar un pasajero cuando faltan menos de 2 horas para el viaje") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusMinutes(15))

                shouldThrow<BusinessException> { viaje.agregarPasajero(cliente()) }
            }
        }

        describe("el tope de gente") {

            it("permite agregar pasajeros mientras no se alcance el tope") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5), topeDeGente = 2)

                shouldNotThrowAny {
                    viaje.agregarPasajero(cliente())
                    viaje.agregarPasajero(cliente())
                }
            }

            it("no permite agregar más pasajeros una vez alcanzado el tope de gente") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5), topeDeGente = 2)
                viaje.agregarPasajero(cliente())
                viaje.agregarPasajero(cliente())

                shouldThrow<BusinessException> { viaje.agregarPasajero(cliente()) }
            }

            it("permite volver a agregar un pasajero si alguien se bajó y liberó un lugar") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5), topeDeGente = 2)
                val primero = cliente()
                val segundo = cliente()
                viaje.agregarPasajero(primero)
                viaje.agregarPasajero(segundo)
                viaje.removePasajero(primero)

                shouldNotThrowAny { viaje.agregarPasajero(cliente()) }
            }
        }

        describe("el saldo deudor permitido (tope configurable por viaje, sin contar el costo del viaje en sí)") {

            it("una persona que debe 90 mangos puede tomar un viaje con tope deudor de 100") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5))

                shouldNotThrowAny { viaje.agregarPasajero(cliente(saldo = -90.0)) }
            }

            it("una persona que debe 120 mangos no puede tomar un viaje con tope deudor de 100") {
                val viaje = viajeQueSale(en = LocalDateTime.now().plusHours(5))

                shouldThrow<BusinessException> { viaje.agregarPasajero(cliente(saldo = -120.0)) }
            }
        }
    }
})
