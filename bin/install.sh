#!/usr/bin/env bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}✓${NC} $1"; }
warn() { echo -e "${YELLOW}!${NC} $1"; }
error() { echo -e "${RED}✗${NC} $1"; exit 1; }

REPO="suprim-corp/suprim-cli"
INSTALL_DIR="$HOME/.suprim"
BIN_DIR="$HOME/.local/bin"

# Auto-detect latest version or use specified
if [ -z "$SUPRIM_VERSION" ]; then
    VERSION=$(curl -sI "https://github.com/$REPO/releases/latest" | grep -i "location:" | sed 's/.*tag\///' | tr -d '\r\n')
    [ -z "$VERSION" ] && error "Could not determine latest version"
else
    VERSION="$SUPRIM_VERSION"
fi
JAR_URL="https://github.com/$REPO/releases/download/${VERSION}/suprim-cli.jar"

check_java() {
    if ! command -v java &> /dev/null; then
        error "Java not found. Install Java 17+ first: https://adoptium.net"
    fi

    JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VER" -lt 17 ] 2>/dev/null; then
        error "Java 17+ required. Found: $JAVA_VER"
    fi
    info "Java detected"
}

download_jar() {
    info "Downloading Suprim CLI ${VERSION}..."
    mkdir -p "$INSTALL_DIR"

    if command -v curl &> /dev/null; then
        curl -fsSL "$JAR_URL" -o "$INSTALL_DIR/suprim-cli.jar" || error "Download failed. Check version: $VERSION"
    elif command -v wget &> /dev/null; then
        wget -q "$JAR_URL" -O "$INSTALL_DIR/suprim-cli.jar" || error "Download failed. Check version: $VERSION"
    else
        error "curl or wget required"
    fi
    info "Downloaded to $INSTALL_DIR"
}

create_wrapper() {
    mkdir -p "$BIN_DIR"

    cat > "$BIN_DIR/suprim" << 'WRAPPER'
#!/usr/bin/env bash
java -jar "$HOME/.suprim/suprim-cli.jar" "$@"
WRAPPER

    chmod +x "$BIN_DIR/suprim"
    info "Created $BIN_DIR/suprim"
}

setup_path() {
    if [[ ":$PATH:" == *":$BIN_DIR:"* ]]; then
        return
    fi

    SHELL_RC=""
    if [ -f "$HOME/.zshrc" ]; then
        SHELL_RC="$HOME/.zshrc"
    elif [ -f "$HOME/.bashrc" ]; then
        SHELL_RC="$HOME/.bashrc"
    elif [ -f "$HOME/.bash_profile" ]; then
        SHELL_RC="$HOME/.bash_profile"
    fi

    if [ -n "$SHELL_RC" ]; then
        if ! grep -q '.local/bin' "$SHELL_RC" 2>/dev/null; then
            echo 'export PATH="$HOME/.local/bin:$PATH"' >> "$SHELL_RC"
            warn "Added ~/.local/bin to PATH in $SHELL_RC"
        fi
    fi
}

main() {
    echo ""
    echo "Installing Suprim CLI ${VERSION}"
    echo ""

    check_java
    download_jar
    create_wrapper
    setup_path

    echo ""
    info "Installation complete!"
    echo ""
    echo "  suprim install migration   # Install migration tool"
    echo "  suprim --help"
    echo ""

    if [[ ":$PATH:" != *":$BIN_DIR:"* ]]; then
        warn "Restart terminal or run: source ~/.zshrc"
    fi
}

main
