package core

import models.Producto
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Tienda(private val inventario: MutableList<Producto>) {
    private val carrito = Carrito()
    private val moneda = NumberFormat.getCurrencyInstance(Locale("es", "SV")).apply {
        currency = Currency.getInstance("USD")
    }

    companion object {
        private const val IVA_TASA = 0.13
    }

    // Método principal: muestra menú y procesa opciones
    fun iniciar() {
        Logger.info("Aplicación iniciada.")
        var opcion: String?
        do {
            mostrarMenu()
            opcion = readlnOrNull()?.trim()
            when (opcion) {
                "1" -> mostrarInventario()
                "2" -> agregarAlCarrito()
                "3" -> eliminarDelCarrito()
                "4" -> mostrarCarrito()
                "5" -> facturar()
                "6" -> salirSeguro()
                else -> println("Opción no válida.")
            }
        } while (opcion != "6")
    }

    // Mostrar el menú principal
    private fun mostrarMenu() {
        println(
            """
            === Menú Tienda ===
            1. Ver productos
            2. Agregar al carrito
            3. Eliminar del carrito
            4. Ver carrito
            5. Pagar
            6. Salir
            """.trimIndent()
        )
        print("Elige opción: ")
    }

    // Mostrar lista de productos con stock
    private fun mostrarInventario() {
        println("\n--- Inventario ---")
        if (inventario.isEmpty()) {
            println("Sin productos.")
            return
        }
        inventario.forEach {
            println("${it.id}. ${it.nombre} | ${moneda.format(it.precio)} | Stock: ${it.stock}")
        }
    }

    // Agregar producto al carrito validando stock y cantidad
    private fun agregarAlCarrito() {
        mostrarInventario()
        val id = Entrada.leerEntero("ID producto: ") ?: return
        val prod = inventario.find { it.id == id } ?: return println("No existe.")
        if (prod.stock == 0) return println("Sin stock.")
        val cant = Entrada.leerEntero("Cantidad: ") ?: return
        if (cant <= 0 || cant > prod.stock) return println("Cantidad inválida.")
        runCatching {
            carrito.agregar(prod, cant)
            InventarioIO.guardar(inventario)
            println("Agregado ${cant}x '${prod.nombre}' al carrito.")
        }.onFailure {
            Logger.error("Error al agregar al carrito", it)
            println("Ocurrió un error al agregar.")
        }
    }

    // Eliminar un producto del carrito
    private fun eliminarDelCarrito() {
        mostrarCarrito()
        if (carrito.estaVacio()) return

        val id = Entrada.leerEntero("ID producto a eliminar: ") ?: return

        val item = carrito.contenido.find { it.producto.id == id }
        if (item == null) {
            println("Ese ID no está en el carrito.")
            return
        }

        val cant = Entrada.leerEntero("Cantidad (máx ${item.cantidad}): ") ?: return
        if (cant !in 1..item.cantidad) {
            println("Cantidad inválida. Debe estar entre 1 y ${item.cantidad}.")
            return
        }

        runCatching {
            carrito.eliminar(id, cant)
            InventarioIO.guardar(inventario)
            println("Eliminado ${cant}x '${item.producto.nombre}' del carrito.")
        }.onFailure {
            Logger.error("Error al eliminar del carrito", it)
            println("Ocurrió un error al eliminar.")
        }
    }

    // Mostrar contenido del carrito
    private fun mostrarCarrito() {
        if (carrito.estaVacio()) {
            println("Carrito vacío.")
            return
        }
        println("\n--- Carrito ---")
        carrito.contenido.forEach {
            println("${it.producto.id} - ${it.producto.nombre} | " +
                    "Cant: ${it.cantidad} | " +
                    "P.Unit: ${moneda.format(it.producto.precio)} | " +
                    "Subtotal: ${moneda.format(it.subtotal)}")
        }
        println("TOTAL (sin IVA): ${moneda.format(carrito.total())}")
    }

    // Generar factura con IVA incluido y limpiar carrito
    private fun facturar() {
        if (carrito.estaVacio()) return println("Carrito vacío.")
        val fmtFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fecha = LocalDateTime.now().format(fmtFecha)

        // Detalle
        println("\n========== FACTURA ==========")
        println("Fecha: $fecha")
        println("-----------------------------")
        carrito.contenido.forEach {
            val nombre = it.producto.nombre
            val cant = it.cantidad
            val pu = moneda.format(it.producto.precio)
            val sub = moneda.format(it.subtotal)
            println("$nombre")
            println("  Cant: $cant  | P.Unit: $pu  | Total: $sub")
        }
        val subtotal = carrito.total()
        val iva = subtotal * IVA_TASA
        val total = subtotal + iva
        println("-----------------------------")
        println("SUBTOTAL: ${moneda.format(subtotal)}")
        println("IVA (${(IVA_TASA*100).toInt()}%): ${moneda.format(iva)}")
        println("TOTAL A PAGAR: ${moneda.format(total)}")
        println("=============================\n")

        Logger.info("Factura generada. Items=${carrito.contenido.size} Subtotal=$subtotal IVA=$iva Total=$total")

        // Persistimos inventario final y vaciamos carrito para permitir seguir comprando
        InventarioIO.guardar(inventario)
        carrito.limpiar()
        println("Compra realizada. ¡Puede seguir comprando desde el menú!")
    }

    // Salida segura: si hay carrito pendiente se pregunta si cancelar
    private fun salirSeguro() {
        if (!carrito.estaVacio()) {
            println("Tiene productos en el carrito. ¿Desea cancelar la compra y restaurar stock? (s/n)")
            when (readlnOrNull()?.trim()?.lowercase()) {
                "s", "si", "sí" -> {
                    carrito.restaurarStockYLimpiar()
                    InventarioIO.guardar(inventario)
                    Logger.info("Salida con carrito cancelado. Stock restaurado.")
                }
                else -> Logger.info("Salida con carrito no confirmado (stock ya descontado).")
            }
        }
        println("Saliendo... ¡Gracias por su visita!")
    }
}
