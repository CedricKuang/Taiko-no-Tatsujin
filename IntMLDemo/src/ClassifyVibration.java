import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import processing.core.PApplet;
import processing.sound.AudioIn;
import processing.sound.FFT;
import processing.sound.Sound;
import processing.sound.Waveform;

class SayHello extends TimerTask {
    public void run() {
       System.out.println("Palm!"); 
    }
}

/* A class with the main function and Processing visualizations to run the demo */

public class ClassifyVibration extends PApplet {

	FFT fft;
	AudioIn in;
	Waveform waveform;
	int bands = 512;
	int nsamples = 1024;
	float[] spectrum = new float[bands];
	float[] fftFeatures = new float[bands];
	String[] classNames = {"quiet", "palm", "tip"};
	int classIndex = 0;
	int dataCount = 0;

	MLClassifier classifier;
	
	Map<String, List<DataInstance>> trainingData = new HashMap<>();
	{for (String className : classNames){
		trainingData.put(className, new ArrayList<DataInstance>());
	}}
	
	DataInstance captureInstance (String label){
		DataInstance res = new DataInstance();
		res.label = label;
		res.measurements = fftFeatures.clone();
		return res;
	}
	
	public static void main(String[] args) {
		Thread thread = new Thread()
        {

            public void run()
            {
                while (true){
                    System.out.println("Palm!");
                    try
                    {
                        Thread.sleep(10000); // 10 second
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
		PApplet.main("ClassifyVibration");
	}
	
	public void settings() {
		size(512, 400);
	}

	public void setup() {
		
		/* list all audio devices */
		Sound.list();
		Sound s = new Sound(this);
		  
		/* select microphone device */
		s.inputDevice(2);
		    
		/* create an Input stream which is routed into the FFT analyzer */
		fft = new FFT(this, bands);
		in = new AudioIn(this, 0);
		waveform = new Waveform(this, nsamples);
		waveform.input(in);
		
		/* start the Audio Input */
		in.start();
		  
		/* patch the AudioIn */
		fft.input(in);
	}

	public void draw() {
		background(0);
		fill(0);
		stroke(255);
		
		waveform.analyze();

		beginShape();
		  
		for(int i = 0; i < nsamples; i++)
		{
			vertex(
					map(i, 0, nsamples, 0, width),
					map(waveform.data[i], -1, 1, 0, height)
					);
		}
		
		endShape();

		fft.analyze(spectrum);
		
		String guessedLabel = "";
		

		for(int i = 0; i < bands; i++){

			/* the result of the FFT is normalized */
			/* draw the line for frequency band i scaling it up by 40 to get more amplitude */
			line( i, height, i, height - spectrum[i]*height*40);
			fftFeatures[i] = spectrum[i];
		} 

		fill(255);
		textSize(30);
		if(classifier != null) {
			guessedLabel = classifier.classify(captureInstance(null));
			text("classified as: " + guessedLabel, 20, 30);
		}else {
			text(classNames[classIndex], 20, 30);
			dataCount = trainingData.get(classNames[classIndex]).size();
			text("Data collected: " + dataCount, 20, 60);
		}
	}
	
	public void keyPressed() {
		if (key == '.') {
			classIndex = (classIndex + 1) % classNames.length;
		}
		
		else if (key == 't') {
			if(classifier == null) {
				println("Start training ...");
				classifier = new MLClassifier();
				classifier.train(trainingData);
			}else {
				classifier = null;
			}
		}
		
		else if (key == 's') {
			// Yang: add code to save your trained model for later use
			try (FileOutputStream fos = new FileOutputStream("object.dat");
				     ObjectOutputStream oos = new ObjectOutputStream(fos)) {

				    // create a new user object
				    oos.writeObject(classifier);

				} catch (IOException ex) {
				    ex.printStackTrace();
				}
		}
		
		else if (key == 'l') {
			// Yang: add code to load your previously trained model
			try {
				 
	            FileInputStream fileIn = new FileInputStream("object.dat");
	            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	 
	            Object obj = objectIn.readObject();
	 
	            System.out.println("The Object has been read from the file");
	            objectIn.close();
	            classifier = (MLClassifier) obj;
	 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		}
			
		else {
			trainingData.get(classNames[classIndex]).add(captureInstance(classNames[classIndex]));
		}
	}

}