package Agent;

import Environment.Forest;
import utils.Pair;
import utils.PairEffector;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player {

    public enum Effector {Top, Bottom, Right, Left, Shoot, Leave, Death}
    private Map<Effector, Integer> agentMeasurement;
    private Map<Forest.State, Effector> eventMeasurement;
    private Map<String, Effector> directions;
    final private Map<Forest.State, Forest.State> basicKnowledge;
    private Map<String, Forest.State> volatileKnowledge;
    private ArrayList<Forest.State> events;
    private ArrayList<Forest.State> effects;
    private int numberOfStates = 0;
    private PairEffector choiceDir;
    private Forest.State choiceStateAssumption;
    private int performanceScore = 0;
    private boolean hasMoved = true;

    private int ScoreDePerformance = 0;
    // KnowledgeVariable

    public Player() {
        this.agentMeasurement = new HashMap<>();
        this.eventMeasurement = new HashMap<>();
        this.directions = new HashMap<>();
        this.basicKnowledge = new HashMap<>();
        this.volatileKnowledge = new HashMap<>();
        this.events = new ArrayList<>();
        this.effects = new ArrayList<>();

        this.agentMeasurement.put(Effector.Top, -1);
        this.agentMeasurement.put(Effector.Right, -1);
        this.agentMeasurement.put(Effector.Bottom, -1);
        this.agentMeasurement.put(Effector.Left, -1);
        this.agentMeasurement.put(Effector.Shoot, -10);
        this.agentMeasurement.put(Effector.Leave, 10 * 3);
        this.agentMeasurement.put(Effector.Death, -10 * 3);
        this.eventMeasurement.put(Forest.State.Monster, Effector.Death);
        this.eventMeasurement.put(Forest.State.Rift, Effector.Death);
        this.eventMeasurement.put(Forest.State.Portal, Effector.Leave);
        this.directions.put("A", Effector.Top);
        this.directions.put("B", Effector.Right);
        this.directions.put("C", Effector.Bottom);
        this.directions.put("D", Effector.Left);
        this.basicKnowledge.put(Forest.State.Smell, Forest.State.Monster);
        this.basicKnowledge.put(Forest.State.Light, Forest.State.Portal);
        this.basicKnowledge.put(Forest.State.Wind, Forest.State.Rift);
        this.volatileKnowledge.put("A", null);
        this.volatileKnowledge.put("B", null);
        this.volatileKnowledge.put("C", null);
        this.volatileKnowledge.put("D", null);
    }

    private void performanceMeasurement(PairEffector choice) {

        for (Effector measure : this.agentMeasurement.keySet()) {
            if (choice.effector == measure) {
                this.performanceScore += this.agentMeasurement.get(measure);
            }
        }
    }

    private void buildKnowledge(ArrayList<Environment.Forest.State> states) {

        if (this.hasMoved == true) {
            this.events.clear();
            this.effects.clear();
            for (Forest.State state : states) {
                for (Forest.State fact : this.basicKnowledge.keySet()) {
                    if (state == fact) {
                        effects.add(state);
                    } else if (state == this.basicKnowledge.get(fact)) {
                        events.add(state);
                    }
                }
            }
            this.numberOfStates = events.size() + effects.size();
        }
    }

    private void updateKnowledge() {
        for (String dir : this.directions.keySet()) {
            if (this.choiceDir != null) {
                if (this.choiceDir.effector == this.directions.get(dir)) {
                    System.out.println("Direction choice : " + dir + " Assumption : " + this.directions.get(dir));
                    this.volatileKnowledge.put(dir, Forest.State.Clear);
                }
            }
        }
    }

    private PairEffector checkEvents() {

        if (this.events.size() > 0) {
            for (Forest.State event : this.events) {
                for (Forest.State ref : this.eventMeasurement.keySet()) {
                    if (event == ref) {
                        return new PairEffector(this.eventMeasurement.get(ref));
                    }
                }
            }
        }
        return null;
    }

    private PairEffector checkEffects(ArrayList<Environment.Forest.State> states) {

        int effectIdx = 0;
        int paths = 2; // Number of possible paths min 2 max 4
        PairEffector choice = null;

        if (this.effects.size() == 0) {
            // Random
        } else if (this.effects.size() == 1) {
            // Check for Smell effect
            // Check for Light effect
            // Check for Wind effect
        } else if (this.effects.size() == 2) {
            // Check for Smell, Light effects
            // Check for Smell, Wind effects
            // Check for Light, Wind effects
        } else if (this.effects.size() == 3) {
            // Check for Smell, Light, Wind effects
        }

        displayProbas();
        return choice;
    }

    private PairEffector filterRules(ArrayList<Environment.Forest.State> states) {

        PairEffector event = checkEvents();

        if (event != null) {
            return event;
        } else {
            return checkEffects(states);
        }
    }

    private void displayProbas() {
        for (String key : this.volatileKnowledge.keySet()) {
            System.out.println("Direction : " + key + " Assumption : " + this.volatileKnowledge.get(key));
        }
        System.out.println("\n\n");
    }

    private PairEffector inferenceEngine(ArrayList<Environment.Forest.State> states) {

        buildKnowledge(states);
        updateKnowledge();
        return filterRules(states);
    }

    private PairEffector choiceWithProba(ArrayList<Environment.Forest.State> states) {

        PairEffector choice = inferenceEngine(states);
        this.choiceDir = choice;
        if (choice.direction == null) {
            this.hasMoved = true;
            System.out.println("Moved");
        } else {
            System.out.println("Not moved");
            this.hasMoved = false;
        }
        System.out.println("Effector : " + choice.effector);

        performanceMeasurement(this.choiceDir);
        return choice;
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
