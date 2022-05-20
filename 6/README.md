
## test script input/output

The input files consist of two JSON objects: a Map and the PlayerState. The former represents the game map,
the latter the state that the referee sends to a player when it is its turn. The given PlayerState must be consistent with the given Map.

Its expected output is an Action, determined by the player's current hand and move priorities.
An action is either the String "more cards" or an indication of which connection the current player wishes to acquire.

## to run with specific input file

1. run `make` within this directory
2. run `cat Tests/{input file} | ./xstrategy`