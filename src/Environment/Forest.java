package Environment;

import Agent.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Forest {
    public enum State {Character, Smell, Wind, Light, Monster, Portal, Rift} // Tirer une roche tue un monstre dans la case directement devant vous

    Scanner input;
    private ArrayList<ArrayList<ArrayList<State>>> tab;

    public static void main(String[] args) {
        Forest forest = new Forest();
        forest.start();
    }

    private void fillAroundWith(State state, int x, int y) {
        if (x - 1 > -1 && !tab.get(x - 1).get(y).contains(state))
            tab.get(x - 1).get(y).add(state);
        if (y - 1 > -1 && !tab.get(x).get(y - 1).contains(state))
            tab.get(x).get(y - 1).add(state);
        if (x + 1 < tab.size() && !tab.get(x + 1).get(y).contains(state))
            tab.get(x + 1).get(y).add(state);
        if (y + 1 < tab.size() && !tab.get(x).get(y + 1).contains(state))
            tab.get(x).get(y + 1).add(state);
    }

    Forest() {
        input = new Scanner(System.in);  // Create a Scanner object

        System.out.println("Adding Monster, Rift and a beautiful portal in the forest...\n");
        boolean portal = false;

        while (!portal) {
            tab = new ArrayList<>();
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
                tab.add(row);
            }
        }
        for (int x = 0; x < tab.size(); x++) {
            for (int y = 0; y < tab.get(x).size(); y++) {
                if (tab.get(x).get(y).contains(State.Rift))
                    fillAroundWith(State.Wind, x, y);
                if (tab.get(x).get(y).contains(State.Monster))
                    fillAroundWith(State.Smell, x, y);
                if (tab.get(x).get(y).contains(State.Portal))
                    fillAroundWith(State.Light, x, y);

            }
        }
        displayTab();
    }

    private void start() {
        boolean lose = false;
        Player player = new Player();

        while (!lose) {
            System.out.print("Press enter to continue");
            input.nextLine();
            player.play(getStatesWherePlayerIs());
            System.out.println();
            displayTab();
        }
    }

    private ArrayList<State> getStatesWherePlayerIs() {
        for (ArrayList<ArrayList<State>> arrayLists : tab) {
            for (ArrayList<State> arrayList : arrayLists) {
                if (arrayList.contains(State.Character))
                    return arrayList;
            }
        }
        return null;
    }

    private void displayTab() {
        for (ArrayList<ArrayList<State>> arrayLists : tab) {
            for (ArrayList<State> arrayList : arrayLists) {
                System.out.print(arrayList);
            }
            System.out.println();
        }
        System.out.println();
    }
}