#!/bin/bash

# Script para compilar os frameworks Kotlin Multiplatform para iOS
# Uso: ./build-ios.sh [simulator|device|all]

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretorio do projeto
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}[OK] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[AVISO] $1${NC}"
}

print_error() {
    echo -e "${RED}[ERRO] $1${NC}"
}

# Funcao para compilar frameworks do simulador
build_simulator() {
    print_header "Compilando frameworks para iOS Simulator (arm64)"

    echo "Compilando shared..."
    ./gradlew :shared:iosSimulatorArm64Binaries --no-daemon
    print_success "Shared framework compilado"

    echo "Compilando composeApp..."
    ./gradlew :composeApp:iosSimulatorArm64Binaries --no-daemon
    print_success "ComposeApp framework compilado"
}

# Funcao para compilar frameworks do device
build_device() {
    print_header "Compilando frameworks para iOS Device (arm64)"

    echo "Compilando shared..."
    ./gradlew :shared:iosArm64Binaries --no-daemon
    print_success "Shared framework compilado"

    echo "Compilando composeApp..."
    ./gradlew :composeApp:iosArm64Binaries --no-daemon
    print_success "ComposeApp framework compilado"
}

# Funcao para atualizar CocoaPods
update_pods() {
    print_header "Atualizando CocoaPods"

    if [ -d "iosApp" ]; then
        cd iosApp

        if command -v pod &> /dev/null; then
            pod install --repo-update
            print_success "Pods atualizados"
        else
            print_warning "CocoaPods nao encontrado. Instale com: sudo gem install cocoapods"
        fi

        cd "$PROJECT_DIR"
    else
        print_warning "Diretorio iosApp nao encontrado"
    fi
}

# Funcao para mostrar ajuda
show_help() {
    echo "Uso: ./build-ios.sh [opcao]"
    echo ""
    echo "Opcoes:"
    echo "  simulator    Compila apenas para iOS Simulator"
    echo "  device       Compila apenas para iOS Device"
    echo "  all          Compila para Simulator e Device"
    echo "  pods         Apenas atualiza os CocoaPods"
    echo "  clean        Limpa o build e recompila para simulator"
    echo "  help         Mostra esta ajuda"
    echo ""
    echo "Sem argumentos: compila para simulator (padrao)"
}

# Funcao para clean build
clean_build() {
    print_header "Limpando build"
    ./gradlew clean --no-daemon
    print_success "Build limpo"
    build_simulator
}

# Main
main() {
    print_header "Build iOS - MakeLifeBetter KMP"

    case "${1:-simulator}" in
        simulator)
            build_simulator
            ;;
        device)
            build_device
            ;;
        all)
            build_simulator
            build_device
            ;;
        pods)
            update_pods
            ;;
        clean)
            clean_build
            ;;
        help|--help|-h)
            show_help
            exit 0
            ;;
        *)
            print_error "Opcao desconhecida: $1"
            show_help
            exit 1
            ;;
    esac

    print_header "Build concluido com sucesso!"

    echo -e "Frameworks gerados em:"
    echo -e "  ${GREEN}shared/build/bin/iosSimulatorArm64/releaseFramework/${NC}"
    echo -e "  ${GREEN}composeApp/build/bin/iosSimulatorArm64/releaseFramework/${NC}"
    echo ""
    echo -e "Para abrir o projeto iOS no Xcode:"
    echo -e "  ${YELLOW}open iosApp/iosApp.xcworkspace${NC}"
}

main "$@"
