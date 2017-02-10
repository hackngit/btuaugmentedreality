/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.procam;

import fr.inria.papart.calibration.ProjectiveDeviceCalibration;
import java.nio.FloatBuffer;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.ProjectiveDevice;
import org.bytedeco.javacv.ProjectorDevice;
import org.bytedeco.javacpp.ARToolKitPlus.ARMarkerInfo;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core.CvMat;

import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix3D;
import processing.core.PVector;
import toxi.geom.Ray3D;
import toxi.geom.Vec3D;

/**
 *
 * @author jiii
 */
public class ProjectiveDeviceP implements PConstants, HasExtrinsics {

    private PMatrix3D intrinsics;
    private PMatrix3D extrinsics;
    private ProjectiveDevice device;
    private int w, h;
    private float ifx;
    private float ify;
    private float fx;
    private float fy;
    private float cx;
    private float cy;
    private Mat intrinsicsMat;
    private boolean hasExtrinsics = false;
    private boolean handleDistorsion = false;

    private ProjectiveDeviceP() {
    }

    public float getFx() {
        return fx;
    }

    public float getFy() {
        return fy;
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }
    
//    public ProjectiveDeviceP(int width, int height) {
//        this.w = width;
//        this.h = height;
//    }
    public PMatrix3D getIntrinsics() {
        return this.intrinsics;
    }

    @Override
    public PMatrix3D getExtrinsics() {
        assert (this.hasExtrinsics());
        return this.extrinsics;
    }

    public ProjectiveDevice getDevice() {
        return this.device;
    }

    public int getWidth() {
        return this.w;
    }

    public int getHeight() {
        return this.h;
    }

    public int getSize() {
        return this.w * this.h;
    }

    @Override
    public boolean hasExtrinsics() {
        return this.hasExtrinsics;
    }

    public PVector getCoordinates(int offset) {
        return new PVector(offset % w, offset / w);
    }

    public boolean handleDistorsions() {
        return this.handleDistorsion;
    }

    public Vec3D pixelToWorld(int x, int y, float depthValue) {

        Vec3D result = new Vec3D();
        float depth = depthValue;
//        float depth = 1000 * depthLookUp[depthValue]; 
        result.x = (float) ((x - cx) * depth * ifx);
        result.y = (float) ((y - cy) * depth * ify);

        result.z = depth;
        return result;
    }

    public void pixelToWorld(int x, int y, float depthValue, Vec3D result) {

        float depth = depthValue;
//        float depth = 1000 * depthLookUp[depthValue]; 
        result.x = (float) ((x - cx) * depth * ifx);
        result.y = (float) ((y - cy) * depth * ify);

        result.z = depth;
    }

    // without depth value, focal distance is assumed
    @Deprecated
    public Vec3D pixelToWorld(int x, int y) {

        Vec3D result = new Vec3D();
        float depth = fx;
//        float depth = 1000 * depthLookUp[depthValue]; 
        result.x = (float) (x - cx);
        result.y = (float) (y - cy);
        result.z = depth;
        return result;
    }

    @Deprecated
    public PVector pixelToWorldP(int x, int y) {
        PVector result = new PVector();
        float depth = (fx + fy) / 2;
        result.x = (float) x - cx;
        result.y = (float) y - cy;
        result.z = depth;
        return result;
    }

    // To use with a projector...
    @Deprecated
    public PVector pixelToWorldPDistort(int x, int y, boolean distort) {
        PVector result = new PVector();

        if (distort) {
            double[] out = device.distort(x, y);
            result.x = (float) out[0];
            result.y = (float) out[1];
        }

        float depth = (fx + fy) / 2;
        result.x = (float) x - cx;
        result.y = (float) y - cy;
        result.z = depth;
        return result;
    }

    @Deprecated
    public PVector pixelToWorldPUndistort(int x, int y, boolean distort) {
        PVector result = new PVector();

        if (distort) {
            double[] out = device.undistort(x, y);
            result.x = (float) out[0];
            result.y = (float) out[1];
        }

        float depth = fx;
        result.x = (float) x - cx;
        result.y = (float) y - cy;
        result.z = depth;
        return result;
    }

    /* * Working, use this one for Low error !
        
     */
    public PVector pixelToWorldNormP(int x, int y) {
        PVector result = new PVector();
        result.x = ((float) x - cx) / fx;
        result.y = ((float) y - cy) / fy;
        result.z = 1;
        return result;
    }

    public PVector pixelToWorldNormPMM(int x, int y, float sizeX) {

        PVector result = pixelToWorldNormP(x, y);

        float sizeY = sizeX * ((float) h / (float) w);
        result.x *= (float) sizeX / (float) w;
        result.y *= (float) sizeY / (float) h;
        return result;
    }

