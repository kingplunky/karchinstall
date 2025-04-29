package lib.disk.parted.model

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import lib.disk.parted.exception.PartedDiskTypeException
import lib.fatalError
import libparted.PedDiskType
import libparted.ped_disk_type_get

@OptIn(ExperimentalForeignApi::class)
enum class PartedDiskType(val typeName: String) {
    GPT("gpt"),
    MSDOS("msdos"),
    BSD("bsd"),
    SUN("sun");

    val pointer: CPointer<PedDiskType> by lazy {
        ped_disk_type_get(typeName)
            ?: fatalError(PartedDiskTypeException("Invalid disk type '$typeName'."))
    }

    companion object {
        fun fromPointer(ptr: CPointer<PedDiskType>?): PartedDiskType {
            if (ptr == null) fatalError(
                PartedDiskTypeException(
                    "Null pointer received for PedDiskType. A valid disk type pointer was expected."
                )
            )

            val name = ptr.pointed.name?.toKString()
                ?: fatalError(PartedDiskTypeException(
                    "PedDiskType at address ${ptr.pointed} has a null 'name' field. This is unexpected."
                ))

            return entries.find { it.typeName.equals(name, ignoreCase = true) }
                ?: fatalError(PartedDiskTypeException("Unknown disk type '$name'."))
        }
    }
}

