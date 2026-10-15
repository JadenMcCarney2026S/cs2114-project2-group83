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
        
        Place place2 = new Place(40, 40);
        Place.Tile[][] dungeon2 = {
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,F,F,F,F,F,F,F,F,F,F,F,F,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
            {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W}
        };

        dungeon2[2][2] = Place.Tile.PLAYER;
        dungeon2[4][24] = Place.Tile.TREASURE;
        dungeon2[20][23] = Place.Tile.ENEMY;
        dungeon2[30][8] = Place.Tile.ENEMY;
        dungeon2[35][35] = Place.Tile.EXIT;

        place2.setMap(dungeon2);
        place2.printMap();
    }
}
