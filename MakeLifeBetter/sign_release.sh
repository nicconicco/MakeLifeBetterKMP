#!/bin/bash
set -e

# ============================================================
# MakeLifeBetter - Script para gerar AAB assinado para Play Store
# ============================================================
# Este script:
#   1. Gera um keystore se nao existir
#   2. Configura local.properties com as credenciais
#   3. Gera o AAB (Android App Bundle) assinado
# ============================================================

KEYSTORE_FILE="release.jks"
LOCAL_PROPS="local.properties"
KEY_ALIAS="makelifebetter"
DNAME="CN=Carlos Nicolau Galves, O=MakeLifeBetter, L=Unknown, ST=Unknown, C=BR"

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo ""
echo "=========================================="
echo "  MakeLifeBetter - Release Builder"
echo "=========================================="
echo ""

# --- Passo 1: Verificar/Criar Keystore ---
if [ ! -f "$KEYSTORE_FILE" ]; then
    echo -e "${YELLOW}Keystore nao encontrado. Vamos criar um novo.${NC}"
    echo ""

    # Pedir senha ao usuario
    read -s -p "Digite uma senha para o keystore (minimo 6 caracteres): " STORE_PASSWORD
    echo ""
    read -s -p "Confirme a senha: " STORE_PASSWORD_CONFIRM
    echo ""

    if [ "$STORE_PASSWORD" != "$STORE_PASSWORD_CONFIRM" ]; then
        echo -e "${RED}Erro: As senhas nao coincidem.${NC}"
        exit 1
    fi

    if [ ${#STORE_PASSWORD} -lt 6 ]; then
        echo -e "${RED}Erro: A senha deve ter no minimo 6 caracteres.${NC}"
        exit 1
    fi

    KEY_PASSWORD="$STORE_PASSWORD"

    echo ""
    echo "Gerando keystore..."
    keytool -genkeypair \
        -v \
        -keystore "$KEYSTORE_FILE" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias "$KEY_ALIAS" \
        -storepass "$STORE_PASSWORD" \
        -keypass "$KEY_PASSWORD" \
        -dname "$DNAME"

    echo ""
    echo -e "${GREEN}Keystore criado com sucesso: $KEYSTORE_FILE${NC}"
    echo ""
    echo -e "${RED}IMPORTANTE: Guarde o arquivo $KEYSTORE_FILE e a senha em local seguro!${NC}"
    echo -e "${RED}Se voce perder o keystore, NAO podera atualizar o app na Play Store.${NC}"
    echo ""
else
    echo -e "${GREEN}Keystore encontrado: $KEYSTORE_FILE${NC}"
    echo ""

    # Pedir senha do keystore existente
    read -s -p "Digite a senha do keystore: " STORE_PASSWORD
    echo ""
    KEY_PASSWORD="$STORE_PASSWORD"
fi

# --- Passo 2: Atualizar local.properties ---
echo "Configurando local.properties..."

# Remover propriedades de release existentes
if [ -f "$LOCAL_PROPS" ]; then
    # Criar backup
    cp "$LOCAL_PROPS" "${LOCAL_PROPS}.bak"

    # Remover linhas antigas de RELEASE_*
    grep -v "^RELEASE_" "$LOCAL_PROPS" > "${LOCAL_PROPS}.tmp" || true
    mv "${LOCAL_PROPS}.tmp" "$LOCAL_PROPS"
else
    touch "$LOCAL_PROPS"
fi

# Adicionar propriedades de release
cat >> "$LOCAL_PROPS" << EOF
RELEASE_STORE_FILE=$KEYSTORE_FILE
RELEASE_STORE_PASSWORD=$STORE_PASSWORD
RELEASE_KEY_ALIAS=$KEY_ALIAS
RELEASE_KEY_PASSWORD=$KEY_PASSWORD
EOF

echo -e "${GREEN}local.properties atualizado.${NC}"
echo ""

# --- Passo 3: Gerar AAB ---
echo "Gerando AAB (Android App Bundle) assinado..."
echo ""

./gradlew :composeApp:bundleRelease

echo ""
echo "=========================================="
echo -e "${GREEN}BUILD CONCLUIDO!${NC}"
echo "=========================================="
echo ""

AAB_PATH="composeApp/build/outputs/bundle/release/composeApp-release.aab"
if [ -f "$AAB_PATH" ]; then
    echo -e "AAB gerado em: ${GREEN}$AAB_PATH${NC}"
    echo ""
    echo "Proximos passos para publicar na Play Store:"
    echo "  1. Acesse https://play.google.com/console"
    echo "  2. Crie um novo app (ou selecione o existente)"
    echo "  3. Va em 'Producao' > 'Criar nova versao'"
    echo "  4. Faca upload do arquivo AAB acima"
    echo "  5. Preencha as informacoes do app (descricao, screenshots, etc)"
    echo "  6. Envie para revisao"
    echo ""
    echo -e "${YELLOW}Dica: A Google recomenda usar 'App Signing by Google Play'.${NC}"
    echo -e "${YELLOW}Nesse caso, a Google gerencia a chave de assinatura final${NC}"
    echo -e "${YELLOW}e voce usa sua chave apenas como 'upload key'.${NC}"
else
    echo -e "${RED}Erro: AAB nao encontrado no caminho esperado.${NC}"
    echo "Verifique os logs do Gradle acima."
    exit 1
fi
