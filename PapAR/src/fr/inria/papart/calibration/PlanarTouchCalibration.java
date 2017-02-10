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
import processing.data.XML;
import toxi.geom.Vec3D;

/**
 *
 * @author jiii
 */
public class PlanarTouchCalibration extends Calibration {

    static final String PLANAR_TOUCH_CALIBRATION_XML_NAME = "PlanarTouchCalibration";

    static final String MAX_DIST_XML_NAME = "MaxDistance";
    static final String MIN_COPO_SIZE_XML_NAME = "MinConnectedCompoSize";
    static final String MIN_HEIGHT_XML_NAME = "MinHeight";
    static final String MAX_RECURSION_XML_NAME = "MaxRecursion";
    static final String TRACKING_FORGET_TIME_XML_NAME = "TrackingForgetTime";
    static final String TRACKING_MAX_DIST_TIME_XML_NAME = "TrackingMaxDist";
    static final String SEARCH_DEPTH_XML_NAME = "SearchDepth";
    static final String PRECISION_XML_NAME = "Precision";

    // Variable parameters... going to a specific class for saving.  
    private float maximumDistance = 10f;    // in mm
    private float minimumHeight = 1; // mm

    private int minimumComponentSize = 3;   // in px
    private int searchDepth = 10;
    private int maximumRecursion = 500;
    private int precision = 2; // pixels

    // tracking
    private int trackingForgetTime = 250; // ms 
    private float trackingMaxDistance = 30; // in mm

    // TODO: implement this !
    @Override
    public boolean isValid() {
        return true;
    }

    private XML createXML() {
        XML root = new XML(PLANAR_TOUCH_CALIBRATION_XML_NAME);
        setIn(root);
        return root;
    }

    private void setIn(XML xml) {
        xml.setFloat(MAX_DIST_XML_NAME, maximumDistance);
        xml.setFloat(MIN_HEIGHT_XML_NAME, minimumHeight);

        xml.setInt(MIN_COPO_SIZE_XML_NAME, minimumComponentSize);
        xml.setInt(MAX_RECURSION_XML_NAME, maximumRecursion);
        xml.setInt(SEARCH_DEPTH_XML_NAME, searchDepth);
        xml.setInt(PRECISION_XML_NAME, precision);

        xml.setInt(TRACKING_FORGET_TIME_XML_NAME, trackingForgetTime);
        xml.setFloat(TRACKING_MAX_DIST_TIME_XML_NAME, trackingMaxDistance);
    }

    private void getFrom(XML xml) {
        maximumDistance = xml.getFloat(MAX_DIST_XML_NAME);
        minimumComponentSize = xml.getInt(MIN_COPO_SIZE_XML_NAME);
        minimumHeight = xml.getFloat(MIN_HEIGHT_XML_NAME);
        maximumRecursion = xml.getInt(MAX_RECURSION_XML_NAME);

        searchDepth = xml.getInt(SEARCH_DEPTH_XML_NAME);
        precision = xml.getInt(PRECISION_XML_NAME);

        trackingForgetTime = xml.getInt(TRACKING_FORGET_TIME_XML_NAME);
        trackingMaxDistance = xml.getFloat(TRACKING_MAX_DIST_TIME_XML_NAME);
    }

    public void setTo(PlanarTouchCalibration calib) {
        this.maximumDistance = calib.maximumDistance;
        this.maximumRecursion = calib.maximumRecursion;
        this.minimumComponentSize = calib.minimumComponentSize;
        this.minimumHeight = calib.minimumHeight;

        this.searchDepth = calib.searchDepth;
        this.precision = calib.precision;

        this.trackingForgetTime = calib.trackingForgetTime;
        this.trackingMaxDistance = calib.trackingMaxDistance;
    }

    @Override
    public void addTo(XML xml) {
        xml.addChild(createXML());
    }

    @Override
    public void replaceIn(XML xml) {
        XML element = xml.getChild(PLANAR_TOUCH_CALIBRATION_XML_NAME);
        setIn(element);
    }

    @Override
    public void loadFrom(PApplet parent, String fileName) {
        XML root = parent.loadXML(fileName);
        XML planarTouchCalibNode = root.getChild(PLANAR_TOUCH_CALIBRATION_XML_NAME);
        getFrom(planarTouchCalibNode);
    }

    public float getMaximumDistance() {
        return maximumDistance;
    }

    public void setMaximumDistance(float maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public int getMinimumComponentSize() {
        return minimumComponentSize;
    }

    public void setMinimumComponentSize(int minimumComponentSize) {
        this.minimumComponentSize = minimumComponentSize;
    }

    public float getMinimumHeight() {
        return minimumHeight;
    }

    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public int getMaximumRecursion() {
        return maximumRecursion;
    }

    public void setMaximumRecursion(int maximumRecursion) {
        this.maximumRecursion = maximumRecursion;
    }

    public int getTrackingForgetTime() {
        return trackingForgetTime;
    }

    public void setTrackingForgetTime(int trackingForgetTime) {
        this.trackingForgetTime = trackingForgetTime;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    /**
     * Number of neighbours seen at a time.
     *
     * @param searchDepth
     */
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public int getPrecision() {
        return precision;
    }

    /**
     * Precision is how much we divide the resolution on X and Y. A precision of
     * 1 is the best, 2 or 3 is OK for touch, 4 or 5 is OK for a hovering hand
     *
     * @param precision
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public float getTrackingMaxDistance() {
        return this.trackingMaxDistance;
    }

    public void setTrackingMaxDistance(float trackDistance) {
        this.trackingMaxDistance = trackDistance;
    }

}
