set -e
source vm.env

./gradlew build

rm -rf "${SHARE_DIR:?}"/*
cp "${BUILD_DIR:?}"/* "$SHARE_DIR"
