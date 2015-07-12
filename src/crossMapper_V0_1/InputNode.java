package crossMapper_V0_1;

//import java.util.Date;
import processing.core.PApplet;
import processing.core.PVector;
import processing.*;
import java.awt.*;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseListener;

public class InputNode extends Node {

    int myRed;
    int myGreen;
    int myBlue;
    Color myColor;
    int noOfControls = 4;
    float[] xs, ys; //used for positioning the controls about the node
    PVector[] vectors; //used to draw the polygon for the node
    PVector vector = new PVector(0, 0);

    boolean[] myConnects;
    int maxInputNs;
    int maxOutputNs;
    float rawInput = 0.0F;
    float normalisedInput = 0.0F;
    int myType = 0; //type is 0 for OSC node, 1 for Serial
    String typeName;
    boolean normalisation = true;
    boolean calibrating = false;
    float minInput = 0.0F, maxInput = 1023.0F; //Used for normalisation of input
    boolean moving = false;
    boolean isGhostNode = false;

    int maxNoOfGhosts = 3;
    int noOfGhosts = 0;
    InputNodeGhost[] myGhosts = new InputNodeGhost[maxNoOfGhosts];
    float ghostOffset = (float) nodeSize * 0.7F;

    String[] infoDialogs = {"IN #" + iD, "default"};
    int infoTextSize = 18;
    int dialogTextSize = 14;
    String[] controlDialogs = {"Delete", "Copy", "Calibrate", "Settings"};

    String nameSpace = new String();
    int inputNumber; //this is used to assign input controller numbers to particular input nodes
    int STROKE_WEIGHT = 3;
    

    InputNode(PApplet parent_, int iD_, float g, float f, int nodeSize_, int maxInputNs_, int maxOutputNs_) {
        super(parent_, iD_, g, f, nodeSize_);
        maxInputNs = maxInputNs_;
        maxOutputNs = maxOutputNs_;
        if (iD < 3) {
            myType = 1;

        } else {
            myType = 0;
        }
        //vectors = new PVector[noOfControls];
        getTypeName();
        //makePopUpMenu();
        initInputNumber();

    }

    private void initInputNumber() {
        // TODO Auto-generated method stub
        inputNumber = iD;

    }

    void setInputNumber() {
        // TODO Auto-generated method stub
        inputNumber = iD;

    }

    private void getTypeName() {
        // TODO Auto-generated method stub
        switch (myType) {
            case 0:
                infoDialogs[1] = "OSC";
                break;

            case 1:
                infoDialogs[1] = "SERIAL";
                break;

            default:
                break;
        }
    }

    void init() {
        connectInit();
        displayInit();
        assignColors();
        generateCoords();
    }

    void assignColors() {

        myRed = (int) 255 / (maxInputNs - 1) * iD;
        myGreen = (int) this.parent.random(0, 250);
        myBlue = 255 - myRed;
		//this.parent.println("ID: "+iD+" Red: "+ myRed+" Blue: "+ myBlue);

        //this.parent.println("Colur value: "+ colourValue);
        myColor = new Color(myRed, myGreen, myBlue);
        //myColor = new Color (255, 0, 0);
    }

    void connectInit() {
        myConnects = new boolean[maxOutputNs];
        for (int i = 0; i < maxOutputNs; i++) {
            myConnects[i] = false;
        }
    }

    void displayInit() {
    }

    void generateCoords() {
        xs = new float[noOfControls];
        ys = new float[noOfControls];
        float angle = 180.0F / (float) noOfControls; //Angle step
        float offset = parent.random(0.0F, 25.0F);
		//angle=angle+offset;
        //System.out.println("Generating Input Co-ords");
        for (int i = 0; i < noOfControls; i += 1) {
            xs[i] = x + offset - parent.cos(parent.radians(angle * i)) * nodeSize;
            ys[i] = y + offset - parent.sin(parent.radians(angle * i)) * nodeSize;
            //System.out.println("x"+i+" is: "+xs[i]+" y"+i+" is: "+ys[i]);
        }
        //makeVector();

    }

