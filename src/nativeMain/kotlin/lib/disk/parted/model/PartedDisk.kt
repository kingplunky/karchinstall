package lib.disk.parted.model

import PartedDevice
import kotlinx.cinterop.*
import lib.toBoolean
import libparted.PedDisk
import libparted.ped_disk_commit

@OptIn(ExperimentalForeignApi::class)
class PartedDisk(val pointer: CPointer<PedDisk>) {
    private val disk = pointer.pointed

    val blockSizes: List<Int> get() = getBlockSizes();
    val device: PartedDevice get() = PartedDevice(disk.dev!!)
    val type: PartedDiskType get() = PartedDiskType.fromPointer(disk.type)

    private fun getBlockSizes(): MutableList<Int> {
        val blockSizes = mutableListOf<Int>()
        var current = disk.block_sizes
        while (current != null && current.pointed.value != 0) {
            blockSizes.add(current.pointed.value)
            println(current.pointed.value.toString())
            current = current.plus(1)
        }
        return blockSizes
    }

    fun commit() {
        ped_disk_commit(pointer)
    }
}

