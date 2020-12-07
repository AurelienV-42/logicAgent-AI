package Agent;

import Environment.Forest;
import utils.PairEffector;

import java.util.ArrayList;

public class Player {
    public enum Effector {Top, Bottom, Right, Left, Shoot, Leave}

    public Player() {
    }

    private PairEffector choiceWithProba(ArrayList<Environment.Forest.State> states) {
        if (states.contains(Forest.State.Portal))
            return new PairEffector(Effector.Leave);
        //TODO
        // • Chaque sortie trouvée : +10 * nombre de cases
        // • Mort : -10 * nombre de cases
        // • Mouvement : -1
        // • Utilisation de roches : -10
        return new PairEffector(Effector.Right);
    }

    public PairEffector play(ArrayList<Environment.Forest.State> states) {
        System.out.println("[Player] I receive: " + states);

        PairEffector returnValue;

        returnValue = choiceWithProba(states);

        if (returnValue.direction == null)
            System.out.println("[Player] I play: " + returnValue.effector);
        else
            System.out.println("[Player] I play: " + returnValue.effector + " " + returnValue.direction);
        return returnValue;
    }
}
