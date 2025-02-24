#!/bin/bash

set -e

OUT_DIR="target/classes"

echo "outdir= ${OUT_DIR}"

CHAT_CLIENT_CLASS="org.chat.start.client_1"

echo "Uruchamianie Client1..."
java -cp $OUT_DIR $CHAT_CLIENT_CLASS