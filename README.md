# The Hollow — dungeon crawler

A desktop dungeon crawler written in Java/Swing. Descend through **10 newly
generated floors**, collect upgrades, fight pursuing enemies, and defeat the
Warden to escape. Game remains the application's entry point. No extra GUI or
asset libraries are needed.

## Run in Eclipse

1. Refresh the project with **F5**.
2. Open `src/javaPackage/Game.java`.
3. Right-click → **Run As → Java Application**.
4. Play in the game window. Commands are no longer typed into the Console.

Java 11 is recommended because it also supports the course's `student.jar` tests.
The application itself uses Java 8-compatible language features and Swing.

From a terminal in the project folder:

```sh
sh scripts/run.sh
```

For the optional text interface:

```sh
sh scripts/run.sh --console
```

## Controls

- **WASD / arrow keys:** move one tile. Walking into an enemy attacks it.
- **Space:** attack in the last direction you faced, without moving.
- **E / Enter:** descend while standing on the golden stairs.
- **P / Escape:** pause or resume.
- **H:** open or close the controls guide.
- **R / New Expedition button:** start a fresh run with a new seed.

Enemies respond after a successful move or attack. Walls and attacks without a
target do not consume a turn. Nearby enemies pursue through corridors; a monster
you attack retaliates once, while other nearby monsters also take their turns.
The game pauses while its guide is open.

Red flasks restore vitality and increase maximum health. Golden blades permanently
increase attack. Both apply automatically on pickup and enter the satchel once.
Descending restores up to 18 missing health. Difficulty and enemy counts increase
with depth. Floor 10 contains the Warden; its exit remains locked until the Warden
is defeated. Death and victory show a result screen; press R to begin again.

The map remembers explored territory. Enemies are drawn only near the player.
The sidebar shows health, attack, collected items, defeated enemies, and recent
events. The seed at the bottom identifies the run.

## Generation and class design

`Game.newRun()` chooses a fresh seed and creates floor 1. Later floors are generated
when entered and appended to the chain of places. `Game.newRun(long seed)` allows a
reproducible campaign for tests or debugging. `Dungeon.generate(layer, seed)` uses a
different random stream for each depth, carves rooms, connects them with corridors,
and chooses the farthest reachable tile for the exit. Items and enemies occupy
distinct reachable tiles, away from the spawn and exit. The border stays solid.
`Dungeon.createDungeons()` can also construct a fresh complete ten-floor array.

- **Game:** launch, input validation, turn handling, combat, progression, inventory,
  run statistics, and a bounded message history. Its existing constructor still
  accepts custom floors for focused tests. `start()` is the optional console loop;
  `startInteractive()` starts the desktop lifecycle without blocking on input.
- **Dungeon:** generation, terrain, occupants, movement, pathfinding, and map data.
- **GameWindow:** Swing presentation and keyboard bindings. It calls Game methods;
  gameplay rules remain in Game/Dungeon. There is no DungeonView class.
- **Player / Enemy / Item / Place:** game entities and terrain operations. Item
  continues to extend Enemy so Dungeon can store both in the same occupant chain.

There is no Camp or CommandHandler class. Game owns inventory and command
normalization. Reflect these agreed changes in the submitted specification/diagram.

## Shared LinkedChain storage

One generic LinkedChain implementation is reused for three element types:

- `LinkedChain<Place>` stores visited floors in Game.
- `LinkedChain<Enemy>` stores enemies and items in Dungeon.
- `LinkedChain<Item>` stores the collected inventory in Game.

Game and Dungeon only call public chain methods; they never access nodes or links.
`getInventory()` returns an ArrayList snapshot for callers. `getEnemies()` returns
a chain snapshot protecting membership. Entity objects themselves remain mutable
so combat and enemy movement can update them.

## Course-standard tests

Every test class imports `student.TestCase` and extends `TestCase`. The original
Eclipse build path references `/CS2-Support/student.jar`.

```sh
sh scripts/test.sh
```

The script locates `~/eclipse-workspace/CS2-Support/student.jar` and prefers the
installed Java 11 on macOS. The course library includes its JUnit runner. To supply
paths on another machine:

```sh
JAVA_HOME=/path/to/jdk-11 \
STUDENT_JAR=/path/to/CS2-Support/student.jar sh scripts/test.sh
```

In Eclipse, select the test classes → **Run As → JUnit Test**, choosing **Java 11**
for that launch. The installed course library cannot start its tests under Java 26
because it uses SecurityManager. The command-line test suite runs with a headless
Swing renderer, so it does not open a game window.

Tests cover:

- Normal cases and invalid arguments, with boundary/default states for getters
  that have no input arguments.
- Empty chains/inventory, missing removals, invalid indices, nulls, and snapshots.
- Exact bad-command responses and continued prompting in the optional console.
- Movement, blocked tiles, collection, bonuses, combat, death, and victory.
- **300 generated maps** across 30 seeds and all ten depths: connected terrain,
  reachable occupants, unique occupancy, safe spawn, solid border, and one exit.
- Reproducible seeds, different floors/runs, invalid depths, and a complete campaign
  with floors generated on entry. Route tests use a strong player to isolate
  connectivity/progression from difficulty balancing.
- Enemy pursuit, retaliation, item blocking, dead enemies, and the Warden exit gate.
- Actual Swing key actions for arrows/WASD, combat, descending, pause/help, restart,
  and offscreen painting at normal/minimum size and in result states.

## Grading evidence and actual Big-O

The original rubric is still supported: a working application with item upgrades
as a stretch goal; three types through the same LinkedChain; collection use only
through its public API; runnable student.TestCase tests; and validation that allows
bad input to survive. GUI controls additionally validate blocked moves, absent
attack targets, premature descent, and paused/dead states in Game and GameWindow.

Let A be floor tile count, E occupant count, I inventory size, F visited floor
count, C command length, and L item-name length.

- **LinkedChain:** front add/remove, append, clear, size, and empty checks are O(1).
  Indexed access/insertion/removal, matching removal, contains/count, and array
  conversion are O(n). String conversion also includes element string lengths.
- **Dungeon:** terrain and exit checks, position/layer getters are O(1).
  Occupant lookup, move validation, snapshots, and item removal are O(E).
  Map rendering is O(A + E). Generation is O(A) for the bounded room-attempt limit
  used here. Enemy turns are O(A + E²): one breadth-first distance map, then
  movement collision searches through the occupant chain.
- **Game:** a GUI turn includes enemy updates, so its worst-case cost is
  O(A + E² + I + L) with pickup. The Swing repaint is separate, O(A + E + I) plus
  displayed text. Direct attack is O(E). Inventory operations and snapshots are
  O(I), except count is O(1). Cached current-floor, player-position, stat, seed,
  turn-count, and outcome getters are O(1). Message snapshots are bounded at 80
  entries. Entering a generated floor costs O(F + A + E); the indexed floor lookup
  is O(F), unlike the original O(1) estimate. Direct movement's console rendering
  adds O(A + E). Parsing is O(C), plus the selected command's cost. Console start
  depends on the number of commands entered.
- **Entities/Place:** stat, coordinate, attack, healing, life-state, and individual
  tile operations are O(1). Place construction/map replacement/printing are O(A).
  Item construction/description are O(L); bonus application/getters are O(1).

The team's explanation should include these actual costs. In particular, a singly
linked chain does not give constant-time indexed floor changes, and enemy-aware
movement is not O(1). Game's cached current-floor lookup does match O(1).
