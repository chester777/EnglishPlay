package control_model;



import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chester
 */
class PronunPrac {
    URL url;
    Recognizer recognizer;
    Microphone microphone; 
    Result result;
    Scanner scan;
    static String resultText = null;
    
    public PronunPrac(int level) throws IOException, PropertyException, InstantiationException {
       
        System.out.println("Loading...");
        switch(level){
            case 1:
                url = PronunPrac.class.getResource("voice1.config.xml");
                break;
            case 2:
                url = PronunPrac.class.getResource("voice2.config.xml");
                break;
            case 3:
                url = PronunPrac.class.getResource("voice3.config.xml");
                break;
        }
        scan = new Scanner(System.in);
        ConfigurationManager cm;
        cm = new ConfigurationManager(url); 
        recognizer = (Recognizer) cm.lookup("recognizer");
        microphone = (Microphone) cm.lookup("microphone");

        recognizer.allocate();
        
        microphone.startRecording();
    }     
    
    public String recognizing(){
        result = recognizer.recognize();
        if(result != null){
            resultText = result.getBestFinalResultNoFiller();
            System.out.println("You said: \"" + resultText + "\"\n");
            result = null;
        }
        else {
            System.out.println("I can't hear what you said.\n");
        }
        return resultText;
    }
}