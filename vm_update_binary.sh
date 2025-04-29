set -e
source vm.env

if [[ $EUID -eq 0 ]]; then
  echo "This script should not be run as root."
  echo "Running as root would cause build files to be owned by root."
  exit 1
fi

./gradlew linkDebugExecutableKarchinstall

sudo rm -rf "${SHARE_DIR:?}"/*
sudo cp "${BUILD_DIR:?}"/* "$SHARE_DIR"
