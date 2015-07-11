package crossMapper_V0_1;
/*
 * This is the Papplet that sits in the Mapper GUI Frame
 * It contains the methods that would be in the equivalent Processing Sketch, setup() and draw().
 * 
 * 
 * 
 * 
 * 13 July 2011: Adding complex weighting controller in output node.
 * 12 July 2011: Looking at designing of output nodes to allow multidimensional weighting.
 * 12 July 2011: Adding ability to accept int data input in OSC handling in MapperV3.
 * 11 July 2011: After Lunch- Adding calibration and normalisation functions in Input Nodes. Need to test.
 * tried to add popup menu for nodes in MapperGUI to start calibration- not working. Adding simpler method 
 * of right clicking node to start calibration
 * 11 July 2011: Before Lunch- Adding all Serial Settings Menu functions.
 * 09 July 2011: Adding Serial comms functions. >> Input working. 
 */

import netP5.NetAddress;
//import oscP5.OscArgument;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PFont;
import processing.serial.*;     // import the Processing serial library
//import cc.arduino.*;
//import processing.opengl.*;

public class CrossMapper_V0_1 extends PApplet {

    boolean useFirmata = false;
    //Main GUI Applet variables
    boolean performanceMode = false; //sets the mode of the GUI
    static int noOfInputNs = 3;
    static int maxInputNs = 5;
    static int noOfOutputNs = 1;
    static int maxOutputNs = 5;
    int totalNodes = noOfInputNs + noOfOutputNs;
    public static InputNode[] inputNodes;
    public static OutputNode[] outputNodes;
    boolean lineStarted;
    boolean lineDisconnect = false;
    int startNode;
    int disconnectNode = 0;
    int nodeSize;
    int yPad = 20;
    int xPad = 100; //use to add borders for input and output value displays
    static int xShift, yShift; //used to translate in the draw sketch
    int menuHeight = 10;
    PFont font;
    static int textSiz = 12;

    int[] nodeControlClicked = {0, 0}; //this holds the no of the node and the no of control clicked
    boolean isInputNodeMoving, isOutputNodeMoving = false;
    int nodeMoving = 0;
    boolean advancedMode = false;

    int[] inputNumbers = new int[maxInputNs];
    int serialTimer = 2000; //This is a delay after serial init to avoid trying to parse garbage
    long startTime;
    //Open Sound Control variables//////////////////////////
    OscP5 myOscP5;
    NetAddress localAddress, remoteAddress;
    int listenPort, sendPort;
    String localAddrString, remoteAddrString;

    //Serial variables///////////////////////////////////////	
    Serial port;
    String comPort, delimiter;
    int baudRate;
    boolean serialEnabled = false;
	//boolean serialActive = false;

    //Constructor...
    CrossMapper_V0_1() {
        if (noOfInputNs > maxInputNs) {
            noOfInputNs = maxInputNs;
        }
        if (noOfOutputNs > maxOutputNs) {
            noOfOutputNs = maxOutputNs;
        }
        inputNodes = new InputNode[maxInputNs];
        outputNodes = new OutputNode[maxOutputNs];
    }

