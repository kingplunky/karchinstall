set -e
source vm.env

pacman -Sy archiso --noconfirm
mkdir -p "$TEST_DIR" "$WORK_DIR" "$SHARE_DIR"
mkdir -p "$WORK_DIR/airootfs/root"

packages_file="$WORK_DIR/packages.x86_64"
packages=(
    gcc
    git
)

cat <<- _EOF_ | tee "$WORK_DIR"/airootfs/root/.zprofile
  mkdir -p /media/share
  mount -t 9p -o trans=virtio host_share /media/share
  cd /media/share
_EOF_

for pkg in "${packages[@]}"; do
    echo "$pkg" >> "$packages_file"
done

cp -r /usr/share/archiso/configs/releng/* "$WORK_DIR"
sudo bash ./vm_update_binary.sh

cd "$WORK_DIR"
mkarchiso -v -w work/ -o out/ .

if [ ! -f "./test.qcow2" ]; then
	qemu-img create -f qcow2 ./test.qcow2 15G
fi

if lsof ./test.qcow2 &>/dev/null; then
    echo "test.qcow2 is locked by another process. Attempting to kill it..."
    fuser -k ./test.qcow2 || true
    sleep 1
fi

qemu-system-x86_64 \
        -cpu host \
        -enable-kvm \
        -machine q35,accel=kvm \
        -device intel-iommu \
        -m 8192 \
        -drive if=pflash,format=raw,readonly=on,file=/usr/share/edk2-ovmf/x64/OVMF_CODE.4m.fd  \
        -drive if=pflash,format=raw,readonly=on,file=/usr/share/edk2-ovmf/x64/OVMF_VARS.4m.fd \
        -device virtio-scsi-pci,bus=pcie.0,id=scsi0 \
        -device scsi-hd,drive=hdd0,bus=scsi0.0,id=scsi0.0,bootindex=2 \
        -drive file=./test.qcow2,if=none,format=qcow2,discard=unmap,aio=native,cache=none,id=hdd0 \
        -device virtio-scsi-pci,bus=pcie.0,id=scsi1 \
        -device scsi-cd,drive=cdrom0,bus=scsi1.0,bootindex=1 \
        -drive file="$(find "$WORK_DIR"/out/ -name "*.iso" | head -n 1)",media=cdrom,if=none,format=raw,cache=none,id=cdrom0 \
          -fsdev local,id=fsdev0,path="$SHARE_DIR",security_model=none -device virtio-9p-pci,id=fs0,fsdev=fsdev0,mount_tag=host_share \
        &
QEMU_PID=$!

# Wait for QEMU VM to start
sleep 2

if ps -p $QEMU_PID > /dev/null; then
    echo "QEMU started successfully, launching VNC viewer..."
    vncviewer localhost::5900
else
    echo "QEMU failed to start or crashed early."
fi