    public int worldToPixel(Vec3D pt) {
        return worldToPixel(pt.x(), pt.y(), pt.z());
    }

    public int worldToPixel(float x, float y, float z) {
        // Reprojection 
        float invZ = 1.0f / z;

        int px = PApplet.constrain(PApplet.round((x * invZ * fx) + cx), 0, w - 1);
        int py = PApplet.constrain(PApplet.round((y * invZ * fy) + cy), 0, h - 1);

        return (int) (py * w + px);
    }

    public PVector worldToPixelCoord(Vec3D pt) {

        // Reprojection 
        float invZ = 1.0f / pt.z();

        int px = PApplet.constrain(PApplet.round((pt.x() * invZ * fx) + cx), 0, w - 1);
        int py = PApplet.constrain(PApplet.round((pt.y() * invZ * fy) + cy), 0, h - 1);

        return new PVector(px, py);
    }

    public PVector worldToPixelCoord(PVector pt) {

        // Reprojection 
        float invZ = 1.0f / pt.z;

        int px = PApplet.constrain(PApplet.round((pt.x * invZ * fx) + cx), 0, w - 1);
        int py = PApplet.constrain(PApplet.round((pt.y * invZ * fy) + cy), 0, h - 1);

        return new PVector(px, py);
    }

    public int worldToPixel(PVector pt) {

        // Reprojection 
        float invZ = 1.0f / pt.z;

        int px = PApplet.constrain(PApplet.round((pt.x * invZ * fx) + cx), 0, w - 1);
        int py = PApplet.constrain(PApplet.round((pt.y * invZ * fy) + cy), 0, h - 1);

        return (int) (py * w + px);
    }

    // TODO: find a name...
    public PVector worldToPixelReal(PVector pt) {

        // Reprojection 
        float invZ = 1.0f / pt.z;

        float px = ((pt.x * invZ * fx) + cx);
        float py = ((pt.y * invZ * fy) + cy);

        return new PVector(px, py);
    }

    public PVector worldToPixel(PVector pt, boolean undistort) {

        // Reprojection 
        float invZ = 1.0f / pt.z;

        int px = PApplet.constrain(PApplet.round((pt.x * invZ * fx) + cx), 0, w - 1);
        int py = PApplet.constrain(PApplet.round((pt.y * invZ * fy) + cy), 0, h - 1);

        if (undistort) {
            double[] out = device.distort(px, py);
            return new PVector((float) out[0], (float) out[1]);
        } else {
            return new PVector(px, py);
        }
    }

    public PVector createRayFrom(PVector pixels) {

        double[] out = device.undistort(pixels.x, pixels.y);

        float norm = PApplet.sqrt(PApplet.pow((float) out[0], 2)
                + PApplet.pow((float) out[1], 2)
                + 1.0f);

        PVector v = new PVector((float) out[0] / norm,
                (float) out[1] / norm,
                1.f / norm);
        return v;
    }

    public PMatrix3D estimateOrientation(PVector[] objectPoints,
            PVector[] imagePoints) {

        assert (objectPoints.length == imagePoints.length);

        Mat op = new Mat(objectPoints.length, 3, CV_32FC1);
        Mat ip = new Mat(imagePoints.length, 2, CV_32FC1);
        Mat rotation = new Mat(3, 1, CV_64FC1);
        Mat translation = new Mat(3, 1, CV_64FC1);

        if (intrinsicsMat == null) {
            intrinsicsMat = new Mat(3, 3, CV_32FC1);
            FloatIndexer intrinsicIdx = intrinsicsMat.createIndexer(true);

            // init to 0
            int k = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    intrinsicIdx.put(k++, 0);
                }
            }

            // set the values
            intrinsicIdx.put(0, 0, intrinsics.m00);
            intrinsicIdx.put(1, 1, intrinsics.m11);
            intrinsicIdx.put(0, 2, intrinsics.m02);
            intrinsicIdx.put(1, 2, intrinsics.m12);
            intrinsicIdx.put(2, 2, 1);
        }

        FloatIndexer opIdx = op.createIndexer();
        FloatIndexer ipIdx = ip.createIndexer();

        // Fill the object and image matrices.
        for (int i = 0; i < objectPoints.length; i++) {
            opIdx.put(i, 0, objectPoints[i].x);
            opIdx.put(i, 1, objectPoints[i].y);
            opIdx.put(i, 2, objectPoints[i].z);

            ipIdx.put(i, 0, imagePoints[i].x);
            ipIdx.put(i, 1, imagePoints[i].y);
        }

        //  cv::SOLVEPNP_ITERATIVE = 0, 
        //  cv::SOLVEPNP_EPNP = 1, 
