/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.calibration;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.data.XML;
import toxi.geom.Plane;
import toxi.geom.Vec3D;

/**
 *
 * @author Jeremy Laviole <jeremy.laviole@inria.fr>
 */
public class PlaneAndProjectionCalibration extends Calibration {

    private HomographyCalibration homographyCalibration = new HomographyCalibration();
    private PlaneCalibration planeCalibration = new PlaneCalibration();


    @Override
    public void loadFrom(PApplet parent, String fileName) {
        planeCalibration.loadFrom(parent, fileName);
        homographyCalibration.loadFrom(parent, fileName);
    }

    public Vec3D project(Vec3D point) {
        Vec3D out = new Vec3D();
        project(point,out);
        return out;
    }
    
    public void project(Vec3D point, Vec3D projectedPoint) {
//        Vec3D out = homographyCalibration.mat.applyTo(getPlane().getProjectedPointOptimAlloc(point));
        Vec3D out = homographyCalibration.mat.applyTo(getPlane().getProjectedPoint(point));
        projectedPoint.set(out);
        projectedPoint.x /= projectedPoint.z;
        projectedPoint.y /= projectedPoint.z;
        projectedPoint.z = getPlane().getDistanceToPoint(point);
    }

    @Override
    public boolean isValid() {
        return planeCalibration.isValid() && homographyCalibration.isValid();
    }

    @Override
    public void addTo(XML xml) {
        planeCalibration.addTo(xml);
        homographyCalibration.addTo(xml);
    }

    @Override
    public void replaceIn(XML xml) {
        planeCalibration.replaceIn(xml);
        homographyCalibration.replaceIn(xml);
    }

    public PlaneCalibration getPlaneCalibration() {
        return planeCalibration;
    }

    public HomographyCalibration getHomographyCalibration() {
        return homographyCalibration;
    }

    public void setPlane(PlaneCalibration pc) {
        this.planeCalibration = pc;
    }

    public void setHomography(HomographyCalibration hc) {
        this.homographyCalibration = hc;
    }

    ///////// Generated Delegation Methods ///////////////
    public PMatrix3D getHomography() {
        return homographyCalibration.getHomography();
    }

    public PMatrix3D getHomographyInv() {
        return homographyCalibration.getHomographyInv();
    }

    public boolean orientation(Vec3D point, float value) {
        return planeCalibration.orientation(point, value);
    }

    public boolean orientation(Vec3D p) {
        return planeCalibration.orientation(p);
    }
    
    public boolean isUnderPlane(Vec3D point) {
        return planeCalibration.isUnderPlane(point);
    }

    public boolean hasGoodOrientationAndDistance(Vec3D point) {
        return planeCalibration.hasGoodOrientationAndDistance(point);
    }

    public boolean hasGoodDistance(Vec3D point) {
        return planeCalibration.hasGoodDistance(point);
    }

    public boolean hasGoodOrientation(Vec3D point) {
        return planeCalibration.hasGoodOrientation(point);
    }

    public float distanceTo(Vec3D point) {
        return planeCalibration.distanceTo(point);
    }

    public void moveAlongNormal(float value) {
        planeCalibration.moveAlongNormal(value);
    }

    public float getPlaneHeight() {
        return planeCalibration.getHeight();
    }

    public void setPlaneHeight(float planeHeight) {
        planeCalibration.setHeight(planeHeight);
    }

    public Plane getPlane() {
        return planeCalibration.getPlane();
    }

}
