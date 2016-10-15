package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{


	Camera myCamera;
	public Keyboard (Camera camera){
		myCamera = camera;
	}
	
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// 
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		 switch (e.getKeyCode()) {
	        case KeyEvent.VK_SPACE:
	           // myWireframe = !myWireframe;
	            break;

	        case KeyEvent.VK_LEFT:
	        	myCamera.rotateLeft();
	            break;

	        case KeyEvent.VK_RIGHT:
	        	myCamera.rotateRight();
	            break;

	        case KeyEvent.VK_UP:
	        	myCamera.stepForward();
	            break;

	        case KeyEvent.VK_DOWN:
	        	myCamera.stepBackward();
	            break;
	        
			case KeyEvent.VK_P:
				myCamera.flip();
	        	break;
	    
			case KeyEvent.VK_O:
				//myCamera.rotateRight();
				break;
			case KeyEvent.VK_W:
				myCamera.avatarStepForward();
				break;
			case KeyEvent.VK_S:
				myCamera.avatarStepBorward();
				break;
			case KeyEvent.VK_A:
				myCamera.avatarRotateCCW();
				break;
			case KeyEvent.VK_D:
				myCamera.avatarRotateCW();
				break;
				
				
			case KeyEvent.VK_Z:
				myCamera.ambientUp();
				break;
			case KeyEvent.VK_X:
				myCamera.ambientDown();
				break;
			case KeyEvent.VK_C:
				myCamera.diffuseUp();
				break;
			case KeyEvent.VK_V:
				myCamera.diffuseDown();
				break;
		 }		
	}
}
