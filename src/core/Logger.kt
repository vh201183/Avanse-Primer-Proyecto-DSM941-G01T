package core

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val file = File("app.log")
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Registrar información general
    fun info(msg: String)  = write("INFO", msg)

    // Registrar advertencias
    fun warn(msg: String)  = write("WARN", msg)

    // Registrar errores con o sin excepción
    fun error(msg: String, ex: Throwable? = null) {
        write("ERROR", msg + (ex?.let { " | ${it::class.simpleName}: ${it.message}" } ?: ""))
    }

    // Método privado para escribir en el archivo
    private fun write(level: String, msg: String) {
        val line = "[${LocalDateTime.now().format(fmt)}][$level] $msg"
        file.appendText(line + System.lineSeparator())
    }
}
