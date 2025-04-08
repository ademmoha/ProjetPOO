/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private  int energy;
    private Direction direction;
    private boolean moveRequested = false;
    private boolean hasPickUpHedgehog = false;
    private long lastMoveTime = 0;


    private int diseaseLevel = 0;
    private int insecticideCount = 0;


    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.lastMoveTime = System.currentTimeMillis();
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        this.energy += game.configuration().energyBoost();
        if (this.energy > game.configuration().gardenerEnergy()) {
            this.energy = game.configuration().gardenerEnergy();
        }
        energyBoost.remove();
        System.out.println("Energy boost collected!");
    }





    @Override
    public void pickUp(Hedgehog hedgehog) {
        System.out.println("gagné");
        this.hasPickUpHedgehog = true;
    }

    public boolean isHasPickUpHedgehog() {
        return hasPickUpHedgehog;
    }

    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        // Vérifie si la position est dans les limites de la carte
        if (!game.world().getGrid().inside(nextPos)) {
            return false;
        }

        // Récupère l'objet décor à la position suivante
        Decor nextDecor = game.world().getGrid().get(nextPos);

        // Si aucun décor, alors on peut avancer
        if (nextDecor == null) {
            return true;
        }

        // Sinon, on vérifie si ce décor est franchissable
        return nextDecor.walkableBy(this);
    }


    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        if (next != null)
            next.pickUpBy(this); // <-- Ici on active l'interaction avec les bonus
        return nextPos;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                energy = Math.max(0, energy - 1);
                lastMoveTime = System.currentTimeMillis();
            }
        } else {
            long currentTime = System.currentTimeMillis();
            long duration = game.configuration().energyRecoverDuration();
            if (currentTime - lastMoveTime >= duration) {
                energy = Math.min(100, energy + 1);
                lastMoveTime = currentTime;
            }
        }
        moveRequested = false;
    }

    public void hurt(int damage) {
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }




    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public int getInsecticideCount() {
        return insecticideCount;
    }

}
