package lib.disk.parted

import PartedDevice
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import lib.disk.parted.exception.PartedDeviceException
import lib.disk.parted.exception.PartedDiskException
import lib.disk.parted.model.PartedDisk
import lib.disk.parted.model.PartedDiskType
import libparted.*

@OptIn(ExperimentalForeignApi::class)
object Parted {
    private fun refreshDevices() {
        ped_device_probe_all()
    }

    fun getDevices(): List<PartedDevice> {
        refreshDevices()
        val devices = mutableListOf<PartedDevice>()

        var dev: CPointer<PedDevice>? = null
        while (true) {
            dev = ped_device_get_next(dev)
            if (dev == null) break

            val device = PartedDevice(dev)
            devices.add(device)
        }

        return devices
    }

    fun getDevice(devicePath: String): Result<PartedDevice> {
        refreshDevices()
        val dev = ped_device_get(devicePath)
        if (dev == null) {
            return Result.failure(PartedDeviceException("Device $devicePath not found!"))
        }

        return Result.success(PartedDevice(dev))
    }

    fun freshDisk(device: PartedDevice, diskType: PartedDiskType): Result<PartedDisk> {
        val disk = ped_disk_new_fresh(device.pointer, diskType.pointer)
        if (disk == null) {
            return Result.failure(
                PartedDiskException(
                    "Failed to create disk on device ${device.path} with type ${diskType.typeName}"
                )
            )
        }
        return Result.success(PartedDisk(disk))
    }
}