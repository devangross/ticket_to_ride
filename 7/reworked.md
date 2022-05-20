# REWORKED

We did not have ranking functionality in the `Referee`, only for finding the winner(s).
<br />
We refactored the `Scoring` class to return a mapping of scores to all players who earned that score.
<br />
Git Commit: [added ranking functionality](https://github.ccs.neu.edu/CS4500-F21/huron/commit/e521ce2fe0346cb2308c8ba5dd08b6422fa22957)

We did not account for a round of no change ending the game.
<br />
We added the game-ending condition in which all players play a turn with no change.
<br />
Git Commit: [add game-ending condition](https://github.ccs.neu.edu/CS4500-F21/huron/commit/467e1b9d685258be06c735f424e4df0b192c2a6c)

We did not include comprehensive unit tests for `canAcquire`.
<br />
We added tests for this method.
<br />
Git Commit: [including comprehensive unit tests](https://github.ccs.neu.edu/CS4500-F21/black-canyon/commit/f53a9a43d9b56ea12595a0f1ecb260dfbed35b11)

We had a very large scoring method that was responsible for several distinct tasks in the `Scoring` class.
<br />
We refactored scoring functionality so that each method is responsible for task.
<br />
Git Commit: [refactoring scoring](https://github.ccs.neu.edu/CS4500-F21/huron/commit/015f415722adc17f1f5f6e7dfc247b48ec2e8c9d)

We had two nearly identical classes: `Kruskal` and `KruskalConnected`.
<br />
We removed the redundant class `KruskalConnected`.
<br />
Git Commit: [deleting KruskalConnected class](https://github.ccs.neu.edu/CS4500-F21/huron/commit/b4deb16b81110f17ad0d7ef4813f84b3df57d675)

We did not have any encapsulation of the `IPlayer` in the `RefereeAgent`.
<br />
We added `eliminatePlayer` in the `RefereeAgent` to better encapsulate `IPlayer`.
<br />
Git Commit: [encapsulate player interactions](https://github.ccs.neu.edu/CS4500-F21/huron/commit/81040fcb2ad1a755813cdc7f6cc8b4fe2a2658d3)

We were building and committing separate JARs for each task, and the auxiliary files to do so cluttered the codebase.
<br />
We refactored all dependency management to use Maven.
<br />
Git Commit: [introduced Maven](https://github.ccs.neu.edu/CS4500-F21/huron/commit/d10cdbc6d4f208a81d0b3f63ac8350c308b28c13)