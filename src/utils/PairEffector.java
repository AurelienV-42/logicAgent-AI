package utils;

import Agent.Player;

public class PairEffector {
    public final Player.Effector effector;
    public final Player.Effector direction;

    public PairEffector(Player.Effector effector) {
        this.effector = effector;
        direction = null;
    }

    public PairEffector(Player.Effector effector, Player.Effector direction) {
        this.effector = effector;
        this.direction = direction;
    }
};
