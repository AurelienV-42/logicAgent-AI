package Agent;

import Environment.Forest;
import utils.PairEffector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player {

    public enum Effector {Top, Bottom, Right, Left, Shoot, Leave, Death}

    private final Map<Effector, Integer> agentMeasurement;
    private final Map<Forest.State, Effector> eventMeasurement;
    private final Map<String, Effector> directions;
    final private Map<Forest.State, Forest.State> basicKnowledge;
    private final Map<String, Forest.State> volatileKnowledge;
    private final ArrayList<Forest.State> events;
    private final ArrayList<Forest.State> effects;
    private int numberOfStates = 0;
    private PairEffector choiceDir;
    private Forest.State choiceStateAssumption;
    private int performanceScore = 0;
    private int iteration = 0;
    private int totalScore = 0;
    private int averagePerf = 0;
    private final boolean hasMoved = true;
    private ArrayList<Effector> shot;

    private final int ScoreDePerformance = 0;
    // KnowledgeVariable

    public Player() {
        this.agentMeasurement = new HashMap<>();
        this.eventMeasurement = new HashMap<>();
        this.directions = new HashMap<>();
        this.basicKnowledge = new HashMap<>();
        this.volatileKnowledge = new HashMap<>();
        this.events = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.shot = new ArrayList<>();

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

    private void measurePerf(PairEffector choice) {

        for (Effector measure : this.agentMeasurement.keySet()) {
            if (choice.effector == measure) {
                this.performanceScore += this.agentMeasurement.get(measure);
                this.totalScore += this.agentMeasurement.get(measure);
                this.iteration++;
                this.averagePerf = this.totalScore / this.iteration;
                if (choice.effector == Effector.Leave) {
                    System.out.println("Score de performance pour cet environnement : " + this.performanceScore);
                    System.out.println("Score moyen de performance : " + this.averagePerf);
                    this.performanceScore = 0;
                } else if (choice.effector == Effector.Death) {
                    System.out.println("Score de performance pour cet environnement : " + this.performanceScore);
                    System.out.println("Score moyen de performance : " + this.averagePerf);
                    System.out.println("Score total de performance pour cet environnement : " + this.totalScore);
                    System.out.println("Nombre d'itérations : " + this.iteration);
                }
            }
        }
    }

    private void buildKnowledge(ArrayList<Environment.Forest.State> states) {
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

    private void updateKnowledge() {
        for (String dir : this.directions.keySet()) {
            if (this.choiceDir != null) {
                if (this.choiceDir.effector == this.directions.get(dir)) {
                    System.out.println("Direction choice : " + dir + " Assumption : " + this.directions.get(dir));
                    this.volatileKnowledge.put(dir, Forest.State.Clear);
                }
            }
        }
    } // TODO Maybe on n'en a pas besoin

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
    } // Permet de savoir s'il y a un event sur la case.

    private PairEffector checkEffects(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {

        for (Effector dir : wherePlayerCanGo) {
            System.out.println("Dir : " + dir);
        }
        int rand = (int)(Math.random() * ((wherePlayerCanGo.size() - 2) + 1)) + 2;
        PairEffector choice = null;

        if (this.effects.size() == 0) {
            choice = new PairEffector(wherePlayerCanGo.get(rand));
            this.shot.clear();
        } else {
            for (Forest.State effect : this.effects) {
                if (effect == Forest.State.Smell) {
                    for (Effector dir : wherePlayerCanGo) {
                        if (!this.shot.contains(dir)) {
                            choice = new PairEffector(Effector.Shoot, dir);
                            this.shot.add(dir);
                            return choice;
                        }
                    }
                }
            }
            choice = new PairEffector(wherePlayerCanGo.get(rand));
            this.shot.clear();
        }
        return choice;
    }

    private PairEffector filterRules(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {
        PairEffector event = checkEvents();

        if (event != null) {
            return event;
        } else {
            return checkEffects(states, wherePlayerCanGo);
        }
    }

    private PairEffector inferenceEngine(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {
        buildKnowledge(states);
        updateKnowledge();
        return filterRules(states, wherePlayerCanGo);
    }

    private PairEffector choiceWithProba(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {
        PairEffector choice = inferenceEngine(states, wherePlayerCanGo);
        measurePerf(choice);
        return choice;
    }

    public PairEffector play(ArrayList<Forest.State> states, ArrayList<Effector> wherePlayerCanGo, int mapSize) {
        System.out.println("[Player] I receive: " + states);

        PairEffector returnValue;

        returnValue = choiceWithProba(states, wherePlayerCanGo);

        if (returnValue.direction == null)
            System.out.println("[Player] I play: " + returnValue.effector);
        else
            System.out.println("[Player] I play: " + returnValue.effector + " " + returnValue.direction);
        return returnValue;
    }
}
