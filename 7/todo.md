# TODO

BUGS
- Data Representation:
  - [X] get rid of null destinations by replacing with list of destinations
  - [X] accommodate big integer for `colorCardCount`
  - [X] failed map with empty connections
- Functionality:
  - [X] catch all exceptions that a player can throw from their strategy
  - [X] fix relative paths in failing tests

REWORKED
- Data Representation:
  - [X] add ranking functionality (changed to a mapping of scores to players with that score)
- Functionality:
  - [X] add ranking functionality (allowed the referee to access final ranking of all players)
  - [X] add played with no change game-ending condition
  - [X] add comprehensive unit tests for `canAcquire` method
  - [X] refactor scoring functionality into one method per task
  - [X] remove redundant class `KruskalConnected`
  - [X] refactor dependency management to use Maven
