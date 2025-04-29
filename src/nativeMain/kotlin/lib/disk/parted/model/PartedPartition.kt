package lib.disk.parted.model

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import libparted.PedPartition

@OptIn(ExperimentalForeignApi::class)
class PartedPartition(val pointer: CPointer<PedPartition>) {
    val partition = pointer.pointed

    val disk: PartedDisk
        get() = PartedDisk(partition.disk!!)

    val previous: PartedPartition?
        get() = partition.prev?.let { PartedPartition(it) }

    val next: PartedPartition?
        get() = partition.next?.let { PartedPartition(it) }

    val number: Int
        get() = partition.num
}