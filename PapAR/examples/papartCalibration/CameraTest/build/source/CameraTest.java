import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import fr.inria.papart.procam.camera.*; 
import org.bytedeco.javacpp.*; 
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

public class CameraTest extends PApplet {

// PapARt library










Camera camera;

int resX = 640;
int resY = 480;

public void settings(){
    // fullScreen(P3D);
    size(resX, resY, P3D);
}

public void setup() {
    
    if (frame != null) {
	frame.setResizable(true);
    }
    

     camera = CameraFactory.createCamera(Camera.Type.OPEN_KINECT, "0");    
     // camera = CameraFactory.createCamera(Camera.Type.OPENCV, "0");    
    // camera = CameraFactory.createCamera(Camera.Type.PROCESSING, "/dev/video1");
    // camera = CameraFactory.createCamera(Camera.Type.FLY_CAPTURE, 0);

    
    camera.setParent(this);
    camera.setSize(resX, resY);
    //    ((CameraFlyCapture) camera).setBayerDecode(true);
    camera.start();
    camera.setThread();
    
}

public void draw() {
    PImage im = camera.getPImage();
    if(im != null)
	image(im, 0, 0, width, height);

}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "CameraTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
