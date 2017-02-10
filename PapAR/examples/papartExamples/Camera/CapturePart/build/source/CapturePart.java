import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.camera.*; 
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

public class CapturePart extends PApplet {



public class MyApp  extends PaperScreen {

    TrackedView boardView;

    // 5cm  ->  50 x 50 pixels 
    PVector captureSize = new PVector(50, 50);
    PVector origin = new PVector(40, 40);
    int picSize = 64; // Works better with power  of 2
    
    public void setup() {
	setDrawingSize(297, 210);
	loadMarkerBoard(sketchPath() + "/data/A3-small1.cfg", 297, 210);
	
	boardView = new TrackedView(this);
	boardView.setCaptureSizeMM(captureSize);

	boardView.setImageWidthPx(picSize);
	boardView.setImageHeightPx(picSize);

	boardView.setBottomLeftCorner(origin);

	boardView.init();
    }


    public void draw() {
	beginDraw2D();

	clear();

	fill(200, 100, 20);
	rect(10, 10, 10, 10);
	PImage out = boardView.getViewOf(cameraTracking);
    
	if(out != null){
	    image(out, 120, 40, picSize, picSize);
	}
	endDraw();
    }
}

// PapARt library






public void settings(){
    size(200, 200, P3D);
}

public void setup(){
    Papart papart = Papart.seeThrough(this);
    papart.loadSketches();
    papart.startTracking();
}

public void draw(){

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "CapturePart" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
