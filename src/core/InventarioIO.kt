package core

import models.Producto
import java.io.File

object InventarioIO {
    private const val FILE = "inventario.txt"

    // Cargar todos los productos desde el archivo de inventario
    fun cargar(): MutableList<Producto> {
        val f = File(FILE)
        if (!f.exists()) {
            Logger.warn("No existe $FILE, inventario vacío.")
            return mutableListOf()
        }
        val productos = mutableListOf<Producto>()
        f.forEachLine { linea ->
            val partes = linea.split(",")
            if (partes.size == 4) {
                runCatching {
                    productos += Producto(
                        id = partes[0].trim().toInt(),
                        nombre = partes[1].trim(),
                        precio = partes[2].trim().toDouble(),
                        stock = partes[3].trim().toInt()
                    )
                }.onFailure { Logger.error("Línea inválida en $FILE: '$linea'", it) }
            } else {
                Logger.warn("Formato incorrecto en $FILE: '$linea'")
            }
        }
        Logger.info("Cargados ${productos.size} productos desde $FILE")
        return productos
    }

    // Guardar el inventario actualizado en el archivo
    fun guardar(inventario: List<Producto>) {
        runCatching {
            File(FILE).printWriter().use { out ->
                inventario.forEach { p ->
                    out.println("${p.id},${p.nombre},${p.precio},${p.stock}")
                }
            }
            Logger.info("Inventario guardado en $FILE (${inventario.size} productos).")
        }.onFailure { Logger.error("No se pudo guardar inventario", it) }
    }
}
