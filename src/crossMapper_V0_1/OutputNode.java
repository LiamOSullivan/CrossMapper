package crossMapper_V0_1;

import java.awt.Color;

import crossMapper_V0_1.CrossMapper_V0_1.*;
import processing.core.*;

public class OutputNode extends Node
{
	int myActiveConnects = 0;
	int maxInputNs;
	int maxOutputNs;
	boolean[] possibleConnects;
	float [] myConnectLengths;
	float [] normalisedLengths;
	//float totalLengths = 0.0F;
	float [] arcAngles;
	PVector [] arcVectors; //x,y points for the arcs displaying weighting coefficients.
	PVector [] myConnectVectors; //x,y positions of nodes I am connected to.
	float[] myWeights;
	float[] lastInputs;
	float myInput = 0.0F;
	float myOutput = 0.0F;

	boolean setWeightsMode =false;
	boolean showControls=false;
	boolean moving=false;
	//float controllerX, controllerY;

	int myRed; 
	int myGreen;
	int myBlue;
	Color myColor;
	int noOfControls =5;
	float [] controlsXs, controlsYs; //used for positioning the controls about the node
	PVector [] vectors ; //used to draw the polygon for the node
	PVector vector = new PVector(0,0);

	String [] infoDialogs = {"OUT #"+iD, "OSC"};
	int infoTextSize =18; int dialogTextSize =14;
	String [] controlDialogs = {"Delete", "Copy", "Calibrate", "Settings"};
	
	OutputNode(PApplet parent_, int iD_, int x_, int y_, int nodeSize_, int maxInputNs_, int maxOutputNs_) {
		super(parent_, iD_, x_, y_, nodeSize_);
		maxInputNs = maxInputNs_;
		maxOutputNs = maxOutputNs_;
		vectors = new PVector[noOfControls];

	}
	void init() {
		connectInit();
		generateCoords();
		weightsInit();
		lastInputsInit();
		displayInit();
		assignColors();

	}
	void assignColors(){

		myRed = (int) 255/(maxInputNs-1)*iD;
		myGreen = (int) this.parent.random(0,250);
		myBlue = 255 - myRed;
		//this.parent.println("ID: "+iD+" Red: "+ myRed+" Blue: "+ myBlue);

		//this.parent.println("Colur value: "+ colourValue);
		myColor = new Color (myRed, myGreen, myBlue);
		//myColor = new Color (255, 0, 0);
	}

	boolean overMe() {
		float disX = x+crossMapper_V0_1.CrossMapper_V0_1.xShift- parent.mouseX;
		float disY = y+crossMapper_V0_1.CrossMapper_V0_1.yShift - parent.mouseY;
		return PApplet.sqrt(PApplet.sq(disX) + PApplet.sq(disY)) < this.nodeSize / 2;
	}

	void connectInit() {
		possibleConnects = new boolean[maxInputNs];
		myConnectVectors = new PVector[maxInputNs];
		myConnectLengths = new float [maxInputNs];
		normalisedLengths = new float [maxInputNs];
		arcVectors = new PVector[maxInputNs];
		for (int i = 0; i < maxInputNs; i++){
			possibleConnects[i] = false;
			myConnectVectors[i] = new PVector(0.0F,0.0F);
			myConnectLengths[i] = 0.0F;
			normalisedLengths[i]= 0.0F;
			arcVectors[i] = new PVector(0.0F,0.0F);
		}
		//measureConnects();
	}



	void lastInputsInit()
	{
		lastInputs = new float[maxInputNs];
		for (int i = 0; i < maxInputNs; i++)
			lastInputs[i] = 0.0F;
	}

	void displayInit()
	{
	}

