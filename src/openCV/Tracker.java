package openCV;

import application.Configuration;
import application.Controller;

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
	private int 			xPx, yPx, thXPx, thYPx;
	private IplImage		grabbedImage = null;
	
	public Tracker(Controller controller) {
		this.controller 	= controller;
		this.configuration 	= this.controller.getConfiguration();
		
		this.yPx 	= this.configuration.getYres();
		this.xPx 	= this.yPx * 4 / 3;
		this.thXPx	= (int) Math.round(this.xPx / this.configuration.getCamFOVX() * (this.configuration.getCamFOVX() - this.configuration.getMoveThresholdX()) / 2);
		this.thYPx	= (int) Math.round(this.yPx / this.configuration.getCamFOVY() * (this.configuration.getCamFOVY() - this.configuration.getMoveThresholdY()) / 2);
		
		this.controller.getGui().printConsole("Auflösung (px): " + this.xPx + "x" + this.yPx, 2);
		this.controller.getGui().printConsole("SchwellwerteX (px): " + this.thXPx + " und " + (this.xPx - this.thXPx) + " (" + this.xPx + ")", 2);
		this.controller.getGui().printConsole("SchwellwerteY (px): " + this.thYPx + " und " + (this.yPx - this.thYPx) + " (" + this.yPx + ")", 2);
	}
	
    public void run() throws Exception {
        String classifierName = "src/resources/openCV/haarcascade_upperbody.xml";

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
        	// TODO Exception
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

        this.grabbedImage			= grabber.grab();
        IplImage 	grayImage    	= null;
        IplImage 	smoothGray		= null;
        IplImage 	prevImage		= null;
        IplImage	diff			= null;

        CvMemStorage storage = CvMemStorage.create();

        while (frame.isVisible() && (this.grabbedImage = grabber.grab()) != null) {
        	prevImage 	= smoothGray;
        	grayImage 	= IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);
        	cvCvtColor(this.grabbedImage, grayImage, CV_RGB2GRAY);
        	smoothGray 	= IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);
        	cvSmooth(grayImage, smoothGray, CV_GAUSSIAN, 9, 9, 0.1, 0.1);            

            if (diff == null) diff = IplImage.create(this.xPx, this.yPx, IPL_DEPTH_8U, 1);

            cvCvtColor(this.grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq bodies = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            
            for (int i = 0; i < bodies.total(); i++) {
                CvRect rect 	= new CvRect(cvGetSeqElem(bodies, i));
                
                double diagonal = Math.sqrt((Math.pow((double) rect.width(), 2)) + (Math.pow((double) rect.height(), 2)));
                double distance	= this.log(0.45, diagonal - 42) + 6.6;
                
        		if (distance > 0) {
                    this.controller.getCamController().bodyDetected(
                    	((double) rect.x() 		/ (double) this.xPx),
                    	((double) rect.y() 		/ (double) this.yPx),
                    	((double) rect.width()	/ (double) this.xPx),
                    	((double) rect.height()	/ (double) this.yPx),
                    	distance
                    );
                    
                    this.drawRectangle(
                    	cvPoint(rect.x(), rect.y()),
                    	cvPoint(rect.x() + rect.width(), rect.y() + rect.height()),
                    	CvScalar.RED
                    );
        		}
            }
            
            if (prevImage != null) {
                cvAbsDiff(smoothGray, prevImage, diff);
                cvThreshold(diff, diff, 70, 255, CV_THRESH_BINARY);

                CvSeq contour = new CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                
                while (contour != null && !contour.isNull()) {
                    if (contour.elem_size() > 0) {
                        CvBox2D box = cvMinAreaRect2(contour, storage);
                        
                        if (box != null) {
                            CvPoint2D32f 	center 	= box.center();
                            CvSize2D32f 	size 	= box.size();

                            this.controller.getCamController().motionDetected(
                            	((double) center.x() / (double) this.xPx),
                            	((double) center.y() / (double) this.yPx),
                            	size.width() * size.height()
                            );
                            
                            this.drawRectangle(
                            	cvPoint(Math.round(center.x()-(size.width()/2)), Math.round(center.y()-(size.height()/2))),
                            	cvPoint(Math.round(center.x()+(size.width()/2)), Math.round(center.y()+(size.height()/2))),
                            	CvScalar.WHITE
                            );
	                    }
	                    contour = contour.h_next();
                    }
                }                
            }
            
            this.drawRectangle(
            	cvPoint(this.thXPx, this.thYPx),
            	cvPoint(this.xPx - this.thXPx, this.yPx - this.thYPx),
            	CvScalar.GREEN
            );

            frame.showImage(this.grabbedImage);
            cvClearMemStorage(storage);
        }
        grabber.stop();
        frame.dispose();
    }
    
    private void drawRectangle(CvPoint p1, CvPoint p2, CvScalar color) {
        cvRectangle(this.grabbedImage, p1, p2, color, 1, CV_AA, 0);
    }
    
    private double log(double base, double x) {
    	return Math.log(x) / Math.log(base);
    }
}