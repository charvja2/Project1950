/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 *
 * @author Jan
 */
public class Unit {

    public static int unitCount;

    protected int id;
    protected String name;

    private final String vrepUnitName;
    private int vrepUnitHandle;

    private final String vrepTargetName;
    private int vrepTargetHandle;
    
    protected Vector3 position;
    protected Vector3 targetPosition;

    protected Vector3 linearVelocity;
    protected Vector3 angularVelocity;
    
    public Unit(String vrepObjectName, String vrepTargetName) {
        this.vrepTargetName = vrepTargetName;
        this.vrepUnitName = vrepObjectName;
        this.id = unitCount;
        unitCount++;
        this.name = "Unit" + Integer.toString(id);

        position = new Vector3();
        targetPosition = new Vector3();
        linearVelocity = new Vector3();
        angularVelocity = new Vector3();
    }

    public int getId() {
        return id;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setVrepObjectHandle(int vrepObjectHandle) {
        this.vrepUnitHandle = vrepObjectHandle;
    }

    public int getVrepObjectHandle() {
        return vrepUnitHandle;
    }

    public String getVrepUnitName() {
        return vrepUnitName;
    }

    public String getVrepTargetName() {
        return vrepTargetName;
    }

    public int getVrepTargetHandle() {
        return vrepTargetHandle;
    }

    public Vector3 getTargetPosition() {
        return targetPosition;
    }

    public void setVrepTargetHandle(int vrepTargetHandle) {
        this.vrepTargetHandle = vrepTargetHandle;
    }

    public Vector3 getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(Vector3 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public Vector3 getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setTargetPosition(Vector3 targetPosition) {
        this.targetPosition = targetPosition;
    }

    @Override
    public String toString() {
        return "Unit{" + "id=" + id + ",\t name=" + name +  ",\t vrepUnitName=" + vrepUnitName
                + ",\t vrepUnitHandle=" + vrepUnitHandle + ",\t vrepTargetName=" + vrepTargetName
                + ",\t vrepTargetHandle=" + vrepTargetHandle + ",\t position=" + position.toString()
                + ",\t targetPosition=" + targetPosition.toString() + ",\t linearVelocity=" + linearVelocity.toString()
                +",\t angularVelocity=" + angularVelocity.toString()+'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unit unit = (Unit) o;

        if (id != unit.id) return false;
        if (vrepUnitHandle != unit.vrepUnitHandle) return false;
        if (!name.equals(unit.name)) return false;
        return vrepUnitName.equals(unit.vrepUnitName);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + vrepUnitName.hashCode();
        result = 31 * result + vrepUnitHandle;
        return result;
    }
}
