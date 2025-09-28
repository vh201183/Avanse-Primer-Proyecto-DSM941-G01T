package models

// Relaciona un producto con la cantidad seleccionada por el cliente
data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
) {
    // Subtotal calculado automáticamente (precio * cantidad)
    val subtotal: Double get() = producto.precio * cantidad
}
