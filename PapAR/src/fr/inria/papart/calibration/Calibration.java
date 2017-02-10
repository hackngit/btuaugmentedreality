/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.calibration;

import java.io.FileNotFoundException;
import processing.core.PApplet;
import processing.data.XML;

/**
 *
 * @author Jeremy Laviole <jeremy.laviole@inria.fr>
 */
public abstract class Calibration {

    static final String CALIBRATION_XML_NAME = "Calibration";

    public abstract boolean isValid();

    public abstract void addTo(XML xml);

    public abstract void replaceIn(XML xml);

    public void saveTo(PApplet parent, String fileName) {
        assert(isValid());
        XML root = new XML(Calibration.CALIBRATION_XML_NAME);
        this.addTo(root);
        parent.saveXML(root, fileName);
    }

    
    public void replaceIn(PApplet parent, String fileName) {
        assert(isValid());
        XML root = parent.loadXML(fileName);
        this.replaceIn(root);
        parent.saveXML(root, fileName);
    }

    public abstract void loadFrom(PApplet parent, String fileName);
}
