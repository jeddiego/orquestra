#!/bin/bash

# Configuración
JAR_NAME="Orquestra-all.jar"
REMOTE_USER="root"
REMOTE_HOST="66.179.94.168"
REMOTE_PATH="/home/divinolabs"
SERVICE_NAME="orquestra"
PASSWORD="j1KuaIL4"

# 1. Compilar con Shadow
./gradlew clean shadowJar || { echo "Falló la compilación"; exit 1; }

# 2. Subir con SCP usando sshpass
sshpass -p "$PASSWORD" scp "build/libs/$JAR_NAME" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH" || {
  echo "Falló el SCP"; exit 1;
}

# 3. Reiniciar el servicio remoto
sshpass -p "$PASSWORD" ssh "$REMOTE_USER@$REMOTE_HOST" "sudo systemctl restart $SERVICE_NAME" || {
  echo "Falló el reinicio del servicio"; exit 1;
}

echo "✅ Despliegue completo"
