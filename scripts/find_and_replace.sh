#!/bin/zsh

usage="Usage: ${0##*/} [-h] [-f file] [-o file] pattern1 replacement1 [pattern2 replacement2] ..."
help="Replace all occurrences of patterns with corresponding replacements in a template file.
Arguments:
  pattern1       the first pattern to replace
  replacement1   the replacement for pattern1
  pattern2       the second pattern to replace (optional)
  replacement2   the replacement for pattern2 (optional)
  ...
Options:
  -h, --help     display this help message and exit
  -f file        the input file (default: stdin)
  -o file        the output file (default: stdout)"

# Default values
input="/dev/stdin"
output="/dev/stdout"

# Parse options
while [[ $# -gt 0 ]]; do
  case $1 in
    -h|--help)
      echo "$help"
      exit 0
      ;;
    -f)
      input=$2
      shift
      shift
      ;;
    -o)
      output=$2
      shift
      shift
      ;;
    *)
      patterns+=($1)
      replacements+=($2)
      shift
      shift
      ;;
  esac
done

# Check that there are an even number of patterns and replacements
if [[ ${#patterns[@]} -ne ${#replacements[@]} ]]; then
  echo "Error: must provide an even number of pattern/replacement pairs"
  exit 1
fi

# Build sed command
sed_cmd="sed"
for (( i=1; i<=${#patterns[@]}; i++ )); do
  pattern=${patterns[$i]}
  replacement=${replacements[$i]}
  sed_cmd+=" -e s/$pattern/$replacement/g"
done

# Run sed command
eval "$sed_cmd <$input >$output"

