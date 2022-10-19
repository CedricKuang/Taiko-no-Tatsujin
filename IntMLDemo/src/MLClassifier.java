import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/* A wrapper class to use Weka's classifiers */

public class MLClassifier implements Serializable {
	private static final long serialVersionUID = 2093796448706806754L;
	
	FeatureCalc featureCalc = null;
    SMO classifier = null;
    Attribute classattr;
    Filter filter = new Normalize();

    public MLClassifier() {
    	
    }

    public void train(Map<String, List<DataInstance>> instances) {
    	
    	/* generate instances using the collected map of DataInstances */
    	
    	/* pass on labels */
    	featureCalc = new FeatureCalc(new ArrayList<>(instances.keySet()));
    	
    	/* pass on data */
    	List<DataInstance> trainingData = new ArrayList<>();
    	 
    	for(List<DataInstance> v : instances.values()) {
    		trainingData.addAll(v);
    	}
         
    	/* prepare the training dataset */
    	Instances dataset = featureCalc.calcFeatures(trainingData);
         
    	/* call build classifier */
    	classifier = new SMO();
         
         try {
        	 
        	 // Yang: RBFKernel requires tuning but might perform better than PolyKernel
        	 
        	 /* 
			classifier.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 "
			         + "-P 1.0E-12 -N 0 -V -1 -W 1 "
			         + "-K \"weka.classifiers.functions.supportVector.RBFKernel "
			         + "-C 0 -G 0.7\""));
			         */
			         
        	
        	classifier.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 "
			         + "-P 1.0E-12 -N 0 -V -1 -W 1 "
			         + "-K \"weka.classifiers.functions.supportVector.PolyKernel "
			         + "-C 0 -E 1.0\""));
        	 
//        	classifier.setOptions(weka.core.Utils.splitOptions("-depth 40 -O"));
			
			classifier.buildClassifier(dataset);
			this.classattr = dataset.classAttribute();
			
			System.out.println("Training done!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public String classify(DataInstance data) {  
    	float maxData = Integer.MIN_VALUE;
    	for (int i = 0; i < data.measurements.length; i++) {
    		maxData = maxData > data.measurements[i] ? maxData : data.measurements[i];
    	}
    	
    	if (maxData < 0.001 || maxData > 1) {
    		return "quiet";
    	}
    	
        if(classifier == null || classattr == null) {
            return "Unknown";
        }
        
        Instance instance = featureCalc.calcFeatures(data);
        
        try {
			double[] p = classifier.distributionForInstance(instance);
//			
//			System.out.println("Palm: " + p[0]);
//			System.out.println("Tip: " + p[1]);
//			System.out.println("Quiet: " + p[2]);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
            int result = (int) classifier.classifyInstance(instance);
            return classattr.value((int)result);
        } catch(Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
    
}