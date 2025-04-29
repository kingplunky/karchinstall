import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import lib.disk.parted.exception.PartedDiskException
import lib.disk.parted.model.PartedDisk
import lib.toBoolean
import libparted.*

@OptIn(ExperimentalForeignApi::class)
class PartedDevice(val pointer: CPointer<PedDevice>): AutoCloseable {
    private val device: PedDevice = pointer.pointed

    val model: String?
        get() = device.model?.toKString()
    val path: String?
        get() = device.path?.toKString()
    val readOnly: Boolean
        get() = device.read_only.toBoolean()

    fun getDisk(): Result<PartedDisk> {
        val diskType = ped_disk_probe(pointer)
        if (diskType == null) {
            return Result.failure(PartedDiskException("Partition table doesn't exist for $path"))
        }
        val pedDiskPointer = ped_disk_new(pointer)
        if (pedDiskPointer == null) {
            return Result.failure(PartedDiskException("Failed to get disk for device $path"))
        }
        return Result.success(PartedDisk(pedDiskPointer))
    }

    override fun close() {
        ped_device_destroy(pointer)
    }

}