package javaPackage;
public class DungeonView {

    public static void main(String[] args) {
        Place place = new Place(20, 20);

        Place.Tile F = Place.Tile.FLOOR;
        Place.Tile W = Place.Tile.WALL;

        Place.Tile[][] dungeon = {
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,W,F,F,F,F,F,F,F,F,W,F,F,F,W},
            {W,F,Place.Tile.PLAYER,F,F,F,W,F,F,F,F,F,F,F,F,W,F,F,F,W},
            {W,F,F,F,F,F,W,F,F,F,F,F,F,F,F,W,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,W,W,W,W,F,W,W,W,W,W,W,F,W,W,W,W,W,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,W,W,W,W,F,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,W,F,F,F,F,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,W,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,W,F,F,F,F,W,W,W,W,W,F,F,F,W},
            {W,W,W,W,F,W,W,F,F,F,F,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W}
        };

        // Add treasure, enemy, and exit.
        dungeon[2][13] = Place.Tile.TREASURE;
        dungeon[8][10] = Place.Tile.ENEMY;
        dungeon[17][17] = Place.Tile.EXIT;

        place.setMap(dungeon);
        place.printMap();
    }
}
