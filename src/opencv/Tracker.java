package opencv;

import app.Configuration;
import app.Controller;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize2D32f;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class Tracker {
	private Controller 		controller;
	private Configuration 	configuration;
	private int 			xPx, yPx, thPx;
	
	public Tracker(Controller controller) {
		this.controller 	= controller;
		this.configuration 	= this.controller.getConfiguration();
		
		this.yPx 	= this.configuration.getYres();
		this.xPx 	= this.yPx * 4 / 3;
		this.thPx	= (int) Math.round(this.xPx / this.configuration.getCamFOVX() * (this.configuration.getCamFOVX() - this.configuration.getMoveThreshold()) / 2);
		
		this.controller.getGui().printConsole("Auflösung (px): " + this.xPx + "x" + this.yPx);
		this.controller.getGui().printConsole("Threshold (px): " + this.thPx + " und " + (this.xPx - this.thPx));	
	}
	
    public void run() throws Exception {
        String classifierName = "src/resources/opencv/haarcascade_upperbody.xml";

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        CanvasFrame frame = new CanvasFrame("Kamera Feed");
        frame.setCanvasSize(this.xPx, this.yPx);
        frame.addKeyListener(this.controller.getGui().getKeyboard());

        // TODO try different framegrabbers
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = new OpenCVFrameGrabber(this.configuration.getDeviceId());
        grabber.setImageHeight(this.yPx);
        grabber.setImageWidth(this.xPx);
        grabber.start();

        IplImage 	grabbedImage 	= grabber.grab();
        IplImage 	grayImage    	= null;
        IplImage 	smoothGray		= null;
        IplImage 	prevImage		= null;
        IplImage	diff			= null;

        CvMemStorage storage = CvMemStorage.create();

        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {
        	prevImage 	= smoothGray;
        	grayImage 	= IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);
        	cvCvtColor(grabbedImage, grayImage, CV_RGB2GRAY);
        	smoothGray 	= IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);
        	cvSmooth(grayImage, smoothGray, CV_GAUSSIAN, 9, 9, 0.1, 0.1);            

            if (diff == null) {
                diff = IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);
            }

            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq 	faces 	= cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            int 	total 	= faces.total();
            
            for (int i = 0; i < total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
               
    			double x = r.x(), y = r.y(), width = r.width(), height = r.height();
                
                double dia = Math.sqrt((width * width) + (height * height));
                double dist = this.log(0.45, dia - 42) + 6.6;
                
        		if (dist > 0) {
                    this.controller.getCamController().bodyFound(
                    	(x/ (double) this.xPx),
                    	(y/ (double) this.yPx),
                    	(width/ (double) this.xPx),
                    	(height/ (double) this.yPx),
                    	dist
                    );
                    
                    cvRectangle(
                    	grabbedImage,
                    	cvPoint(r.x(), r.y()),
                    	cvPoint(r.x() + r.width(), r.y() + r.height()),
                    	CvScalar.RED, 1, CV_AA, 0
                    );
        		}
            }
            
            if (prevImage != null) {
                // perform ABS difference
                cvAbsDiff(smoothGray, prevImage, diff);
                // do some threshold for wipe away useless details
                cvThreshold(diff, diff, 70, 255, CV_THRESH_BINARY);

                // recognize contours
                CvSeq contour = new CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                
                while (contour != null && !contour.isNull()) {
                    if (contour.elem_size() > 0) {
                        CvBox2D box = cvMinAreaRect2(contour, storage);
                        // test intersection
                        if (box != null) {
                            CvPoint2D32f 	center 	= box.center();
                            CvSize2D32f 	size 	= box.size();

                            cvRectangle(
                            	grabbedImage,
                            	cvPoint((int) Math.round(center.x()-(size.width()/2)), (int) Math.round(center.y()-(size.height()/2))),
                            	cvPoint((int) Math.round(center.x()+(size.width()/2)), (int) Math.round(center.y()+(size.height()/2))),
                            	CvScalar.WHITE, 1, CV_AA, 0
                            );
	                    }
	                    contour = contour.h_next();
                    }
                }                
            }
            
            cvLine(grabbedImage, cvPoint(this.thPx, 0), cvPoint(this.thPx, this.yPx), CvScalar.GREEN, 1, CV_AA, 0);
            cvLine(grabbedImage, cvPoint(this.xPx - this.thPx, 0), cvPoint(this.xPx - this.thPx, this.yPx), CvScalar.GREEN, 1, CV_AA, 0);
            
            frame.showImage(grabbedImage);
            cvClearMemStorage(storage);
        }
        grabber.stop();
        frame.dispose();
    }
    
    private double log(double base, double x) {
    	return Math.log(x) / Math.log(base);
    }
}