    public void setup() {
        size(800, 600);//, OPENGL);		
        frameRate(25.0F);
        background(255);
        fill(150);
        strokeWeight(6.0F);
        stroke(100);
        smooth();
        ellipseMode(CENTER);
        rectMode(RADIUS);
        this.font = loadFont("C:\\OneDrive\\NetBeansProjects\\CrossMapperGoldsmiths\\CrossMapper_Goldsmiths_01\\src\\crossMapper_V0_1\\Batang-12.vlw");
        textFont(this.font);
        textSize(textSiz);

        //Initalise OSC with default values
        listenPort = 7400;
        sendPort = 7401;
        localAddrString = getLocalAddress();
        remoteAddrString = "127.0.0.1";
        oscInitialise();
        addrInitialise();

        //Initialse Serial with defaults
        baudRate = 115200;
        comPort = "N/A";
        delimiter = ",";
        if (serialEnabled) {
            serialInitialise();
        }

        nodeSize = ((height) / (maxInputNs + 1));//spacing will be done in the drawing method of the node
        xShift = (int) (width * 0.25);
        yShift = (int) (height * 0.3);
        //println("Each Node has a size of "+nodeSize);
        for (int i = 0; i < maxInputNs; i++) {//InputNode(PApplet parent_, int iD_, int x_, int y_, int nodeSize_, int maxInputNs_, int maxOutputNs_)
            inputNodes[i] = new InputNode(this, i, 0, i * (nodeSize), nodeSize, maxInputNs, maxOutputNs);
            inputNodes[i].init();
        }

        for (int i = 0; i < maxOutputNs; i++) {
            outputNodes[i] = new OutputNode(this, i, width / 2, (i * nodeSize), nodeSize, maxInputNs, maxOutputNs);
            outputNodes[i].init();
        }

        //Initalise some defaults
        getInputNumbers();

    }

    public void draw() {

        background(255);

        translate(xShift, yShift);
        for (int i = 0; i < noOfInputNs; i++) {
            for (int j = 0; j < noOfOutputNs; j++) {
                if (inputNodes[i].myConnects[j] == true) {
                    strokeWeight(5);
                    stroke(inputNodes[i].myColor.getRed(), inputNodes[i].myColor.getGreen(), inputNodes[i].myColor.getBlue());
                    line(inputNodes[i].x, inputNodes[i].y, outputNodes[j].x, outputNodes[j].y);
                }
            }
        }
        for (int i = 0; i < noOfInputNs; i++) {
            inputNodes[i].drawMe();
            inputNodes[i].display();

        }
        for (int i = 0; i < noOfOutputNs; i++) {
            outputNodes[i].drawMe();
            outputNodes[i].display();
        }
        //popMatrix();
        if (lineStarted) {
            stroke(255.0F, 0.0F, 0.0F, 150.0F);
            line(inputNodes[startNode].x, inputNodes[startNode].y, mouseX - xShift, mouseY - yShift);
        }
        if (lineDisconnect) {
            stroke(255.0F, 0.0F, 0.0F, 150.0F);
            noFill();
            ellipse(outputNodes[disconnectNode].x, outputNodes[disconnectNode].y, nodeSize, nodeSize);

            line(outputNodes[disconnectNode].x, outputNodes[disconnectNode].y, mouseX - xShift, mouseY - yShift);
        }
        //stroke(0,0,255);
        //strokeWeight(5);
        //line(0,0,0,height);//y-axis
        //line(0,0,width,0); //x-axis

    }

    //Open Sound Control//////////////////////////////////////////////////////////////
    //Methods to change OSC addresses and port based on OSCSettings menu fields
    public void oscListenPortChange(int listenPort_) {
        listenPort = listenPort_;
        //println("Changing OSC listening port to "+listenPort);
        oscInitialise();
    }

    public void oscSendPortChange(int sendPort_) {
        sendPort = sendPort_;
        //println("Changing OSC sending port to "+sendPort);
        addrInitialise();

    }

    public void oscLocalAddrChange(String localAddrString_) {
        localAddrString = localAddrString_;
        //println("Changing OSC local address to "+localAddrString);
        // No need to re-initalise OSC as listen port is not being changed.
    }

    public void oscRemoteAddrChange(String remoteAddrString_) {
        remoteAddrString = remoteAddrString_;
        //println("Changing OSC remote address to "+remoteAddrString);
        addrInitialise();
    }

    public void oscInitialise() {
        /* This must be called at setup and whenever a port number is changed.*/
        myOscP5 = new OscP5(this, listenPort);

    }

