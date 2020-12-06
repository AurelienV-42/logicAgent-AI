package Agent;

import java.util.ArrayList;

public class Player {
    public enum Effector {Top, Bottom, Right, Left, ShootTop, ShootBottom, ShootRight, ShootLeft, Leave}

    public Player() {}

    private Effector choiceWithProba(ArrayList<Environment.Forest.State> states) {
        return Effector.Top;
    }

    public Effector play(ArrayList<Environment.Forest.State> states) {
        Effector returnValue;
        System.out.println("I will play for you... I receive this: " + states);
        returnValue = choiceWithProba(states);
        System.out.println("Hmm... I'm going to play that: " + returnValue);
        return returnValue;
    }

// • Chaque sortie trouvée : +10 * nombre de cases
// • Mort : -10 * nombre de cases
// • Mouvement : -1
// • Utilisation de roches : -10
}
