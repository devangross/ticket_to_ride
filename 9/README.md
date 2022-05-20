
## test script input/output

Each input file consists of three JSON values: a Map, an array of between 2-8 players, and an array of 250 colors.
The two Strings are names of cities that exist on the map. The three values specify the game map to be used for a game,
the participating players (in descending order of age), and which colored cards to hand out and in which order the referee hands out.

The output is either the JSON string "error: not enough destinations" or the outcome of running the tournament.
The latter consists of a JSON array that contains two arrays: the first contains the names of the winner(s)
and the second contains the names of the misbehaving players.
The manager does not run any games if the chosen map doesn't support running all games.

## to run with specific input file

1. run `make` within this directory
2. run `cat Tests/{input file} | ./xmanager`