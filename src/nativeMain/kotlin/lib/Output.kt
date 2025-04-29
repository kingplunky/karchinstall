package lib

import kotlin.system.exitProcess


fun fatalError(error: Throwable): Nothing {
    val brightRedBold = "\u001B[1;91m"
    val reset = "\u001B[0m"
    println("${brightRedBold}${error::class.simpleName}${reset}: ${error.message}")
    exitProcess(1)
}
