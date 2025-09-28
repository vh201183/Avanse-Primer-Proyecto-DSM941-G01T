package models

// Representa un producto dentro del inventario de la tienda
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    var stock: Int
)
