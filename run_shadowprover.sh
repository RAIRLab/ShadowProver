#!/bin/sh

set -o errexit
set -o nounset

show_usage() {
    echo "Usage: ./run_shadowprover.sh [FILENAME]"
    exit 1
}

# Check argument count
if [ "$#" -ne 1 ]; then
    show_usage
fi

if ! command -v mvn > /dev/null; then
    echo "Maven (mvn) is not found in the path"
    exit 1
fi

mvn -q exec:java -Dexec.mainClass="org.rairlab.shadow.prover.Runner" -Dexec.args="$1"