//  cv::SOLVEPNP_P3P = 2, 
//  cv::SOLVEPNP_DLS = 3, 
//  cv::SOLVEPNP_UPNP = 4 
// Convert all to Mat, instead of CvMat
        boolean solved = opencv_calib3d.solvePnP(op,
                ip,
                new Mat(intrinsicsMat), new Mat(),
                rotation, translation,
                false, opencv_calib3d.SOLVEPNP_ITERATIVE);

        Mat rotMat = new Mat(3, 3, CV_64FC1);
        Rodrigues(rotation, rotMat);

        double[] rotationIndex = (double[]) rotMat.createIndexer(false).array();
        double[] translationIndex = (double[]) translation.createIndexer(false).array();

//        float RTMat[] = {
//            (float) rotationIndex[0], (float) rotationIndex[1], (float) rotationIndex[2], (float) translationIndex[0],
//            (float) rotationIndex[3], (float) rotationIndex[4], (float) rotationIndex[5], (float) translationIndex[1],
//            (float) rotationIndex[6], (float) rotationIndex[7], (float) rotationIndex[8], (float) translationIndex[2],
//            0, 0, 0, 1f};

        PMatrix3D mat = new PMatrix3D((float) rotationIndex[0], (float) rotationIndex[1], (float) rotationIndex[2], (float) translationIndex[0],
                (float) rotationIndex[3], (float) rotationIndex[4], (float) rotationIndex[5], (float) translationIndex[1],
                (float) rotationIndex[6], (float) rotationIndex[7], (float) rotationIndex[8], (float) translationIndex[2],
                0, 0, 0, 1f);
//        mat.set(RTMat);

        return mat;
    }

    // TODO: update this estimationRansac !
