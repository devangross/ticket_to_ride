# The Game Trains

## Game Overview
The Trains game is about acquiring train lines and getting from one place to another.
The central game piece is a map of places and direct train connections between places.
Each connection has a color and a length. Two cities can be connected by one color at most once.

A referee gives players colored cards and rails which are hidden from other players and used to acquire connections on the map.
Acquired connections cannot be used by other players. Players are also tasked with creating routes between a few specified sets
of cities that are assigned to them.

Points are earned by acquiring connections, connecting destinations, and holding the longest continuous path.

## This Codebase

All source code is located in `Trains/Other/Trains/src`.

This codebase contains representations of game pieces, a rulebook, and various agents with gameplay strategies.
Agents include players, referees, and tournament managers. The entire prototype is broken into server and clients pieces.
The server signs up clients and calls on the tournament manager to run a tournament. Each client connects to the server,
and the remote proxy pattern is used to insert communication components and separate the logical components
of the tournament and the players.

## To run and observe milestones
Direrctories `3`-`10` (barring `7`) contain executables that demonstrate various milestones of development.
The final demonstration of the client and server is in directory `10`.
Each directory contains a README further detailing how to run its specific executable(s) and what they are demonstrating.

## To run test suites
All unit tests are located in `Trains/Other/Trains/test`

1. Navigate to the `Trains` directory and run `make`
2. run `xtest`

### Attributions
This code was written with a partner for Northeastern University's CS4500 Software Development.