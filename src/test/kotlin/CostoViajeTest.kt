import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class CostoViajeTest : DescribeSpec({

    // ViajeProgramado exige que el timestamp sea futuro, así que se ancla a "ahora" en vez de a una fecha fija
    val unDiaFuturo = LocalDateTime.now().plusDays(10)
    val horaNoPico = unDiaFuturo.withHour(12).withMinute(0)
    val horaPicoMañana = unDiaFuturo.withHour(8).withMinute(0)
    val horaPicoTarde = unDiaFuturo.withHour(17).withMinute(0)

    val constitucion = Lugar("Constitución", floatArrayOf(-34.6276f, -58.3806f))
    val retiro = Lugar("Retiro", floatArrayOf(-34.5916f, -58.3736f))

    fun viajeARetiro(timestamp: LocalDateTime): Viaje = ViajeProgramado(
        origen = constitucion,
        destino = retiro,
        timestamp = timestamp,
        topeDeudor = -100.0,
        topeDeGente = 50.0
    )

    val joven = Cliente(edad = 15, email = "joven@mail.com")
    val adulto = Cliente(edad = 30, email = "adulto@mail.com")
    val jubilado = Cliente(edad = 70, email = "jubilado@mail.com")

    describe("el costo de un viaje sin condiciones adicionales") {

        it("es de 5 mangos, el costo base") {
            val viaje = ViajeBuilder(viajeARetiro(horaNoPico)).build()

            viaje.costoViaje(adulto) shouldBe (5.0 plusOrMinus 0.01)
        }
    }

    describe("el descuento por edad") {

        it("descuenta un 30% a una persona menor de edad: 5 - 30% = 3,50") {
            val viaje = ViajeBuilder(viajeARetiro(horaNoPico))
                .conDescuentoDeEdad()
                .build()

            viaje.costoViaje(joven) shouldBe (3.50 plusOrMinus 0.01)
        }

        it("descuenta un 50% a una persona jubilada: 5 - 50% = 2,50") {
            val viaje = ViajeBuilder(viajeARetiro(horaNoPico))
                .conDescuentoDeEdad()
                .build()

            viaje.costoViaje(jubilado) shouldBe (2.50 plusOrMinus 0.01)
        }

        it("no aplica descuento a una persona adulta que no es jubilada") {
            val viaje = ViajeBuilder(viajeARetiro(horaNoPico))
                .conDescuentoDeEdad()
                .build()

            viaje.costoViaje(adulto) shouldBe (5.0 plusOrMinus 0.01)
        }
    }

    describe("el recargo por hora pico") {

        it("cobra 2 mangos extra en el horario pico de la mañana (de 7 a 10)") {
            val viaje = ViajeBuilder(viajeARetiro(horaPicoMañana))
                .conCostoHoraPico()
                .build()

            viaje.costoViaje(adulto) shouldBe (7.0 plusOrMinus 0.01)
        }

        it("cobra 2 mangos extra en el horario pico de la tarde (de 16 a 19)") {
            val viaje = ViajeBuilder(viajeARetiro(horaPicoTarde))
                .conCostoHoraPico()
                .build()

            viaje.costoViaje(adulto) shouldBe (7.0 plusOrMinus 0.01)
        }

        it("no aplica recargo fuera del horario pico") {
            val viaje = ViajeBuilder(viajeARetiro(horaNoPico))
                .conCostoHoraPico()
                .build()

            viaje.costoViaje(adulto) shouldBe (5.0 plusOrMinus 0.01)
        }
    }

    describe("la combinación de descuentos y recargos depende del orden de aplicación") {

        it("descontando primero y recargando después: (5 * 70%) + 2 = 5,50") {
            val viaje = ViajeBuilder(viajeARetiro(horaPicoMañana))
                .conDescuentoDeEdad()
                .conCostoHoraPico()
                .build()

            viaje.costoViaje(joven) shouldBe (5.50 plusOrMinus 0.01)
        }

        it("recargando primero y descontando después: (5 + 2) * 70% = 4,90") {
            val viaje = ViajeBuilder(viajeARetiro(horaPicoMañana))
                .conCostoHoraPico()
                .conDescuentoDeEdad()
                .build()

            viaje.costoViaje(joven) shouldBe (4.90 plusOrMinus 0.01)
        }
    }
})
