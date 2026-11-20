# marina-bay — Singapore's Marina Bay and its towers, built on the engine

Marina Bay as it lies: its land, its sea and the bay, the river's mouth, the gardens' lakes, its roads and their
bridges, and the city's towers standing on it. Each tower is a model you can walk into, break the glass of and ride
the lifts in. The bay is its own project. It builds on the engine as released to it and changes nothing outside this
folder.

It has **81 refined towers** so far: the Marina Bay Financial Centre and its homes, One Raffles Quay, The Sail, Asia
Square, Marina One, and every finished tower of 150 m and up round Raffles Place, Shenton Way, Anson Road, Tanjong
Pagar, Marina Centre and City Hall. The engine's own Marina Bay Sands towers stand among them.

## Build

It needs Java 25 and Maven. From the repository's root, the engine and the bay together:

```
mvn install
```

Or the bay alone, once the engine's libraries are installed (add `-o` to stay offline):

```
mvn -f marina-bay/pom.xml install
```

Either way the bay's jar is `marina-bay/bay-world/target/marina-bay.jar`. Close a running copy before rebuilding:
a running jar is locked.

## Launch

From the repository's root.

**The bay**, played: the bay with all its towers in the engine's game.

```
java -jar marina-bay/bay-world/target/marina-bay.jar bay
```

The avatar starts by the water, facing Marina Bay Sands. The business district lies to the south-west, Marina Centre
and City Hall to the north. Press J to put on the jet-pack and F to fly; Tab or the menu bar goes back to the
choice of demos. The full list of controls is in the [avatar demo's README](../avatar-demo/README.md).

**The bay's bench**: one tower at a time, where it stands, to look at and look into.

```
java -jar marina-bay/bay-world/target/marina-bay.jar
```

Choose a tower, cut it away over any storey, take its glass off, show the others round it. Its figures (storeys,
roof, floor area, each bank of lifts) are read out under it. Play shows that one tower alone on level ground, its
lifts at work.

**The tower bench**: the engine's own bench, with the bay's towers offered among its generic ones.

```
java -jar marina-bay/bay-world/target/marina-bay.jar towers
```

### Options

Set before `-jar`, for example `java -Dbay.tower=67 -Dbay.others=true -jar marina-bay/bay-world/target/marina-bay.jar`.

| Option | For | What it does |
|---|---|---|
| `-Dbay.tower=N` | bench | which tower is shown, 1 the first, in the list's order (`Refined`) |
| `-Dbay.upto=N` | bench | the storey it is cut away over, 0 the lobby |
| `-Dbay.glass=false` | bench | its glass left off |
| `-Dbay.others=true` | bench | the other towers shown round it |
| `-Dbay.yaw`, `-Dbay.pitch`, `-Dbay.distance` | bench | where it is seen from (degrees, degrees, metres) |
| `-Dbench.play=true` | bench | plays the tower shown at once |
| `-Dbench.snapshot=out.png`, `-Dbench.settle=ms` | bench | saves the window after so long and exits |
| `-Davatar.demo=NAME` | bay | another of the game's demos instead of the bay |
| `-Davatar.script="@90:1;W:3"` | bay | plays without a keyboard: keys held for seconds, `@90` turns, `^20` looks up |
| `-Davatar.snapshot=out.png` | bay | saves the window as the script ends and exits |

For example, turning toward the business district and saving what is seen:

```
java -Davatar.script="@120+^12:1;:15" -Davatar.snapshot=bay.png -jar marina-bay/bay-world/target/marina-bay.jar bay
```

## How it is laid out

| Module | What it holds |
|---|---|
| `bay-land` | the land, the water and the roads (`MarinaBay`, `MarinaBayRoads`), worked out from OpenStreetMap |
| `bay-buildings` | the towers: one class to a building or a family of them, and the single list of them (`Refined`) |
| `bay-world` | the bay's demo (`Bay`), its bench (`BayBench`), and the jar's start (`BayMain`) |
| `tools` | `OsmTerrain.java`, which turns a map extract into the land's lines |

Every tower is the engine's office tower model (`OfficeTower`), described by the bay's figures. How well each is
known varies:

- **From what their owners or architects publish**, the lengths written on their plans: MBFC's three towers, One
  Raffles Quay's two, Asia Square's two (scaled from their plans) and One Raffles Place's two.
- **On the map's outline** (`Mapped`), with their storeys, heights, floors' uses and lift zones as published where
  they are, and the rest estimated: every other tower. Offices take an estimated core (`Offices`, or its slim form
  for small floors), homes towers theirs (`Homes`). Each class says where its figures come from and which are
  estimates.

Each tower is placed in the bay by the list in `Refined`, which also gives it its own range of draw keys. Adding a
tower is a class for it and a line in that list.

## Tests

`mvn install` runs them. Each tower is checked against the figures published for it: its storeys, its height, and
where one of its floors is. Every tower is checked for the same things:

- each floor of offices, homes or hotel rooms is served by one zone of lifts;
- on every storey its core is inside its outline and clear of its glass;
- no two towers stand in each other;
- none needs more draw keys than it is given.

## Rules it keeps

- **Nothing outside `marina-bay/` is changed.** What the bay finds wrong or missing in the engine, it reports to the
  lab that owns it, and works with what it has until the fix is released.
- **The engine is pinned in one place**: `engine.version` in `marina-bay/pom.xml`.
- **No class is longer than 250 lines.**
- **Map data**: the land, the roads and the towers' outlines are derived from OpenStreetMap. Map data (c)
  OpenStreetMap contributors, available under the Open Database License (ODbL).