    void connectAdd(int connection) {
        myConnects[connection] = true;

    }

    void connectBreak(int connection) {
        myConnects[connection] = false;

    }

    void connectBreak() {
        //if no arg specified, delete all connects
        for (int i = 0; i < maxOutputNs; i += 1) {
            myConnects[i] = false;
            //CrossMapper_V0_1_Start.myMapperV3.outputNodes[i].connectBreak(iD);
        }
    }

    void inputUpdate(float inputValue_) {
        rawInput = inputValue_;
		//PApplet.println("Input Node " + iD + " Has received input: " + 
        //rawInput);

        if (normalisation) {
            //System.out.println("Normalisation is on.");
            switch (myType) {
                case 0:
                    //System.out.println("Sending value to normalisation method");
                    normalisedInput = normaliseOSC(rawInput);
                    break;

                case 1:
                    normalisedInput = normaliseSerial(rawInput);
                    break;
                default:
                    break;
            }
        }

        send(iD, normalisedInput);
    }

    void send(int senderId_, float sendValue_) {
        int senderId = senderId_;
        float sendValue = sendValue_;
        for (int i = 0; i < maxOutputNs; i++) {
            if (myConnects[i] == true) {
                CrossMapper_V0_1_Start.myMapperV3.outputNodes[i].inputUpdate(senderId, sendValue);
            }
        }
    }

    void display() {
        parent.fill(255);
        parent.text(normalisedInput, x , y + (nodeSize / 2) - dialogTextSize);
    }

    float normaliseOSC(float _valueIn) {
        float valueIn = _valueIn;
        float normalInput = (valueIn - minInput) / (maxInput - minInput); //Map the input to the calibrated range
        //System.out.println("Raw OSC Input: "+valueIn+" has been normalised to: "+normalInput);
        if (normalInput < 0.0) {
            normalInput = 0.0F;
        }

        if (normalInput > 1.0) {
            normalInput = 1.0F;
        }
        return normalInput;
    }

    float normaliseSerial(float _valueIn) {
        float valueIn = _valueIn;
        float normalInput = (valueIn - minInput) / (maxInput - minInput); //Map the input to the calibrated range
        //System.out.println("Raw Serial Input: "+valueIn+" has been normalised to: "+normalInput);
        if (normalInput <= 0.0F) {
            normalInput = 0.0F;
        }

        if (normalInput >= 1.0F) {
            normalInput = 1.0F;
        }
        return normalInput;
    }

    void calibrateMe() {
        calibrating = true;
        long startTime = System.currentTimeMillis();
        //System.out.println("Vary input in desired range for 5 seconds");
        System.out.println("***Calibrating***");
        float tempMinInput = rawInput;
        float tempMaxInput = rawInput;
        while (System.currentTimeMillis() - startTime < 5000) {
            if (tempMinInput > rawInput) {
                tempMinInput = rawInput;
            }
            if (tempMaxInput < rawInput) {
                tempMaxInput = rawInput;
            }
			//if((System.currentTimeMillis()-startTime)% 500<50){
            //	System.out.print("*");
            //}
        }
        if (tempMinInput != tempMaxInput) {
            minInput = tempMinInput;
            maxInput = tempMaxInput;
        }

        System.out.println("Calibration complete!");
        System.out.println("Range minimum set at:" + minInput);
        System.out.println("Range maximum set at:" + maxInput);
        calibrating = false;
    }

    void drawMe() {
        this.parent.fill(myColor.getRed(), myColor.getGreen(), myColor.getBlue());
        this.parent.stroke(255);
        this.parent.strokeWeight(STROKE_WEIGHT);

        this.parent.rect(this.x, this.y, (float) (nodeSize * 0.4), (float) (nodeSize * 0.4));
        if (showControls == true) {
            showControls();
        }
        showInfo();
    }

