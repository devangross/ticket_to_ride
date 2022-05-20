
## test script input/output

Each input file consists of three JSON values: a Map, an array of between 2-8 players, and an array of 250 colors.
The two Strings are names of cities that exist on the map. The three values specify the game map to be used for a game,
the participating players (in descending order of age), and which colored cards to hand out and in which order the referee hands out.

The output is either the JSON string "error: not enough destinations" or the outcome of running the game.

## to run with specific input file

1. run `make` within this directory
2. run `cat Tests/{input file} | ./xref`