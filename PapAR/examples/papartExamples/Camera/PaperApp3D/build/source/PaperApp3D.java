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

public class PaperApp3D extends PApplet {

// PapARt library







public void settings(){
    size(200, 200, P3D);
}

public void setup(){
    Papart papart = Papart.seeThrough(this);
    papart.loadSketches() ;
    papart.startTracking() ;
}

public void draw(){
}

public class MyApp  extends PaperScreen {

    PShape rocketShape;
    
    public void setup(){
	setDrawingSize(297, 210);
	loadMarkerBoard(Papart.markerFolder + "A3-small1.cfg", 297, 210);
	rocketShape = loadShape("rocket.obj");
    }

    public void draw(){
	beginDraw3D();
	pushMatrix();
	scale(0.5f);
	rotateX(HALF_PI);
	rotateY((float) millis() / 1000f) ;
	shape(rocketShape);
	popMatrix();
	
	translate(100, 10, 0);
	box(50);
	
	endDraw();
    }

}
    
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PaperApp3D" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
