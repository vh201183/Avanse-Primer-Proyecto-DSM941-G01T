package core

object Entrada {
    // Leer un número entero desde consola
    fun leerEntero(pedir: String): Int? {
        print(pedir)
        val v = readlnOrNull()?.trim()
        return v?.toIntOrNull().also {
            if (it == null) Logger.warn("Entrada inválida para entero: '$v'")
        }
    }

    // Leer un texto desde consola
    fun leerCadena(pedir: String): String? {
        print(pedir)
        val v = readlnOrNull()?.trim()
        if (v.isNullOrEmpty()) Logger.warn("Entrada vacía para cadena")
        return v
    }
}
