import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import fr.inria.papart.procam.display.*; 
import org.bytedeco.javacpp.*; 
import toxi.geom.*; 
import fr.inria.skatolo.*; 
import fr.inria.skatolo.gui.controllers.*; 
import java.awt.Frame; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class calibration extends PApplet {






Papart papart;
// ProjectorDisplay ardisplay;
ARDisplay ardisplay;

float focal, cx, cy;
PMatrix3D projIntrinsics;

float paperDistance = 800; // in mm

public void settings(){
    size(200, 200, P3D);
}

public void setup() {

    Papart.cameraCalib = "calib.xml";

    Papart.seeThrough(this);

    papart =  Papart.getPapart();
    ardisplay = papart.getARDisplay();
    ardisplay.manualMode();

    projIntrinsics = ardisplay.getIntrinsics();
    focal = projIntrinsics.m00;
    cx = projIntrinsics.m02;
    cy = projIntrinsics.m12;

    initGui();

}

PMatrix3D objectArdisplayTransfo, pos;
PGraphicsOpenGL arGraphics;

public void draw() {

    projIntrinsics.m00 = focal;
    projIntrinsics.m11 = focal;
    projIntrinsics.m02 = cx;
    projIntrinsics.m12 = cy;

    // Update the rendering.
    ardisplay.updateIntrinsicsRendering();

    ProjectiveDeviceP pdp = ardisplay.getProjectiveDeviceP();

    // Update the estimation.
    pdp.updateFromIntrinsics();

    arGraphics = ardisplay.beginDraw();
    arGraphics.clear();
    arGraphics.scale(1, -1, 1);

    // set the distance of the paper (facing the camera)
    drawAt(paperDistance); // in mm

    // draw the squares (printed from data/squares.svg)
    drawSquares();

    ardisplay.endDraw();

    // draw the image
    image(papart.getCameraTracking().getImage(), 0, 0, width, height);

    // draw the AR Layer
    DrawUtils.drawImage((PGraphicsOpenGL) g,
			ardisplay.render(),
			0, 0, width, height);
}

public void drawAt(float distance){
    arGraphics.translate(0, 0, distance);
}

public void drawSquares(){

    arGraphics.noFill();

    // thin 130mm square
    arGraphics.rect(-10, -10, 120, 120);


    arGraphics.fill(50, 50, 200, 100);

    // filled 100mm square
    arGraphics.rect(0, 0, 100, 100);


    // small green square 50mm
    arGraphics.fill(0, 191, 100, 100);
    arGraphics.rect(150, 80, 50, 50);
}

public void keyPressed() {

  if (key == 's') {

      ProjectiveDeviceP pdp = ardisplay.getProjectiveDeviceP();
      pdp.saveTo(this, "calib.xml");
      println("Saved");
  }

  if (key == 'S') {
      ProjectiveDeviceP pdp = ardisplay.getProjectiveDeviceP();
      pdp.saveTo(this, Papart.calibrationFolder + "camera.xml");
      println("Saved as default");
  }

}





Bang saveScanBang, decodeBang;

public void initGui(){
    ControlFrame controlFrame = new ControlFrame(this);

}


// the ControlFrame class extends PApplet, so we
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
public class ControlFrame extends PApplet {
  int w, h;
  Skatolo skatolo;
  Object parent;

    public ControlFrame(PApplet parent){
	super();
	this.parent = parent;
	PApplet.runSketch(new String[]{this.getClass().getName()}, this);
    }

    public void settings(){
	size(1000, 300);
    }

  public void setup() {
    skatolo = new Skatolo(this);

    // add a horizontal sliders, the value of this slider will be linked
    // to variable 'sliderValue'

    int width = ardisplay.getWidth();
    int height = ardisplay.getHeight();

    skatolo.addSlider("focal").plugTo(parent, "focal")
	.setPosition(10, 20)
	.setRange(500, 3000)
	.setSize(800,20)
	.setValue(1000)
	;

    skatolo.addSlider("cx").plugTo(parent, "cx")
	.setPosition(10, 60)
	.setRange(0, width  *2)
	.setSize(800,20)
	.setValue(width / 2)
	;

    skatolo.addSlider("cy").plugTo(parent, "cy")
	.setPosition(10, 100)
	.setSize(800,20)
	.setRange(0 , height  *3)
	.setValue(height / 2)
	;

  }

  public void draw() {
      background(100);
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "calibration" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
