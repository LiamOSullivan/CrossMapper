package crossMapper_V0_1;

import processing.core.PApplet;
import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Node
{
	PApplet parent;
	int iD;
	int x;
	int y;
	int nodeSize;
	boolean allowMove; 
	boolean showControls; 
	//parent.color mycolour=color(255,0,0);

	Node(PApplet parent_, int iD_, float g, float f, int nodeSize_)
	{
		this.parent = parent_;
		this.iD = iD_;
		this.nodeSize = nodeSize_;
		this.x = (int) g;
		this.y = (int) f;
		allowMove = false;
		showControls = false;
	}
	
	

}

