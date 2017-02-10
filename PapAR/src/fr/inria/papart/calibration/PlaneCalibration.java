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
import processing.core.PVector;
import processing.data.XML;
import toxi.geom.Plane;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;

/**
 *
 * @author Jeremy Laviole <jeremy.laviole@inria.fr>
 */
public class PlaneCalibration extends Calibration {

    public static final float HEIGHT_NOT_SET = -1;

    static final String PLANE_XML_NAME = "Plane";
    static final String PLANE_POS_XML_NAME = "Position";
    static final String PLANE_NORMAL_XML_NAME = "Normal";
    static final String PLANE_HEIGHT_XML_NAME = "Height";
    static final String X_XML_NAME = "x";
    static final String Y_XML_NAME = "y";
    static final String Z_XML_NAME = "z";

    private float height = HEIGHT_NOT_SET;
    private Plane plane;

    public boolean orientation(Vec3D point, float value) {
        return plane.classifyPoint(point, 0.05f) == Plane.Classifier.BACK;
    }

    public boolean orientation(Vec3D p) {
        float d = plane.sub(p).dot(plane.normal);
        if (d < -MathUtils.EPS) {
            return false;
        } else if (d > MathUtils.EPS) {
            return true;
        }
        return true; //ON_PLANE;
    }

    public boolean hasGoodOrientationAndDistance(Vec3D point) {
        return orientation(point) && plane.getDistanceToPoint(point) <= height;
    }

    public boolean isUnderPlane(Vec3D point) {
        return !orientation(point) && plane.getDistanceToPoint(point) <= height;
    }

    public boolean hasGoodDistance(Vec3D point) {
        return plane.getDistanceToPoint(point) <= height;
    }

    public boolean hasGoodOrientation(Vec3D point) {
        return orientation(point);
    }

    public float distanceTo(Vec3D point) {
        return plane.getDistanceToPoint(point);
    }
    
    public float distanceTo(PVector point) {
        return plane.getDistanceToPoint(new Vec3D(point.x, point.y, point.z));
    }

    public void flipNormal() {
        plane.normal = plane.normal.invert();
    }

    public void moveAlongNormal(float value) {
        plane.x = plane.x + value * plane.normal.x;
        plane.y = plane.y + value * plane.normal.y;
        plane.z = plane.z + value * plane.normal.z;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float planeHeight) {
        this.height = planeHeight;
    }

    protected void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Plane getPlane() {
        assert (isValid());
        return plane;
    }

    public boolean isValid() {
        return this.plane != null && this.height != HEIGHT_NOT_SET;
    }

    @Override
    public void replaceIn(XML xml) {
        XML planeNode = xml.getChild(PLANE_XML_NAME);
        setVectorIn(planeNode.getChild(PLANE_POS_XML_NAME), (Vec3D) plane);
        setVectorIn(planeNode.getChild(PLANE_NORMAL_XML_NAME), plane.normal);
        planeNode.getChild(PLANE_HEIGHT_XML_NAME).setFloat(PLANE_HEIGHT_XML_NAME, height);
    }

    @Override
    public void addTo(XML xml) {
        XML root = new XML(PLANE_XML_NAME);
        XML pos = createXML(PLANE_POS_XML_NAME, (Vec3D) plane);
        XML normal = createXML(PLANE_NORMAL_XML_NAME, plane.normal);
        XML height = new XML(PLANE_HEIGHT_XML_NAME);
        height.setFloat(PLANE_HEIGHT_XML_NAME, this.height);

        root.addChild(pos);
        root.addChild(normal);
        root.addChild(height);
        xml.addChild(root);
    }

    private XML createXML(String name, Vec3D v) {
        XML node = new XML(name);
        this.setVectorIn(node, v);
        return node;
    }

    private void setVectorIn(XML node, Vec3D v) {
        node.setFloat(X_XML_NAME, v.x);
        node.setFloat(Y_XML_NAME, v.y);
        node.setFloat(Z_XML_NAME, v.z);
    }

    private Vec3D getVectorFrom(XML node) {
        Vec3D v = new Vec3D();
        v.x = node.getFloat(X_XML_NAME);
        v.y = node.getFloat(Y_XML_NAME);
        v.z = node.getFloat(Z_XML_NAME);
        return v;
    }

    @Override
    public void loadFrom(PApplet parent, String fileName) {
        XML root = parent.loadXML(fileName);
        XML planeNode = root.getChild(PLANE_XML_NAME);
        XML posNode = planeNode.getChild(PLANE_POS_XML_NAME);
        XML normalNode = planeNode.getChild(PLANE_NORMAL_XML_NAME);
        XML heightNode = planeNode.getChild(PLANE_HEIGHT_XML_NAME);

        Vec3D position = getVectorFrom(posNode);
        Vec3D normal = getVectorFrom(normalNode);
        float h = heightNode.getFloat(PLANE_HEIGHT_XML_NAME);

        this.plane = new Plane();
        plane.set(position);
        plane.normal.set(normal);
        setHeight(h);
    }

    @Override
    public String toString() {
        return this.plane.toString() + " " + this.height;
    }

    public static float DEFAULT_PLANE_SHIFT = -8f;
    public static float DEFAULT_PLANE_HEIGHT = 15f;

    /**
     * Get a plane from a 3D  matrix. 
     * Here the size is not that important, might be removed.  
     * @param mat
     * @param size
     * @return 
     */
    public static PlaneCalibration CreatePlaneCalibrationFrom(PMatrix3D mat, PVector size) {
        PlaneCreator planeCreator = new PlaneCreator();

        planeCreator.addPoint(new Vec3D(mat.m03, mat.m13, mat.m23));
        mat.translate(size.x, 0, 0);
        planeCreator.addPoint(new Vec3D(mat.m03, mat.m13, mat.m23));
        mat.translate(0, size.y, 0);
        planeCreator.addPoint(new Vec3D(mat.m03, mat.m13, mat.m23));

        planeCreator.setHeight(DEFAULT_PLANE_HEIGHT);
        assert (planeCreator.isComputed());
        PlaneCalibration planeCalibration = planeCreator.getPlaneCalibration();
        
        planeCalibration.flipNormal();
//        planeCalibration.moveAlongNormal(DEFAULT_PLANE_SHIFT);
        assert (planeCalibration.isValid());
        return planeCalibration;
    }

}
