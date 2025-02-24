#!/bin/bash

set -e

OUT_DIR="target/classes"

echo "outdir= ${OUT_DIR}"

CHAT_SERVER_CLASS="org.chat.start.Server"

echo "Uruchamianie ChatServer..."
java -cp $OUT_DIR $CHAT_SERVER_CLASS &

SERVER_PID=$!
echo "ChatServer działa (PID: $SERVER_PID)"

