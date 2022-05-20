
## test script input/output

Each input file consists of three JSON values: two Strings and a Map object. The two Strings are names of cities that exist on the map.

The expected output is a Boolean, indicating whether the two give cities are connected on the map.

## to run with specific input file

1. run `make` within this directory
2. run `cat Tests/{input file} | ./xmap`