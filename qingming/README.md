# qingming — Along the River During the Qingming Festival, in 3D

A world after Zhang Zeduan's scroll 清明上河图: the reach of the Bian River (汴河) from the Rainbow Bridge (虹桥) to the
city gate. The scroll is not a map, so the plan is read from it and its sizes come from what is written and dug up. It
is built on the engine as released to it, as the bay is.

**So far: the terrain, the Rainbow Bridge, a street of Song buildings, and the city's wall and gate.** A level plain
1.1 km by 0.5 km, the river cut into it with its banks and water, its towpaths, the earth road from the country to the
bridge and the street from it to the gate and through it. Over the river, the Rainbow Bridge: its woven timber arch, its
deck to walk up and over, its railings and its four poles. Along the street west of it, fourteen shops, seven a side,
each a Song timber frame under a folded gable roof, its front open to walk in by. Between them and the bridge, the
two-storey wine house, its rooms reached by a stair, its festive gateway (彩楼欢门) before its door. A tea house at
each end of the bridge, two warehouses on the north bank, and seven stalls under awnings and umbrellas. A cruiser and a
boxer motorcycle are parked on the street by the start, to get about on (not of the scroll's time). Where the street
ends, the city gate (城门): a brick-faced base through the wall, a passage through it framed in timber, its two leaves
standing open, a tower of five bays on it; and the wall (城墙) either side of it, a bare rampart of rammed earth 9 m
high, to the reach's edges. The passage is walked and ridden through; the wall is in the way. Inside the wall, south
of the gate, a brick ramp (慢道) 48 m long climbs to the gate's top; the wall's top is stepped onto from it on the way
up. Boats and people are to come.

## Build and launch

Java 25 and Maven. From the repository's root:

```
mvn install
```

or the world alone, once the engine is installed: `mvn -f qingming/pom.xml install`. Then:

```
java -jar qingming/qm-world/target/qingming.jar
```

The avatar starts on the street north of the bridge. Press J to put on the jet-pack and F to fly. The gate is 540 m
west of the bridge; `-Davatar.at=-485,30,0,270` starts before it, facing it. Unattended runs take the game's `-Davatar.script` and `-Davatar.snapshot`, as the bay's do.

## Where the sizes come from

| What | Size here | From |
|---|---|---|
| The bridge's span | 22 m between the river's banks | about 20 to 22 m by the bridge's historians (唐寰澄 and others), read off the scroll |
| The bridge's width | 8 m, the road's width | the same reckonings |
| Its arch | 21 groups of squared logs 40 cm thick, 45 cm apart: 11 of four members, 10 of three, tied by 5 logs across; its crown some 5 m over the water | 茅以升's reading of the scroll, via the timber-arch literature; 唐寰澄 |
| Its railings and poles | 23 posts a side, 2 taller ones at each end; 4 poles with a cross-bar, a disc and a crane | read off the scroll; their heights estimates |
| A shop's frame | columns no taller than a bay is wide; its ridge a quarter of its span over its eaves, its slope folded a tenth of that rise | the Yingzao Fashi (营造法式, 1103): 柱高不越间之广, 举折; its sizes estimates |
| The river's water | 1.86 m deep | the 6 chi the Song history (宋史) has it held at for loaded boats |
| Its towpaths | 4.65 m wide | the dike tops the Song history keeps along it within the capital |
| The wall | half as thick again at its foot as it is high, as thick at its top as it is high: 13.5 m and 9 m | the Yingzao Fashi's rule for a city's wall (筑城之制); its height an estimate; no battlements, as the scroll draws none |
| The gate's passage | 5.6 m wide and 19.3 m through; posts along both sides (排叉柱), struts slanting in to a beam, its head a trapezoid, not an arch | as reported of the Song gate dug up at Kaifeng (景龙门); the framing as the scroll draws it and the Yingzao Fashi describes; its height an estimate |
| The gate's way up | a ramp inside the wall, a foot up for every five along: 48 m for the base's 9.6 m | the Yingzao Fashi's rule for a city gate's ramp (城门慢道), its five taken along the ground; which side of the gate, and its width, estimates |
| The gate's tower | five bays, a folded gable roof | the frame the shops share; the scroll's has a hipped roof on bracket sets, not built yet |
| The gate | an old-city gate, the bridge a short way outside it | the Palace Museum's reading (东角子门, the bridge 上土桥); the distance shortened as the scroll shortens it |
| The land | level | the plain round Kaifeng |

## Layout

| Module | What it holds |
|---|---|
| `qm-land` | the land (`Reach`), the river (`Rivers`), the roads (`Streets`) |
| `qm-structures` | what stands in it, declared on a map (`Fixtures`): each thing a place and a specification |
| `qm-world` | the demo (`Qingming`), told to the engine by the world itself, and the jar's start (`QingmingMain`) |

The scroll is in the public domain and serves only as a reference: nothing of it is stored here.

## How things are built

What stands in the reach is not built here. The map (`Fixtures`) says whose the world is and when (the Chinese of the
Northern Song, about 1120), binds an implementation once, and then puts each thing by a place and a typed
specification:

```java
reach.with(new SongWovenArch()).at("the Rainbow Bridge", 0, 0, 0, new WovenArch(2_000, 800, 360, ...));
var shops = reach.with(new SongShopHouse());
shops.at("shop north 1", x, z, 180, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE));
```

The implementations are the Song's own, in `building/building-chinese`: each a function of its specification, its type
saying the thing, the civilization and the era. What each builds is said in the taxonomy's terms and checked: a shop has
a way in, a crossing has two ends.
