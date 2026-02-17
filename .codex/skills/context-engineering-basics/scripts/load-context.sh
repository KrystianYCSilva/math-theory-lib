#!/bin/bash
# Simple Context Loader Script
# Usage: ./load-context.sh [topic]

CONTEXT_DIR=".context"
OUTPUT_FILE="context_dump.txt"

echo "Loading T0 (Constitution)..." > $OUTPUT_FILE
cat CONSTITUTION.md >> $OUTPUT_FILE 2>/dev/null

echo -e "
Loading T1 (Standards)..." >> $OUTPUT_FILE
# Load all standards
for f in $CONTEXT_DIR/standards/*.md; do
    echo "--- File: $f ---" >> $OUTPUT_FILE
    cat "$f" >> $OUTPUT_FILE
    echo -e "
" >> $OUTPUT_FILE
done

if [ ! -z "$1" ]; then
    echo -e "
Loading Dynamic Context for topic: $1..." >> $OUTPUT_FILE
    # Simple keyword search in T2/T3
    grep -lR "$1" $CONTEXT_DIR/_meta $CONTEXT_DIR/patterns | while read match; do
        echo "--- File: $match ---" >> $OUTPUT_FILE
        cat "$match" >> $OUTPUT_FILE
        echo -e "
" >> $OUTPUT_FILE
    done
fi

echo "Context generated at $OUTPUT_FILE"
