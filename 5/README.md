
## test script input/output

Each input file consists of three JSON values: a Map object, a PlayerState object, and an indication of which connection the current player wishes to acquire.

The output is a Boolean, indicating whether the requested action is legal according to the rules with respect to the given map and state.

## to run with specific input file

1. run `make` within this directory
2. run `cat Tests/{input file} | ./xlegal`