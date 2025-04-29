package lib.cinterop

interface CObject: AutoCloseable {
    val closed: Boolean
}