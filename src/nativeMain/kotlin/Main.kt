import lib.disk.parted.Parted
import lib.disk.parted.model.PartedDiskType
import lib.fatalError
import kotlin.system.exitProcess

fun main() {
    val device = Parted.getDevice("/dev/sda").getOrElse { fatalError(it) }

    val disk = device.getDisk().getOrElse {
            println("Partition table doesn't exist.. Creating partition table.\n")
            Parted.freshDisk(device, PartedDiskType.GPT)
                .getOrElse { error -> fatalError(error) }
                .also { disk -> disk.commit() }
    }
    device.close()

    println(disk.device.path)
}