	void connectUpdate(int inputId_, int inputX_, int inputY_){
		int inputId = inputId_;
		int inputX = inputX_;
		int inputY = inputY_;
		myActiveConnects = 0;
		possibleConnects[inputId] = true;
		//PApplet.println("Output Node " + iD + " is connected to Input Node " + connection);
		//PApplet.print("Output Node " + iD + " | Connection array: ");
		for (int i = 0; i < maxInputNs; i++) {
			//PApplet.print(possibleConnects[i] + " | ");
			if (possibleConnects[i] ==true) {
				myActiveConnects += 1;
			}
		}
		myConnectVectors[inputId].x = (float)inputX;
		myConnectVectors[inputId].y = (float)inputY;
		//PApplet.println("Connect Update Method: connected to input node at ("
		//+myConnectVectors[inputId].x+", "+myConnectVectors[inputId].y+")");
		//PApplet.println();
		//generateCoords();
		measureConnects();

	}
	void connectBreak(int connection_) {
		int connection = connection_;
		myActiveConnects = 0;
		possibleConnects[connection] = false;
		//PApplet.println("Output Node " + iD + " is disconnected from Input Node " + connection);
		//PApplet.print("Output Node " + iD + " | Connection array: ");
		for (int i = 0; i < maxInputNs; i++) {
			//	PApplet.print(possibleConnects[i] + " | ");
			if (possibleConnects[i] ==true) {
				myActiveConnects += 1;
			}
		}
		//PApplet.println();
		//PApplet.println("Output Node " + iD + " has " + myActiveConnects + " active connections");
		//generateCoords();
		weightsUpdate(connection, 0.0F); //zero the weight of the broken connection
		measureConnects(); //recalculate the weights via connection measurement
		if(myActiveConnects==0){
			outputUpdate(0,0.0F);
		}
	}



	void inputUpdate(int senderId_, float inputValue_)
	{
		int senderId = senderId_;
		float inputValue = inputValue_;

		//PApplet.println("Output Node " + iD + " Has received input: " + inputValue + " from Input Node " + senderId);
		applyWeight(senderId, inputValue);
	}

	void applyWeight(int senderId_, float rawInput_) {
		int senderId = senderId_;
		float rawInput = rawInput_;
		PApplet.println("The raw input is " + rawInput);
		PApplet.println("Weighting coefficent is " + myWeights[senderId]);
		float weightedInput = rawInput * myWeights[senderId];
		PApplet.println("The weighted input is " + weightedInput);
		//PApplet.println("_____________________________");
		storeLast(senderId, weightedInput);
		outputUpdate(senderId, weightedInput);
	}

	void display()
	{
		//parent.fill(255.0F, 0.0F, 0.0F);
//		for (int i = 0; i < maxOutputNs; i++)
//		{
//			parent.text(myWeights[i], x - nodeSize/2, y+(i*CrossMapper_V0_1_Start.myMapperV3.textSiz));
//		}
//		parent.text(myOutput, x+nodeSize, y+(nodeSize/2));
	}



	void drawMe(){

		//Simple drawing of output node;
		this.parent.fill(255);
		this.parent.stroke(0);
		this.parent.ellipse(this.x, this.y, (float)(nodeSize*0.8), (float)(nodeSize*0.8));
		if(showControls==true){
			showControls();
		}
		drawArcs();
		showInfo();

		/*if(setWeightsMode){
			parent.fill(0,0,150,100);
			parent.stroke(0);
		}
		else{
			parent.fill(150);
			parent.stroke(0);
		}
		//parent.ellipse(x, y, (float)(nodeSize*0.8), (float)(nodeSize*0.8));
		if(myActiveConnects<=1){
			parent.ellipse(x, y, (float)(nodeSize*0.8), (float)(nodeSize*0.8));

		}
		else if(myActiveConnects==2){
			parent.rect(x, y, nodeSize/4, (float)(nodeSize*0.8));
		}
		else{
			drawPoly();
		}
		 */
	}

	void drawPoly(){
		//System.out.println("Drawing Polygon");

		/*if (containsPoint(vertices, mouseX, mouseY)) {
		    fill(200, 200);
		} 
		else {
		noFill();
		}
		 */

		/*parent.fill(0);
		parent.beginShape();
		//for (PVector v : vertices) {
		for(int i=0;i<myActiveConnects;i+=1){
			parent.vertex(vectors[i].x, vectors[i].y);
		}
		parent.endShape(parent.CLOSE);
		 */

	}



