/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.cyber.dca.engine.util;

import coppelia.FloatW;
import coppelia.FloatWA;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;

import java.text.DecimalFormat;

import static java.lang.Math.*;
import static java.lang.Math.sqrt;

/**
 *
 * @author Jan
 */
public class Vector3 {
    
    private double x;
    private double y;
    private double z;

    public Vector3(Vector3 vector3) {
        this.x = vector3.getX();
        this.y = vector3.getY();
        this.z = vector3.getZ();
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(FloatWA array) {
        this.x = array.getArray()[0];
        this.y = array.getArray()[1];
        this.z = array.getArray()[2];
    }

    public Vector3(double x) {
        this.x = x;
        this.y = x;
        this.z = x;
    }

    public Vector3() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void timesScalar(double scalar){
        x = getX()*scalar;
        y = getY()*scalar;
        z = getZ()*scalar;
    }

    public static Vector3 timesScalar(Vector3 vector3, double scalar){
        return new Vector3(vector3.getX()*scalar,vector3.getY()*scalar,vector3.getZ()*scalar);
    }

    public static Vector3 timesScalar(Vector3 vector3, long scalar){
        return new Vector3(vector3.getX()*scalar,vector3.getY()*scalar,vector3.getZ()*scalar);
    }

    public static Vector3 plus(Vector3 a, Vector3 b){
        return new Vector3(a.getX()+b.getX(),a.getY()+b.getY(),a.getZ()+b.getZ());
    }

    public void cut(double max){
        if(x>max)this.x = max;
        if(x<-max)this.x = -max;
        if(y>max)this.y = max;
        if(y<-max)this.y = -max;
        if(z>max)this.z = max;
        if(z<-max)this.z = -max;
    }

    public void plus(Vector3 pos){
        this.x = getX() + pos.getX();
        this.y = getY() + pos.getY();
        this.z = getZ() + pos.getZ();
    }

    public void minus(Vector3 pos){
        this.x = getX() - pos.getX();
        this.y = getY() - pos.getY();
        this.z = getZ() - pos.getZ();
    }

    public static Vector3 minus(Vector3 a, Vector3 b){
        return new Vector3(a.getX()-b.getX(),a.getY()-b.getY(),a.getZ()-b.getZ());
    }

    public double dot(Vector3 pos){
    return x*pos.getX()+y*pos.getY()+z*pos.getZ();
    }

    public static double dot(Vector3 a, Vector3 b){
        return b.getX()*a.getX()+b.getY()*a.getY()+b.getZ()*a.getZ();
    }

    public Vector3 cross(Vector3 b){
        double c1 = y*b.getZ() - z*b.getY();
        double c2 = z*b.getX() - x*b.getZ();
        double c3 = x*b.getY() - y*b.getX();
        return new Vector3(c1,c2,c3);
    }

    public static Vector3 cross(Vector3 a, Vector3 b){
        double c1 = a.getY()*b.getZ() - a.getZ()*b.getY();
        double c2 = a.getZ()*b.getX() - a.getX()*b.getZ();
        double c3 = a.getX()*b.getY() - a.getY()*b.getX();
        return new Vector3(c1,c2,c3);
    }

    public void unitVector(){
        if(norm3()!=0)timesScalar(1/norm3());
    }

    public Vector3 newUnitVector(){
        Vector3 unitVector = new Vector3(this);
        if(norm3()!=0){
        unitVector.timesScalar(1/norm3());
        return unitVector;
        }else return unitVector;
    }

    public double norm(){
        return norm2();
        //return sqrt(x*x+y*y+z*z);
    }

    public double norm2(){
        return sqrt(x*x+y*y);
    }

    public double norm3() {
        return sqrt(x*x+y*y+z*z);
    }

    public static double norm(Vector3 vec){
        return norm2(vec);
        //return sqrt(pow(vec.getX(), 2)+ pow(vec.getY(), 2)+ pow(vec.getZ(), 2));
    }

    public static double norm2(Vector3 vec){
        return sqrt(pow(vec.getX(), 2)+ pow(vec.getY(), 2));
    }

    public static double norm3(Vector3 vec){
        return sqrt(pow(vec.getX(), 2)+ pow(vec.getY(), 2) + pow(vec.getZ(), 2));
    }

    public double distance(Vector3 vec){
        return sqrt(pow(getX()-vec.getX(),2) + pow(getY()-vec.getY(),2) + pow(getZ()-vec.getZ(),2) );
    }

    public boolean isVectorNull(){
        return (getX()==0.0&&getY()==0.0&&getY()==0.0);
    }

    public Vector2 toVector2(){
        return new Vector2(getX(),getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3 vector3 = (Vector3) o;

        if (Double.compare(vector3.x, x) != 0) return false;
        if (Double.compare(vector3.y, y) != 0) return false;
        return Double.compare(vector3.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "["  + x + "\t" + y + "\t" + z + ']';
    }

    public String toStringRounded(){
        DecimalFormat df = new DecimalFormat("####0.000");
        return "["  + df.format(x) + "\t" + df.format(y) + "\t" + df.format(z) + ']';
    }

}
