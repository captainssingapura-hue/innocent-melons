# qingming — Along the River During the Qingming Festival, in 3D

A world after Zhang Zeduan's scroll 清明上河图: the reach of the Bian River (汴河) from the Rainbow Bridge (虹桥) to the
city gate. The scroll is not a map, so the plan is read from it and its sizes come from what is written and dug up. It
is built on the engine as released to it, as the bay is.

**So far: the terrain and the Rainbow Bridge.** A level plain 1.1 km by 0.5 km, the river cut into it with its banks
and water, its towpaths, the earth road from the country to the bridge and the street from it to the gate, and level
ground where the city's wall and its gate will stand. Over the river, the Rainbow Bridge: its woven timber arch, its
deck to walk up and over, its railings and its four poles. The wall and gate, buildings, boats and people are to come.

## Build and launch

Java 25 and Maven. From the repository's root:

```
mvn install
```

or the world alone, once the engine is installed: `mvn -f qingming/pom.xml install`. Then:

```
java -jar qingming/qm-world/target/qingming.jar
```

The avatar starts on the street north of the bridge. Press J to put on the jet-pack and F to fly. The gate's ground is
540 m west of the bridge. Unattended runs take the game's `-Davatar.script` and `-Davatar.snapshot`, as the bay's do.

## Where the sizes come from

| What | Size here | From |
|---|---|---|
| The bridge's span | 22 m between the river's banks | about 20 to 22 m by the bridge's historians (唐寰澄 and others), read off the scroll |
| The bridge's width | 8 m, the road's width | the same reckonings |
| Its arch | 21 groups of squared logs 40 cm thick, 45 cm apart: 11 of four members, 10 of three, tied by 5 logs across; its crown some 5 m over the water | 茅以升's reading of the scroll, via the timber-arch literature; 唐寰澄 |
| Its railings and poles | 23 posts a side, 2 taller ones at each end; 4 poles with a cross-bar, a disc and a crane | read off the scroll; their heights estimates |
| The river's water | 1.86 m deep | the 6 chi the Song history (宋史) has it held at for loaded boats |
| Its towpaths | 4.65 m wide | the dike tops the Song history keeps along it within the capital |
| The gate | an old-city gate, the bridge a short way outside it | the Palace Museum's reading (东角子门, the bridge 上土桥); the distance shortened as the scroll shortens it |
| The land | level | the plain round Kaifeng |

## Layout

| Module | What it holds |
|---|---|
| `qm-land` | the land (`Reach`), the river (`Rivers`), the roads (`Streets`) |
| `qm-structures` | what stands in it: the Rainbow Bridge (`RainbowBridge`), its timbers (`Timbers`) |
| `qm-world` | the demo (`Qingming`), told to the engine by the world itself, and the jar's start (`QingmingMain`) |

The scroll is in the public domain and serves only as a reference: nothing of it is stored here.