	/*if(myActiveConnects<=1){
			parent.ellipse(x, y, (float)(nodeSize*0.8), (float)(nodeSize*0.8));

		}
		else if(myActiveConnects==2){
			parent.rect(x, y, nodeSize/4, (float)(nodeSize*0.8));
		}
		else{
			generateCoords();
			parent.beginShape();
			for(int i=0;i<myActiveConnects;i+=1){
				parent.vertex(controlsXs[i], controlsYs[i]);
			}
			parent.endShape(parent.CLOSE);
		}

		if(setWeightsMode){
			parent.fill(0);
			if(overMe()){
				updateController(parent.mouseX-MapperV3.xShift,parent.mouseY-MapperV3.yShift);
			}
			parent.ellipse(controllerX, controllerY, (float)(nodeSize*0.1), (float)(nodeSize*0.1));
		}
	 */

	/*void updateController(float _newX, float _newY){
		controllerX = _newX;
		controllerY = _newY;

	}
	 */

	void storeLast(int senderId_, float newInput_)
	{
		int senderId = senderId_;
		float newInput = newInput_;
		lastInputs[senderId] = newInput;
	}

	void outputUpdate(int senderId_, float newInput_) {
		int sender = senderId_;
		float newInput = newInput_;

		float output = newInput;
		for (int i = 0; i < maxInputNs; i++) {
			if ((possibleConnects[i] ==true) && (i != sender)) {
				output += lastInputs[i];
			}
		}
		myOutput = output;
		PApplet.println("The combined, weighted output for Node" + iD + " is " + output);
		//send the id number and the value
		CrossMapper_V0_1_Start.myMapperV3.oscSend(iD, myOutput);
	}

	void generateCoords(){

		controlsXs=new float[noOfControls];
		controlsYs=new float[noOfControls];
		float angle = 180.0F/(float)noOfControls; //Angle step
		float offset=parent.random(0.0F,25.0F);
		//angle=angle+offset;
		//System.out.println("Generating Input Co-ords");
		for(int i=0;i<noOfControls;i+=1){
			controlsXs[i]= x+offset-parent.cos(parent.radians(angle*i))*nodeSize;
			controlsYs[i]= y+offset-parent.sin(parent.radians(angle*i))*nodeSize;
			//System.out.println("x"+i+" is: "+controlsXs[i]+" y"+i+" is: "+controlsYs[i]);
		} 
		//makeVector();

	}


	void showControls(){
		for(int i=0;i<noOfControls;i+=1){
			this.parent.line(x,y,controlsXs[i],controlsYs[i]);
			this.parent.rect(controlsXs[i], controlsYs[i], (float)(nodeSize*0.2), (float)(nodeSize*0.2));

		}
	}
	void moveMe(int x_, int y_){
		//System.out.println("Moving node "+iD);
		if(moving){
			this.x=x_;
			this.y=y_;
			generateCoords();
			measureConnects();
		}
	}

	void overControl() {
		int control=0;
		float [] xDistances=new float[noOfControls];
		float [] yDistances=new float[noOfControls];
		for(int i=0;i<noOfControls;i+=1){
			xDistances[i] = controlsXs[i]+crossMapper_V0_1.CrossMapper_V0_1.xShift- parent.mouseX;//X-Mapper_V3.MapperV3.xShift;
			yDistances[i] = controlsYs[i]+crossMapper_V0_1.CrossMapper_V0_1.yShift - parent.mouseY;//-Mapper_V3.MapperV3.yShift;
			if(PApplet.sqrt(PApplet.sq(xDistances[i]) + PApplet.sq(yDistances[i])) < this.nodeSize / 4){
				control = i+1;
				//System.out.println("Control #"+control+" click detected in overControl() method");
				break;
			}
			else{
				control=0;
				//System.out.println("No Control clicked");

			}
		}

	}

