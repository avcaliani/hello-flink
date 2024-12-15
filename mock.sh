#!/bin/bash
# Use this script to generate mocked data.
# Args
# 1 - Number of Lines
#
# Usage Example
# ./mock.sh 100
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <number_of_lines>"
    exit 1
fi

num_lines="$1"
output_dir="data/input/transactions"
output_file="$output_dir/$(date -u +%Y%m%d_%H%M%S).csv"

mkdir -p "$output_dir"
echo "user_id,tid,amount,created_at,tags" > "$output_file"

printf "Starting "
for i in $(seq 0 "$num_lines"); do
    user_id=$(uuidgen)
    tid=$(uuidgen)
    amount=$(echo "scale=2; 1 + ($RANDOM / 32767) * (100000 - 1)" | bc -l)
    created_at="$(date -u +%Y-%m-%dT%H:%M:%S)"
    tags="customer#not-admin#type-$RANDOM"
    echo "$user_id,$tid,$amount,$created_at,$tags" >> "$output_file"
    printf "."
done
echo " ✓"

# Notify the user
echo "CSV file '$output_file' generated with $num_lines lines."