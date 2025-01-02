#!/bin/bash

set -e

OUT_DIR="target/classes"

echo "outdir= {$OUT_DIR}"

CHAT_SERVER_CLASS="org.chat.ChatServer"
TESTS_CLASS="org.chat.tests"

echo "Uruchamianie ChatServer..."
java -cp $OUT_DIR $CHAT_SERVER_CLASS &

SERVER_PID=$!

echo "ChatServer działa"

echo "Uruchamianie klasy tests..."
java -cp $OUT_DIR $TESTS_CLASS

echo "Zakończono działanie klasy tests."

#kill $SERVER_PID
echo "Zatrzymano ChatServer."
