/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.calibration;

import fr.inria.papart.procam.camera.Camera;
import fr.inria.papart.procam.camera.CameraFactory;
import processing.core.PApplet;
import processing.data.XML;

/**
 *
 * @author Jeremy Laviole <jeremy.laviole@inria.fr>
 */
public class CameraConfiguration  extends Calibration {
    
    static final String CAMERA_XML_NAME = "Camera";
    static final String CAMERA_ID_XML_NAME = "CameraID";
    static final String CAMERA_NAME_XML_NAME = "CameraName";
    static final String CAMERA_TYPE_XML_NAME = "CameraType";

    private String cameraName = "";
    private Camera.Type cameraType = Camera.Type.OPENCV;

    @Override
    public boolean isValid() {
        // todo check ID, name & type ?
        return true;
    }

    @Override
    public void addTo(XML xml) {
        xml.addChild(createCameraNode());
    }

    @Override
    public void replaceIn(XML xml) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Camera createCamera(){
       return CameraFactory.createCamera(cameraType, cameraName);
    }

    @Override
    public void loadFrom(PApplet parent, String fileName) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        XML root = parent.loadXML(fileName);

        XML cameraNode = root.getChild(CAMERA_XML_NAME);
        loadCameraFrom(cameraNode);
    }


    private void loadCameraFrom(XML cameraNode) {
        this.cameraName = cameraNode.getString(CAMERA_NAME_XML_NAME);
        this.cameraType = Camera.Type.valueOf(cameraNode.getString(CAMERA_TYPE_XML_NAME));
    }

    private XML createCameraNode() {
        XML cameraNode = new XML(CAMERA_XML_NAME);
        cameraNode.setString(CAMERA_NAME_XML_NAME, cameraName);
        String type = this.cameraType.name();
        cameraNode.setString(CAMERA_TYPE_XML_NAME, type);

        return cameraNode;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Camera.Type getCameraType() {
        return cameraType;
    }

    public void setCameraType(Camera.Type cameraType) {
        this.cameraType = cameraType;
    }
}