    boolean overMe() {
        float disX = x + crossMapper_V0_1.CrossMapper_V0_1.xShift - parent.mouseX;//X-Mapper_V3.MapperV3.xShift;
        float disY = y + crossMapper_V0_1.CrossMapper_V0_1.yShift - parent.mouseY;//-Mapper_V3.MapperV3.yShift;
        return PApplet.sqrt(PApplet.sq(disX) + PApplet.sq(disY)) < this.nodeSize / 2;
    }

    void overControl() {
        int control = 0;
        float[] xDistances = new float[noOfControls];
        float[] yDistances = new float[noOfControls];
        for (int i = 0; i < noOfControls; i += 1) {
            xDistances[i] = xs[i] + crossMapper_V0_1.CrossMapper_V0_1.xShift - parent.mouseX;//X-Mapper_V3.MapperV3.xShift;
            yDistances[i] = ys[i] + crossMapper_V0_1.CrossMapper_V0_1.yShift - parent.mouseY;//-Mapper_V3.MapperV3.yShift;
            if (PApplet.sqrt(PApplet.sq(xDistances[i]) + PApplet.sq(yDistances[i])) < this.nodeSize / 4) {
				//control = i+1;
                //System.out.println("Control #"+i+" click detected in overControl() method for Node #"+iD);
                chooseControl(i);
                break;
            } else {
				//control=0;
                //System.out.println("No Control clicked");

            }
        }

    }

    void showControls() {
        for (int i = 0; i < noOfControls; i += 1) {
            this.parent.strokeWeight(STROKE_WEIGHT);
            this.parent.line(x, (float) (y - (nodeSize * 0.4)), xs[i], ys[i]);
            this.parent.ellipse(xs[i], ys[i], (float) (nodeSize * 0.2), (float) (nodeSize * 0.2));
            this.parent.ellipse(xs[i], ys[i], 5, 5);
            this.parent.text(controlDialogs[i],
                    xs[i], ys[i] - (float) (nodeSize * 0.2));
        }
    }

    void showInfo() {
        this.parent.textSize(infoTextSize);
        this.parent.textAlign(3); //3 for CENTER, RIGHT is 39, LEFT is 37
        for (int i = 0; i < infoDialogs.length; i += 1) {
            parent.fill(255);
            this.parent.text(infoDialogs[i], this.x, this.y + (i * infoTextSize)- dialogTextSize);
        }
    }

    void moveMe(int x_, int y_) {
        //System.out.println("Moving node "+iD);
        if (moving) {
            this.x = x_;
            this.y = y_;
            generateCoords();
            for (int i = 0; i < maxOutputNs; i += 1) {
                if (myConnects[i]) {
                    crossMapper_V0_1.CrossMapper_V0_1.outputNodes[i].connectUpdate(iD, x, y);
                }
            }
        }
    }

    void chooseControl(int _choice) {

        int choice = _choice;

        switch (choice) {
            case 0:
                deleteMe();
                break;

            case 1:
                addGhost();
                break;

            case 2:
                calibrateMe();
                break;

            case 3:
                updateSettings();
                break;

            default:
                break;
        }

    }

    private void updateSettings() {
        // TODO Auto-generated method stub
        setNameSpace();
    }

    private void addGhost() {
        // TODO Auto-generated method stub

        if (noOfGhosts < maxNoOfGhosts) {
            //System.out.println("Adding Ghost Node");
            myGhosts[noOfGhosts] = new InputNodeGhost(parent, iD, x + ghostOffset, y + ghostOffset, nodeSize,
                    maxInputNs, maxOutputNs);
            myGhosts[noOfGhosts].isGhostNode = true;
            noOfGhosts += 1;
        } else {
            //System.out.println("Can't add any more ghosts for this node!");

        }

    }

    private void deleteMe() {
        // TODO Auto-generated method stub

    }

    void setNameSpace() {
        switch (myType) {
            case 0:
                nameSpace = "crossmapper/osc/" + iD + "/";
                //System.out.println("Node name format is: "+nameSpace);
                break;

            case 1:
                nameSpace = "crossmapper/serial/" + iD + "/";
                //System.out.println("Node name format is: "+nameSpace);
                break;
        }

    }

    void loadFirmata() {
		//if(useFirmata);
        //
    }

}
