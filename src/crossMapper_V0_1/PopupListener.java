package crossMapper_V0_1;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter {
	JPopupMenu popupMenu = new JPopupMenu();
	
	PopupListener(JPopupMenu _popupMenu) {
        popupMenu = _popupMenu;
    }    
	
	public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	        System.out.println("The Mouse Listener in the Pop-up heard a mouse event!");
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popupMenu.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}