    public void addrInitialise() {
        /* This must be called at setup and whenever a NetAddress is changed.*/
        //localAddress = new NetAddress(localAddrString, listenPort); // not required
        remoteAddress = new NetAddress(remoteAddrString, sendPort);
    }

    String getLocalAddress() {
        try {
            java.net.InetAddress localMachine
                    = java.net.InetAddress.getLocalHost();
            //System.out.println ("Hostname of local machine: " +
            //localMachine.getHostName());
            //System.out.println ("Address of local machine: " +
            //localMachine.getHostAddress());
            return localMachine.getHostAddress();
        } catch (java.net.UnknownHostException uhe) {
            //handle exception
            return "Local IP n/a";
        }

    }

    void oscEvent(OscMessage theOscMessage) {
        int controllerInt = 0, dataInt = 0;
        float dataFloat = 0.0F;

        //print("Input-Output Mapper received an osc message.");
        //print(" addrpattern: " + theOscMessage.addrPattern());
        //println(" typetag: " + theOscMessage.typetag());
        if (theOscMessage.checkTypetag("if")) {
            controllerInt = theOscMessage.get(0).intValue();
            dataFloat = theOscMessage.get(1).floatValue();
            //println("Controlller: " + controllerInt + "\t | Value: " + dataFloat);
            this.inputNodes[controllerInt].inputUpdate(dataFloat);

        } else if (theOscMessage.checkTypetag("ii")) {
            controllerInt = theOscMessage.get(0).intValue();
            dataInt = theOscMessage.get(1).intValue();
            //println("Slider: " + controllerInt + "\t | Value: " + dataInt);
            dataFloat = (float) dataInt;
            this.inputNodes[controllerInt].inputUpdate(dataFloat); //inputUpdate accepts floats only for now

        }

    }

    void oscSend(int nodeId_, float output_) {
        //Gets messages from Output Nodes and formats as OSC before sending out
        int nodeId = nodeId_;
        float output = output_;
        OscMessage myMessage = new OscMessage("/crossmapper");
        myMessage.add(nodeId); /* add an int to the osc message */

        //myMessage.add("/");

        myMessage.add(output); /* add a float to the osc message */
        /* send the message */

        myOscP5.send(myMessage, remoteAddress);
    }

    //public void mouseClicked(){
    //	println("************");
    //	println("Mouse clicked!");
    //	println("************");
    //}
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Serial handling
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // The serialEvent method runs when the buffer reaches the
    // value set in the bufferUntil() method in setup() above
    public void serialInitialise() {
        //Call this when at setup and whenever settings are changed

        println("Initialising Serial Communications at " + baudRate + " Baud");
        startTime = System.currentTimeMillis();
        try {
            comPort = Serial.list()[1];
            println("*************************************");
            println("Avaiable Serial Com Port is: " + comPort);
            println("*************************************");
            port = new Serial(this, comPort, baudRate);
            //serialActive = true;
            // read bytes into a buffer until you get a linefeed (ASCII 10)
            //This character will signify the end of a serial event.
            port.bufferUntil('\n');
        } catch (Exception e) {
            println("There are no active COM ports available");
            serialEnabled = false;
            SerialSettingsMenu.serialNotAvailable();
            //comPort = "N/A";

        }
    }

    public void serialEvent(Serial port) {
        System.out.println("Serial Event Received");
        // We specified a "\n" in setup as terminating the serial event.
        //So here we read in the whole string until the "\n"
        String stringIn = port.readStringUntil('\n');
		//System.out.println("Serial Event");
        //Arduino output format: ***Pin number (int),value (int)\n ***

        //We need to be sure we don't get a Null Pointer exception here
        if (stringIn != null) {
            //trim cuts out any white space characters so we're left with the data and delimiter
            stringIn = trim(stringIn);
            // split the string at the commas and convert the sections into integers:
            String[] stringsIn = split(stringIn, ',');
            System.out.println("Serial received: " + stringIn);
            int[] sensors = new int[stringsIn.length];

            for (int i = 0; i < sensors.length; i += 1) {
                if ((System.currentTimeMillis() - startTime) > serialTimer) {
                    sensors[i] = Integer.parseInt(stringsIn[i]); //for some reason Processing's int() won't work here.
                    System.out.print(sensors[i] + "\t");
                }

            }
            System.out.println();
            if ((System.currentTimeMillis() - startTime) > serialTimer) {
                System.out.print("Serial Timer");
                if (sensors.length == 2) {
                    serialControlRoute(sensors);
                }
            }

        }
    }

    

