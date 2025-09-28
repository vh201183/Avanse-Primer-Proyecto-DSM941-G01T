import core.Tienda
import core.InventarioIO

// Punto de entrada de la aplicación
fun main() {
    val inventario = InventarioIO.cargar()   // Cargar inventario desde archivo
    val tienda = Tienda(inventario)          // Inicializar tienda
    tienda.iniciar()                         // Iniciar bucle principal
}
