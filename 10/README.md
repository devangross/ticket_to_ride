
## the two executable programs
* `xserver` starts the server
* `xclients` launches a number of clients and points them to a server

## test script inputs/outputs

`xserver` and `xclients` take the same input files. `xserver` only uses the third value (array of cards),
and `xclients` uses the first two values (game map and array of players).

The output of `xserver` is either the JSON string "error: not enough destinations" or the outcome of running the tournament.
The latter consists of a JSON array that contains two arrays: the first contains the names of the winner(s)
and the second contains the names of the misbehaving players. Any output of `xclients` is ignored.

## to run with specific input file

1. run `make` within this directory
2. in one window, run `cat Tests/{input file} | ./xserver`
3. concurrently in another window, run `cat Tests/{input file} | ./xclients`