	void measureConnects(){
		float totalLengths=0.0F;
		float totalNormalised=0.0f;
		for(int i=0;i<maxInputNs;i+=1){
			if(possibleConnects[i]){
				//System.out.println("Input "+i+" is connected to "+iD);
				//System.out.println("Input "+i+" is at ("
				//+myConnectVectors[i].x+ ", "+myConnectVectors[i].y+")");
				//System.out.println("Output "+iD+" is at ("
				//+x+ ", "+y+")");

				myConnectLengths[i]= PApplet.sqrt(PApplet.sq(x-myConnectVectors[i].x)
						+PApplet.sq(y - myConnectVectors[i].y))-(nodeSize*0.8F);

				//System.out.println("Distance from input node "+i 
				//+" to output node "+iD +" is "+myConnectLengths[i]);
				totalLengths += myConnectLengths[i];

			}
		}
		for(int i=0;i<maxInputNs;i+=1){
			if(possibleConnects[i]){
				normalisedLengths[i]= myConnectLengths[i]/totalLengths;
				normalisedLengths[i]=(1-normalisedLengths[i]);
			}

		}
		for(int i=0;i<maxInputNs;i+=1){
			if(possibleConnects[i]){
				totalNormalised+=normalisedLengths[i];
				//System.out.println("Total Normalised Distance: "+totalNormalised );
			}
		}

		for(int i=0;i<maxInputNs;i+=1){
			if(possibleConnects[i]){
				if(totalNormalised!=0.0){ //catches divide by zero
					normalisedLengths[i]=normalisedLengths[i]/totalNormalised;
					System.out.println("Normalised Distance from input node "+i 
							+" to output node "+iD +" is "+normalisedLengths[i]);
					weightsUpdate(i,normalisedLengths[i]);
				}
				else{
					weightsUpdate(i,1.0F); //If only 1 connection, need to set its weight to 1
				}
			}

		}
	}


	void weightsInit() {
		myWeights = new float[maxInputNs];
		for (int i = 0; i < maxInputNs; i++)
			myWeights[i] = 0.0F;
	}

	void weightsUpdate(int index_, float weightIn_) {
		int index = index_;
		float weightIn = weightIn_;
		myWeights[index]= weightIn; 
		if(myActiveConnects==1){
			for(int i=0;i<maxInputNs;i+=1){
				if(possibleConnects[i]){
					myWeights[i]=1.0F; //for just one connection, the weight should be 1.0
				}
			}
		}


		//for (int i = 0; i < myWeights.length; i++){
		//simple way to assign weights based on equally weighting according to number of connections
		/*if (possibleConnects[i] ==true) { 
				myWeights[i] = (float)(1.0D / myActiveConnects);
			}
			else
				myWeights[i] = 0.0F;
		 */
		//}
	}	
	//New methods to draw coloured arcs based on connection weight.
	void drawArcs(){
		float lastAngle=0.0F;
		//calculate the starting pints of the arcs based on the normalised lengths of connections
		for(int i=0;i<maxInputNs;i+=1){
			if(possibleConnects[i]){
				//arcAngles[i]= 360*normalisedLengths[i];
				//arcVectors[i].x = x-parent.cos(parent.radians(360*normalisedLengths[i]))*nodeSize;
				//arcVectors[i].y = y-parent.sin(parent.radians(360*normalisedLengths[i]))*nodeSize;
				//System.out.println("Arc x"+i+" is: "+arcVectors[i].x+" y"+i+" is: "+arcVectors[i].y);
				//arc(x, y, width, height, start, stop)
				int arcRed = crossMapper_V0_1.CrossMapper_V0_1.inputNodes[i].myColor.getRed();
				int arcBlue = crossMapper_V0_1.CrossMapper_V0_1.inputNodes[i].myColor.getBlue();
				int arcGreen = crossMapper_V0_1.CrossMapper_V0_1.inputNodes[i].myColor.getGreen();
				this.parent.fill(arcRed,arcGreen,arcBlue);

				if(lastAngle==0.0F){

					this.parent.ellipse(x,y,(float)(nodeSize*0.8), (float)(nodeSize*0.8)); //fills with input color when only 1 node connected (normalised length is 0.0)
				}
				else{
					this.parent.arc(x,y,(float)(nodeSize*0.8), (float)(nodeSize*0.8), lastAngle, lastAngle+parent.TWO_PI*normalisedLengths[i]);
				}
				lastAngle =lastAngle+parent.TWO_PI*normalisedLengths[i];
				//this.parent.arc(x,y,(float)(nodeSize*0.8), (float)(nodeSize*0.8), 0, parent.TWO_PI*0.5);
			}
		}
	}
	
	void showInfo(){
		this.parent.textSize(infoTextSize);
		this.parent.textAlign(3); //3 for CENTER, RIGHT is 39, LEFT is 37
		for(int i=0;i<infoDialogs.length;i+=1){
			parent.fill(0);
			this.parent.text(infoDialogs[i], this.x, this.y+(i*infoTextSize));
		}
	}
}




