package core

import models.ItemCarrito
import models.Producto

class Carrito {
    private val items = mutableListOf<ItemCarrito>()
    val contenido: List<ItemCarrito> get() = items

    // Agregar producto al carrito y descontar stock
    fun agregar(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "Cantidad debe ser positiva" }
        val existente = items.find { it.producto.id == producto.id }
        if (existente != null) existente.cantidad += cantidad
        else items += ItemCarrito(producto, cantidad)
        producto.stock -= cantidad
        Logger.info("Agregado al carrito ${cantidad}x '${producto.nombre}' (id=${producto.id})")
    }

    // Eliminar un producto del carrito y devolver stock
    fun eliminar(idProducto: Int, cantidad: Int) {
        val item = items.find { it.producto.id == idProducto } ?: return
        require(cantidad in 1..item.cantidad) { "Cantidad inválida" }
        item.producto.stock += cantidad
        if (cantidad == item.cantidad) items.remove(item) else item.cantidad -= cantidad
        Logger.info("Eliminado del carrito ${cantidad}x '${item.producto.nombre}' (id=$idProducto)")
    }

    // Calcular total sin IVA
    fun total(): Double = items.sumOf { it.subtotal }

    // Restaurar stock de productos y vaciar carrito
    fun restaurarStockYLimpiar() {
        items.forEach { it.producto.stock += it.cantidad }
        items.clear()
    }

    // Vaciar carrito
    fun limpiar() = items.clear()

    // Saber si está vacío
    fun estaVacio(): Boolean = items.isEmpty()
}
