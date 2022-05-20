# BUGS

Representation of `PlayerHand` destinations as distinct values allowed for null pointer exceptions when destinations are never selected.
<br />
[git commit of failing unit test prior to fix](https://github.ccs.neu.edu/CS4500-F21/huron/commit/d17862de96b50012c9c39272a50171db71f1452f)
<br />
Refactor `PlayerHand` destinations to be represented by a list, initialized as empty.
<br />
[git commit to fix the bug](https://github.ccs.neu.edu/CS4500-F21/huron/commit/5d0951e7c53f4eb5f22d63166ca46582e10c5b2a)

An `IPlayer` throwing exceptions from their strategy (choosing destinations/moves) crashes the `RefereeAgent`.
<br />
[git commit of failing unit test prior to fix](https://github.ccs.neu.edu/CS4500-F21/huron/commit/68eb8ea81d06d186d007db084d7051190dda7f89)
<br />
Added catches for all exceptions that an `IPlayer` can throw from their strategy.
<br />
[git commit to fix the bug](https://github.ccs.neu.edu/CS4500-F21/huron/commit/81040fcb2ad1a755813cdc7f6cc8b4fe2a2658d3)

Construction of a `TrainsMap` with no connections fails.
<br />
[git commit of failing unit test prior to fix](https://github.ccs.neu.edu/CS4500-F21/huron/commit/015f415722adc17f1f5f6e7dfc247b48ec2e8c9d)
<br />
Remove the exception that prevented a `TrainsMap` with no connections from being constructed.
<br />
[git commit to fix the bug](https://github.ccs.neu.edu/CS4500-F21/black-canyon/commit/dce79a346e8dcdcce6a8ca96958f60282d45950e)

A Big integer number of cards breaks parsing.
<br />
[git commit of failing unit test prior to fix](https://github.ccs.neu.edu/CS4500-F21/huron/commit/d17862de96b50012c9c39272a50171db71f1452f)
<br />
Truncate big integer to regular integer.
<br />
[git commit to fix the bug](https://github.ccs.neu.edu/CS4500-F21/huron/commit/210382a3baf77034e0f99da94dedb43f244244b1)

