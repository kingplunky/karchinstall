package lib.disk.parted.model

enum class PartedPartitionType(val flag: Int) {
    PED_PARTITION_NORMAL(0x00),
    PED_PARTITION_LOGICAL(0x01),
    PED_PARTITION_EXTENDED(0x02),
    PED_PARTITION_FREESPACE(0x04),
    PED_PARTITION_METADATA(0x08),
    PED_PARTITION_PROTECTED(0x10);
}