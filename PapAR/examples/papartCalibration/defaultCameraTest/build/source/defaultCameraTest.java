import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import org.bytedeco.javacpp.*; 
import org.reflections.*; 
import TUIO.*; 
import toxi.geom.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class defaultCameraTest extends PApplet {

// PapARt library






Papart papart;

public void settings(){
    size(200, 200, P3D);
}
    

public void setup() {

    papart = Papart.seeThrough(this);
    
    papart.loadSketches();
    papart.startTracking();
    frameRate(200);
}

public void draw() {
}


public void keyPressed() {

  // Placed here, bug if it is placed in setup().
  if (key == ' ')
    	papart.defaultFrameLocation();
}
public class MyApp  extends PaperScreen {

  public void setup() {
    setDrawingSize(297, 210);
    loadMarkerBoard(sketchPath() + "/data/A3-small1.cfg", 297, 210);
    // loadMarkerBoard(sketchPath() + "/data/frame5.png",
    // 		    420, 297);

  }

  public void draw() {
      //      getLocation().print();
    beginDraw2D();
    background(100, 0, 0);
    fill(200, 100, 20);
    rect(10, 10, 100, 30);
    endDraw();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "defaultCameraTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
