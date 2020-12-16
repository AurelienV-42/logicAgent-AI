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
    final private Map<Forest.State, Forest.State> basicKnowledge;
    private final ArrayList<Forest.State> events;
    private final ArrayList<Forest.State> effects;
    private int performanceScore = 0;
    private int iteration = 0;
    private int totalScore = 0;
    private final ArrayList<Effector> shot;

    // KnowledgeVariable

    public Player() {
        this.agentMeasurement = new HashMap<>();
        this.eventMeasurement = new HashMap<>();
        Map<String, Effector> directions = new HashMap<>();
        this.basicKnowledge = new HashMap<>();
        this.events = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.shot = new ArrayList<>();

        this.agentMeasurement.put(Effector.Top, -1);
        this.agentMeasurement.put(Effector.Right, -1);
        this.agentMeasurement.put(Effector.Bottom, -1);
        this.agentMeasurement.put(Effector.Left, -1);
        this.agentMeasurement.put(Effector.Shoot, -10);

        this.eventMeasurement.put(Forest.State.Monster, Effector.Death);
        this.eventMeasurement.put(Forest.State.Rift, Effector.Death);
        this.eventMeasurement.put(Forest.State.Portal, Effector.Leave);

        directions.put("A", Effector.Top);
        directions.put("B", Effector.Right);
        directions.put("C", Effector.Bottom);
        directions.put("D", Effector.Left);

        this.basicKnowledge.put(Forest.State.Smell, Forest.State.Monster);
        this.basicKnowledge.put(Forest.State.Light, Forest.State.Portal);
        this.basicKnowledge.put(Forest.State.Wind, Forest.State.Rift);
    }

    private void measurePerf(PairEffector choice) {

        for (Effector measure : this.agentMeasurement.keySet()) {
            if (choice.effector == measure) {
                this.performanceScore += this.agentMeasurement.get(measure);
                this.totalScore += this.agentMeasurement.get(measure);
                this.iteration++;
                int averagePerf = this.totalScore / this.iteration;
                if (choice.effector == Effector.Leave) {
                    System.out.println("Score de performance pour cet environnement : " + this.performanceScore);
                    System.out.println("Score moyen de performance : " + averagePerf);
                    this.performanceScore = 0;
                } else if (choice.effector == Effector.Death) {
                    System.out.println("Score de performance pour cet environnement : " + this.performanceScore);
                    System.out.println("Score moyen de performance : " + averagePerf);
                    System.out.println("Score total de performance : " + this.totalScore);
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
    } // Permet de savoir s'il y a un event sur la case.

    private PairEffector checkEffects(ArrayList<Effector> wherePlayerCanGo) {
        int rand = (int) (Math.random() * ((wherePlayerCanGo.size() - 1) + 1));
        if (rand < 0)
            rand = 0;
        PairEffector choice;

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

    private PairEffector filterRules(ArrayList<Effector> wherePlayerCanGo) {
        PairEffector event = checkEvents();

        if (event != null) {
            return event;
        } else {
            return checkEffects(wherePlayerCanGo);
        }
    }

    private PairEffector inferenceEngine(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {
        buildKnowledge(states);
        return filterRules(wherePlayerCanGo);
    }

    private PairEffector choiceWithProba(ArrayList<Environment.Forest.State> states, ArrayList<Effector> wherePlayerCanGo) {
        PairEffector choice = inferenceEngine(states, wherePlayerCanGo);
        measurePerf(choice);
        return choice;
    }

    public PairEffector play(ArrayList<Forest.State> states, ArrayList<Effector> wherePlayerCanGo, int mapSize) {
        System.out.println("[Player] I receive: " + states);

        PairEffector returnValue;

        this.agentMeasurement.put(Effector.Leave, 10 * mapSize);
        this.agentMeasurement.put(Effector.Death, -10 * mapSize);

        returnValue = choiceWithProba(states, wherePlayerCanGo);

        if (returnValue.direction == null)
            System.out.println("[Player] I play: " + returnValue.effector);
        else
            System.out.println("[Player] I play: " + returnValue.effector + " " + returnValue.direction);
        return returnValue;
    }
}