//    public PMatrix3D estimateOrientationRansac(PVector[] objectPoints,
//            PVector[] imagePoints) {
//
//        assert (objectPoints.length == imagePoints.length);
//
//        CvMat op = CvMat.create(objectPoints.length, 3);
//        CvMat ip = CvMat.create(imagePoints.length, 2);
////        Mat op = new Mat(objectPoints.length, 3, CV_32FC1);
////        Mat ip = new Mat(imagePoints.length, 2, CV_32FC1);
//
//        Mat rotation = new Mat(3, 1, CV_32FC1);
//        Mat translation = new Mat(3, 1, CV_32FC1);
//
////        CvMat rotation = CvMat.create(3, 1);
////        CvMat translation = CvMat.create(3, 1);
//        // Create internal parameters matrix.
//        if (intrinsicsMat == null) {
//            intrinsicsMat = CvMat.create(3, 3);
//
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 3; j++) {
//                    intrinsicsMat.put(i, j, 0);
//                }
//            }
//
//            intrinsicsMat.put(0, 0, intrinsics.m00);
//            intrinsicsMat.put(1, 1, intrinsics.m11);
//            intrinsicsMat.put(0, 2, intrinsics.m02);
//            intrinsicsMat.put(1, 2, intrinsics.m12);
//            intrinsicsMat.put(2, 2, 1);
//        }
//
//        // Fill the object and image matrices.
//        for (int i = 0; i < objectPoints.length; i++) {
//            op.put(i, 0, objectPoints[i].x);
//            op.put(i, 1, objectPoints[i].y);
//            op.put(i, 2, objectPoints[i].z);
//
//            ip.put(i, 0, imagePoints[i].x);
//            ip.put(i, 1, imagePoints[i].y);
//        }
//
//        // TODO: remove the new ...
////        ITERATIVE=CV_ITERATIVE,
////        EPNP=CV_EPNP,
////        P3P=CV_P3P;
//        // Convert all to Mat, instead of CvMat
//        opencv_calib3d.solvePnPRansac(
//                new Mat(op),
//                new Mat(ip),
//                new Mat(intrinsicsMat),
//                new Mat(),
//                rotation,
//                translation,
//                false, // extrinsic guess
//                100, // iterationsCount
//                8.0f, // reprojError
//                100, // minInlinersCount
//                new Mat(), // outputArray
//                opencv_calib3d.ITERATIVE);
//
////        boolean solvePnP(@InputArray CvMat objectPoints,
////            @InputArray CvMat imagePoints, @InputArray CvMat cameraMatrix,
////            @InputArray CvMat distCoeffs,  @OutputArray CvMat rvec,
////            @OutputArray CvMat tvec, boolean useExtrinsicGuess
//        PMatrix3D mat = new PMatrix3D();
//
//        CvMat rotMat = CvMat.create(3, 3);
//        cvRodrigues2(rotation.asCvMat(), rotMat, null);
//
//        CvMat translationCv = translation.asCvMat();
//
//        float RTMat[] = {
//            (float) rotMat.get(0), (float) rotMat.get(1), (float) rotMat.get(2), (float) translationCv.get(0),
//            (float) rotMat.get(3), (float) rotMat.get(4), (float) rotMat.get(5), (float) translationCv.get(1),
//            (float) rotMat.get(6), (float) rotMat.get(7), (float) rotMat.get(8), (float) translationCv.get(2),
//            0, 0, 0, 1f};
//        mat.set(RTMat);
//        return mat;
//    }
    public void saveTo(PApplet applet, String filename) {
        ProjectiveDeviceCalibration calib = new ProjectiveDeviceCalibration();
        calib.setWidth(this.w);
        calib.setHeight(this.h);
        calib.setIntrinsics(intrinsics);
        if (this.hasExtrinsics()) {
            calib.setExtrinsics(extrinsics);
        }
        calib.saveTo(applet, filename);
    }

    public static ProjectiveDeviceP loadCameraDevice(PApplet parent, String filename) throws Exception {
        return loadCameraDevice(parent, filename, 0);
    }

    public static ProjectiveDeviceP loadCameraDevice(PApplet parent, String filename, int id) throws Exception {
        ProjectiveDeviceP p = new ProjectiveDeviceP();

        if (filename.endsWith(".yaml")) {
            CameraDevice[] camDev = CameraDevice.read(filename);
            if (camDev.length <= id) {
                throw new Exception("No camera device with the id " + id + " in the calibration file: " + filename);
            }
            CameraDevice cameraDevice = camDev[id];
            loadParameters(cameraDevice, p);
        }

        if (filename.endsWith((".xml"))) {
            ProjectiveDeviceCalibration calib = new ProjectiveDeviceCalibration();
            calib.loadFrom(parent, filename);
            loadParameters(calib, p);
        }

        return p;
    }


    @Deprecated
    public static ProjectiveDeviceP loadProjectiveDevice(String filename, int id) throws Exception {
        ProjectiveDeviceP p = new ProjectiveDeviceP();
        try {
            ProjectiveDevice[] camDev = ProjectiveDevice.read(filename);
            if (camDev.length <= id) {
                throw new Exception("No projective device with the id " + id + " in the calibration file: " + filename);
            }
            ProjectiveDevice projectiveDevice = camDev[id];
            p.device = projectiveDevice;
            loadParameters(projectiveDevice, p);
        } catch (Exception e) {
            throw new Exception("Error reading the calibration file : " + filename + " \n" + e);
        }
        return p;
    }

    private static void loadParameters(ProjectiveDevice dev, ProjectiveDeviceP p) {
        double[] camMat = dev.cameraMatrix.get();

        p.handleDistorsion = true;

        p.intrinsics = new PMatrix3D((float) camMat[0], (float) camMat[1], (float) camMat[2], 0,
                (float) camMat[3], (float) camMat[4], (float) camMat[5], 0,
                (float) camMat[6], (float) camMat[7], (float) camMat[8], 0,
                0, 0, 0, 1);

        p.w = dev.imageWidth;
        p.h = dev.imageHeight;

        p.updateFromIntrinsics();

        p.hasExtrinsics = dev.R != null && dev.T != null;

        if (p.hasExtrinsics()) {
            double[] projR = dev.R.get();
            double[] projT = dev.T.get();

            p.extrinsics = new PMatrix3D((float) projR[0], (float) projR[1], (float) projR[2], (float) projT[0],
                    (float) projR[3], (float) projR[4], (float) projR[5], (float) projT[1],
                    (float) projR[6], (float) projR[7], (float) projR[8], (float) projT[2],
                    0, 0, 0, 1);
        }
        p.device = dev;
    }

    private static void loadParameters(ProjectiveDeviceCalibration dev, ProjectiveDeviceP p) {
        // Not implemented yet
        p.handleDistorsion = false;
        p.intrinsics = dev.getIntrinsics();

        p.w = dev.getWidth();
        p.h = dev.getHeight();
        p.updateFromIntrinsics();

        if (p.hasExtrinsics()) {
            p.extrinsics = dev.getExtrinsics();
        }

        p.device = null;
    }

    public void setIntrinsics(PMatrix3D intrinsics) {
        this.intrinsics.set(intrinsics);
        updateFromIntrinsics();
    }

    public void updateFromIntrinsics() {
        fx = intrinsics.m00;
        fy = intrinsics.m11;
        ifx = 1f / intrinsics.m00;
        ify = 1f / intrinsics.m11;
        cx = intrinsics.m02;
        cy = intrinsics.m12;
    }

    public String toString() {
        return "intr " + intrinsics.toString() + (extrinsics != null ? " extr " + extrinsics.toString() : " ") + " "
                + " width " + w + " height " + h;
    }
}
