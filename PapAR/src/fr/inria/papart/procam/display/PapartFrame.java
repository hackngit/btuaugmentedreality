/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.procam.display;

import javax.swing.JFrame;
import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

/**
 * TEST CODE.
 * @author Jeremy Laviole <jeremy.laviole@inria.fr>
 */
public class PapartFrame extends JFrame {

    ARApplet applet;
    PApplet parent;
    BaseDisplay display;

    /**
     * WARNING IT IS BROKEN IN PROCESSING 3 
     * @param parent
     * @param display 
     */
    public PapartFrame(PApplet parent, BaseDisplay display) {

        this.parent = parent;
        this.display = display;

        setSize(display.getWidth(), display.getHeight());

        applet = new ARApplet();
//        applet.init();

        display.manualMode();

        // Force initialization again, to create the texture at the right place.
//        display.automaticMode();
//        this.add(applet);

        this.setVisible(true);

    }

    class ARApplet extends PApplet {

        boolean first = true;

        public void setup() {
            size(display.getWidth(),
                    display.getHeight(),
                    OPENGL);

        }

        public void draw() {

            if (first) {
                println("1 " + display.graphics);
                display.setParent(this);
                display.initGraphics();

                println("2 " + display.graphics);
                display.graphics.beginDraw();
                display.graphics.background(0, 100, 255);
                display.graphics.endDraw();
                first = false;
            }
//            display.pre();

            background(random(100));
            rect(0, 0, 100, 100);

            display.graphics.beginDraw();
            display.graphics.background(random(255), 100, 255);
            display.renderScreens();
            display.graphics.endDraw();

            image(display.render(), 0, 100, 100, 100);

        }
    }

}
