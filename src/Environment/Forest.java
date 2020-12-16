package Environment;

import Agent.Player;
import utils.Pair;
import utils.PairEffector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Forest {
    public enum State {Character, Smell, Wind, Light, Monster, Portal, Rift, Clear} // TODO Supprimer Clear

    Scanner input;
    private ArrayList<ArrayList<ArrayList<State>>> map;

    public static void main(String[] args) {
        Forest forest = new Forest();
        forest.start();
    }

    Forest() {
        input = new Scanner(System.in);  // Create a Scanner object

        System.out.println("Adding Monster, Rift and a beautiful portal in the forest...\n");
        generateMap(3);
        displayMap();
    }

    private void start() {
        boolean lose = false;
        Player player = new Player();

        while (!lose) {
            Pair coords = getWherePlayerIs();
            ArrayList<Player.Effector> wherePlayerCanGo = getWherePlayerCanGo(coords);

            System.out.print("Press enter to continue");
            input.nextLine();

            PairEffector thePlay = player.play(map.get(Objects.requireNonNull(coords).x).get(coords.y), wherePlayerCanGo, map.size());
            lose = updateTheMap(thePlay, coords);
            System.out.println();
            displayMap();
        }
        System.out.println("You did a mistake... :(");
    }

    private ArrayList<Player.Effector> getWherePlayerCanGo(Pair coords) {
        ArrayList<Player.Effector> result = new ArrayList<>();

        if (coords.x > 0)
            result.add(Player.Effector.Top);
        if (coords.x < map.size() - 1)
            result.add(Player.Effector.Bottom);
        if (coords.y < map.size() - 1)
            result.add(Player.Effector.Right);
        if (coords.y > 0)
            result.add(Player.Effector.Left);
        return result;
    }

    private void generateMap(int size) {
        boolean portal = false;

        while (!portal) {
            map = new ArrayList<>();
            for (int x = 0; x < size; x++) {
                ArrayList<ArrayList<State>> row = new ArrayList<>();
                for (int y = 0; y < size; y++) {
                    ArrayList<State> states = new ArrayList<>();
                    if (x == 0 && y == 0)
                        states.add(State.Character);
                    else {
                        State state = chooseAState(portal);
                        if (state == State.Portal)
                            portal = true;
                        if (state != null)
                            states.add(state);
                    }
                    row.add(states);
                }
                map.add(row);
            }
        }
        addSideEffects();
    }

    private State chooseAState(boolean portal) {
        double rand = Math.random();

        if (rand <= 0.25) {
            return State.Rift;
        } else if (rand > 0.25 && rand <= 0.5) {
            return State.Monster;
        } else if (rand > 0.5 && rand <= 0.75 && !portal) {
            return State.Portal;
        }
        return null;
    }

    private void addSideEffects() {
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

    private ArrayList<State> getStates(int x, int y) {
        return map.get(x).get(y);
    }

    private boolean shootTo(PairEffector effectors, Pair coords) {
        int x = coords.x;
        int y = coords.y;
        boolean monster = true;

        if (effectors.direction == null) {
            System.err.println("You have to give a direction to shoot");
            return true;
        }
        switch (effectors.direction) {
            case Top -> {
                x--;
                if (x < 0 || !getStates(x, y).contains(State.Monster))
                    monster = false;
            }
            case Bottom -> {
                x++;
                if (x > map.size() || !getStates(x, y).contains(State.Monster))
                    monster = false;
            }
            case Right -> {
                y++;
                if (y > map.size() || !getStates(x, y).contains(State.Monster))
                    monster = false;
            }
            case Left -> {
                y = y - 1;
                if (y < 0 || !getStates(x, y).contains(State.Monster))
                    monster = false;
            }
        }
        if (monster) {
            addOrDeleteElement(State.Monster, x, y, true);
            removeAroundWith(State.Smell, x, y);
        } else {
            System.err.println("There isn't any monster in x: " + x + " y: " + y);
        }
        return false;
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
        if (x - 1 > -1)
            map.get(x - 1).get(y).remove(state);
        if (y - 1 > -1)
            map.get(x).get(y - 1).remove(state);
        if (x + 1 < map.size())
            map.get(x + 1).get(y).remove(state);
        if (y + 1 < map.size())
            map.get(x).get(y + 1).remove(state);
    }

    private void addOrDeleteElement(State state, int x, int y) {
        addOrDeleteElement(state, x, y, false);
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
            case Top -> {
                if (x - 1 < 0)
                    return true;
                addOrDeleteElement(State.Character, x - 1, y);
            }
            case Bottom -> {
                if (x + 1 >= map.size())
                    return true;
                addOrDeleteElement(State.Character, x + 1, y);
            }
            case Right -> {
                if (y + 1 >= map.size())
                    return true;
                addOrDeleteElement(State.Character, x, y + 1);
            }
            case Left -> {
                if (y - 1 < 0)
                    return true;
                addOrDeleteElement(State.Character, x, y - 1);
            }
        }
        addOrDeleteElement(State.Character, x, y, true);
        return false;
    }

    private void upgradeMap() {
        System.out.println("[Forest] Portal Reached!");
        generateMap(map.size() + 1);
    }

    private boolean updateTheMap(PairEffector thePlay, Pair coords) {
        switch (thePlay.effector) {
            case Shoot:
                return shootTo(thePlay, coords);
            case Leave:
                if (map.get(coords.x).get(coords.y).contains(State.Portal)) {
                    upgradeMap();
                    return false;
                }
            case Death:
                return true;
            default:
                return travel(thePlay.effector, coords);
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