    public void serialControlRoute(int[] sensorBuffer_) {

        int[] sensorBuffer = sensorBuffer_;
        this.inputNodes[sensorBuffer[0]].inputUpdate((float)sensorBuffer[1]);
//        for (int i = 0; i < maxInputNs; i += 1) {
//            //Convert input to float if needs be (inputUpdate only accepts floats)
//            //int controllerInt = sensors[0];
//            float dataFloat;
//            if (sensorBuffer[i] == sensorBuffer[i]) {
//                //System.out.println("Serial input data is in integer format.");
//                //dataFloat = (float) sensorBuffer[i];
//            } else {
//                //System.out.println("Serial input data is in float format.");
//                //dataFloat = sensorBuffer[i];
//            }
//            if (inputNumbers[i] == i && inputNodes[i].myType == 1) {
//                //this.inputNodes[i].inputUpdate(dataFloat);
//            }
//        }
    }

    public void getInputNumbers() {
        for (int i = 0; i < maxInputNs; i += 1) {
            inputNumbers[i] = inputNodes[i].inputNumber;

        }

    }

    public void serialStop() {
        //comPort = "N/A";
        port.clear();
        port.stop();
    }

    public void serialBaudChanged(int iBaud) {
        baudRate = iBaud;

    }

    public void serialPortChanged(String iPort) {
        comPort = iPort;
    }

    public void serialDelimChanged() {

    }

    /////Mouse Handler//////////////////////////////////////////////////////////////////////////
    public void mousePressed() {
        //Detect if an input node has been double clicked. Used to open controls for the node
        if (mouseEvent.getClickCount() == 2) {
            //println("<double click>");
            for (int i = 0; i < noOfInputNs; i++) {
                if (inputNodes[i].overMe()) {
                    inputNodes[i].showControls = !inputNodes[i].showControls;
                    //println("Input node " +i+" 'show controls' value is "+inputNodes[i].showControls);
                    break;
                }
            }
            for (int i = 0; i < noOfOutputNs; i++) {
                if (outputNodes[i].overMe()) {
                    outputNodes[i].showControls = !outputNodes[i].showControls;
                    //println("Output node " +i+" 'show controls' value is "+outputNodes[i].showControls);
                    break;
                }
            }

        } //Detect a single click on a node.
        else {
            //Shift + left click starts a connection...
            if (mouseButton == LEFT && keyPressed && key == CODED && keyCode == SHIFT) {
                if (!lineStarted && noOfOutputNs > 0) {
                    for (int i = 0; i < noOfInputNs; i++) {
                        if (inputNodes[i].overMe()) {
                            lineStarted = true;
                            startNode = i;

                            //println("Connection start at input node " + inputNodes[i].iD);
                        }
                    }
                }
                if (!lineDisconnect && noOfInputNs > 0) {
                    for (int j = 0; j < noOfOutputNs; j++) {
                        if (outputNodes[j].overMe()) {
                            if (outputNodes[j].myActiveConnects > 0) {
                                lineDisconnect = true;
                                disconnectNode = j;
                                //println("Disconnecting from Output Node " + outputNodes[j].iD);
                            }
                        }
                    }
                }

            } //Left click moves the node...
            else if (mouseButton == LEFT && !keyPressed) {
                //println("Moving a node");
                for (int i = 0; i < noOfInputNs; i++) {
                    if (inputNodes[i].overMe()) {
                        inputNodes[i].moving = true;
                        isInputNodeMoving = true;
                        nodeMoving = i;
                        break;
                    }
                }
                for (int i = 0; i < noOfOutputNs; i++) {
                    if (outputNodes[i].overMe()) {
                        outputNodes[i].moving = true;
                        isOutputNodeMoving = true;
                        nodeMoving = i;
                        break;
                    }
                }
            }

            //If a node has controls showing, this detects which control has been clicked
            for (int i = 0; i < noOfInputNs; i += 1) {
                if (inputNodes[i].showControls) {
                    inputNodes[i].overControl();
                    //break;
                }
            }
            for (int i = 0; i < noOfOutputNs; i += 1) {
                if (outputNodes[i].showControls) {
                    outputNodes[i].overControl();
                    //break;
                }
            }
        }

    }

