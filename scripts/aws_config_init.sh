#!/bin/zsh

set -euo pipefail

# Check that we have one argument
if [[ $# -ne 1 ]]; then
  echo "Error: Please provide one argument - your username" >&2
  exit 1
fi

# Display usage and help
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  echo "Usage: $0 <username>"
  echo "This script initializes your AWS configuration by replacing the ETELIE_USERNAME pattern in the template file with your username, and saves the output to ~/.aws/config"
  exit 0
fi

username="$1"
template_file="$(dirname "$0")/resources/aws_config.template"
output_file="$HOME/.aws/config"

# Confirm with the user that they want to overwrite the output file
if [[ -e "$output_file" ]]; then
  echo "The file $output_file already exists. Are you sure you want to overwrite it? (y/N)"
  read confirm
  if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
    echo "Exiting without making changes"
    exit 0
  fi
fi

# Replace the pattern in the template file
echo "Initializing AWS configuration for user $username"
if "$(dirname "$0")/find_and_replace.sh" -f "$template_file" -o "$output_file" "ETELIE_USERNAME" "$username"; then
  echo "AWS configuration successfully initialized at $output_file"
else
  echo "Error: Failed to initialize AWS configuration" >&2
fi
