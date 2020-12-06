package Environment;

import Agent.Player;
import utils.Pair;

import java.util.ArrayList;
import java.util.Scanner;

public class Forest {
    public enum State {Character, Smell, Wind, Light, Monster, Portal, Rift}

    Scanner input;
    private ArrayList<ArrayList<ArrayList<State>>> map;

    public static void main(String[] args) {
        Forest forest = new Forest();
        forest.start();
    }

    Forest() {
        input = new Scanner(System.in);  // Create a Scanner object

        System.out.println("Adding Monster, Rift and a beautiful portal in the forest...\n");
        boolean portal = false;

        while (!portal) {
            map = new ArrayList<>();
            for (int x = 0; x < 3; x++) {
                ArrayList<ArrayList<State>> row = new ArrayList<>();
                for (int y = 0; y < 3; y++) {
                    ArrayList<State> states = new ArrayList<>();
                    if (x == 0 && y == 0)
                        states.add(State.Character);
                    else {
                        double rand = Math.random();

                        if (rand <= 0.25) {
                            states.add(State.Rift);
                        } else if (rand > 0.25 && rand <= 0.5) {
                            states.add(State.Monster);
                        } else if (rand > 0.5 && rand <= 0.75 && !portal) {
                            states.add(State.Portal);
                            portal = true;
                        }
                    }
                    row.add(states);
                }
                map.add(row);
            }
        }
        for (int x = 0; x < map.size(); x++) {
            for (int y = 0; y < map.get(x).size(); y++) {
                if (map.get(x).get(y).contains(State.Rift))
                    fillAroundWith(State.Wind, x, y);
                if (map.get(x).get(y).contains(State.Monster))
                    fillAroundWith(State.Smell, x, y);
                if (map.get(x).get(y).contains(State.Portal))
                    fillAroundWith(State.Light, x, y);

            }
        }
        displayMap();
    }

    private void start() {
        boolean lose = false;
        Player player = new Player();

        while (!lose) {
            Pair coords = getWherePlayerIs();

            System.out.print("Press enter to continue");
            input.nextLine();
            Player.Effector thePlay = player.play(map.get(coords.x).get(coords.y));
            lose = updateTheMap(thePlay, coords);
            System.out.println();
            displayMap();
        }
    }

    private Pair getWherePlayerIs() {
        for (int x = 0; x < map.size(); x++) {
            for (int y = 0; y < map.get(x).size(); y++) {
                if (map.get(x).get(y).contains(State.Character))
                    return new Pair(x, y);
            }
        }
        return null;
    }

    private boolean ShootTo(Player.Effector direction, Pair coords) {
        int x = coords.x;
        int y = coords.y;

        switch (direction) {
            case ShootTop:
                x--;
                if (x < 0 || !getStates(x, y).contains(State.Monster))
                    return true;
            case ShootBottom:
                x++;
                if (x > map.size() || !getStates(x, y).contains(State.Monster))
                    return true;
            case ShootRight:
                y++;
                if (y > map.size() || !getStates(x, y).contains(State.Monster))
                    return true;
            case ShootLeft:
                y = y - 1;
                if (y < 0 || !getStates(x, y).contains(State.Monster))
                    return true;
        }
        addOrDeleteElement(State.Monster, x, y, false);
        removeAroundWith(State.Smell, x, y);
        return false;
    }

    private ArrayList<State> getStates(int x, int y) {
        return map.get(x).get(y);
    }

    private void fillAroundWith(State state, int x, int y) {
        if (x - 1 > -1 && !map.get(x - 1).get(y).contains(state))
            map.get(x - 1).get(y).add(state);
        if (y - 1 > -1 && !map.get(x).get(y - 1).contains(state))
            map.get(x).get(y - 1).add(state);
        if (x + 1 < map.size() && !map.get(x + 1).get(y).contains(state))
            map.get(x + 1).get(y).add(state);
        if (y + 1 < map.size() && !map.get(x).get(y + 1).contains(state))
            map.get(x).get(y + 1).add(state);
    }

    private void removeAroundWith(State state, int x, int y) {
        if (x - 1 > -1 && map.get(x - 1).get(y).contains(state))
            map.get(x - 1).get(y).remove(state);
        if (y - 1 > -1 && map.get(x).get(y - 1).contains(state))
            map.get(x).get(y - 1).remove(state);
        if (x + 1 < map.size() && map.get(x + 1).get(y).contains(state))
            map.get(x + 1).get(y).remove(state);
        if (y + 1 < map.size() && map.get(x).get(y + 1).contains(state))
            map.get(x).get(y + 1).remove(state);
    }

    private void addOrDeleteElement(State state, int x, int y, boolean remove) {
        if (remove)
            map.get(x).get(y).remove(state);
        else
            map.get(x).get(y).add(state);
    }

    private boolean travel(Player.Effector direction, Pair coords) {
        int x = coords.x;
        int y = coords.y;

        switch (direction) {
            case Top:
                if (x - 1 < 0)
                    return true;
                addOrDeleteElement(State.Character, x - 1, y, false);
            case Bottom:
                if (x + 1 > map.size())
                    return true;
                addOrDeleteElement(State.Character, x - 1, y, false);
            case Right:
                if (y + 1 > map.size())
                    return true;
                addOrDeleteElement(State.Character, x - 1, y, false);
            case Left:
                if (y - 1 < 0)
                    return true;
                addOrDeleteElement(State.Character, x - 1, y, false);
        }
        addOrDeleteElement(State.Character, x, y, true);
        return false;
    }

    private void upgradeMap() {
        // TODO
    }

    private boolean updateTheMap(Player.Effector thePlay, Pair coords) {
        switch (thePlay) {
            case ShootTop, ShootBottom, ShootLeft, ShootRight:
                return ShootTo(thePlay, coords);
            case Leave:
                if (map.get(coords.x).get(coords.y).contains(State.Portal)) {
                    upgradeMap();
                    return false;
                }
            default:
                return travel(thePlay, coords);
        }
    }

    private void displayMap() {
        for (ArrayList<ArrayList<State>> arrayLists : map) {
            for (ArrayList<State> arrayList : arrayLists) {
                System.out.print(arrayList);
            }
            System.out.println();
        }
        System.out.println();
    }
}