    public void mouseDragged() {

        if (isInputNodeMoving) {
            inputNodes[nodeMoving].moveMe(mouseX - xShift, mouseY - yShift);
        }
        if (isOutputNodeMoving) {
            outputNodes[nodeMoving].moveMe(mouseX - xShift, mouseY - yShift);
        }
    }

    public void mouseReleased() {
        isInputNodeMoving = false;
        isOutputNodeMoving = false;

        if (mouseButton == LEFT) {
            if (lineStarted) {
                for (int i = 0; i < noOfOutputNs; i++) {
                    if (outputNodes[i].overMe()) {
                        //println("Connecting to output node " + outputNodes[i].iD);
                        //println("Connection set");
                        inputNodes[startNode].connectAdd(i);
                        outputNodes[i].connectUpdate(startNode, inputNodes[startNode].x, inputNodes[startNode].y);
                        lineStarted = false;
                        break;
                    }

                    lineStarted = false;
                    //println("Connection to output node " + i + " cancelled");
                }
            }

            if (lineDisconnect) {
                for (int j = 0; j < noOfInputNs; j++) {
                    if (inputNodes[j].overMe()) {
                        //println("Disconnecting from Input Node " + inputNodes[j].iD);
                        this.lineDisconnect = false;
                        //println("Disconnected");
                        inputNodes[j].connectBreak(disconnectNode);
                        outputNodes[disconnectNode].connectBreak(j);
                        break;
                    }

                    lineDisconnect = false;
                    //println("Disconnection from output node " + j + " cancelled");
                }
                //println("************");
            }

        }

        if (mouseButton == RIGHT) {

        }
    }

    //Node Management///////////////////////////////////////////////////////////////////////////////////
    static void addInput() {
        if (noOfInputNs < maxInputNs) {
            noOfInputNs += 1;
        }
        System.out.println("No of Input nodes is: " + noOfInputNs);

    }

    static void removeInput() {
        //This currently removes just the highest numbered input and
        //breaks connections to it

        inputNodes[noOfInputNs - 1].connectBreak();
        //If input removed need to disconnect output node from it
        for (int i = 0; i < maxOutputNs; i += 1) {
            if (outputNodes[i].possibleConnects[noOfInputNs - 1]) {
                outputNodes[i].connectBreak(noOfInputNs - 1);
                System.out.println("Breaking connection " + (noOfInputNs - 1)
                        + " in output node " + i);
            }
        }
        if (noOfInputNs > 0) {
            noOfInputNs -= 1;
        }

        System.out.println("No of Input nodes is: " + noOfInputNs);
    }

    static void addOutput() {
        if (noOfOutputNs < maxOutputNs) {
            noOfOutputNs += 1;
        }
        //System.out.println("No of Output nodes is: "+noOfOutputNs);
    }

    static void removeOutput() {
        if (noOfOutputNs > 0) {
            noOfOutputNs -= 1;
        }
        //System.out.println("No of Output nodes is: "+noOfOutputNs);
    }

}
