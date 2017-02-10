import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import org.bytedeco.javacpp.*; 
import org.bytedeco.javacpp.opencv_core; 
import org.reflections.*; 
import TUIO.*; 
import toxi.geom.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PaperApp2D extends PApplet {

// PapARt library









boolean useProjector = false;
Papart papart;

public void setup() {

    // use our calibration
    // Papart.cameraCalibName = "camera.xml";
    // Papart.cameraCalib = Papart.calibrationFolder + Papart.cameraCalibName;
    
    papart = Papart.seeThrough(this);

    papart.loadSketches();
    papart.startTracking();
}

public void settings(){
    size(300, 300, P3D);
}

public void draw() {
    
}



public class MyApp extends PaperScreen {

  public void setup() {
    setDrawingSize(297, 210);
    loadMarkerBoard(Papart.markerFolder + "A3-small1.cfg", 297, 210);
  }

  public void draw() {
    beginDraw2D();
    background(100, 0, 0);
    fill(200, 100, 20);
    rect(10, 10, 100, 30);
    endDraw();
  }
 }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PaperApp2D" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
