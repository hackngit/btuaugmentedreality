import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import fr.inria.papart.procam.display.*; 
import org.bytedeco.javacpp.*; 
import org.reflections.*; 
import TUIO.*; 
import toxi.geom.*; 
import processing.video.*; 
import processing.app.Base; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class saveLocation extends PApplet {

// PapARt library












Papart papart;

public void settings(){
    size(200, 200, P3D);
}

public void setup() {

    papart = Papart.seeThrough(this);
    papart.loadSketches();
    papart.startTracking();
}

public void draw() {
}



public void keyPressed() {

    if(key == 's'){
	app.saveLocationTo("loc.xml");
	println("Position saved");
    }

    if(key == 'l'){
	app.loadLocationFrom("loc.xml");
    }

    // Move again
    if(key == 'm'){
	app.useManualLocation(false);
    }

}
MyApp app;

public class MyApp extends PaperScreen {

  public void setup() {
    setDrawingSize(297, 210);
    loadMarkerBoard(Papart.markerFolder + "A3-small1.cfg", 297, 210);
    app = this;
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
    String[] appletArgs = new String[] { "saveLocation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
