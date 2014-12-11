import org.lwjgl.input.Mouse;


public abstract class EventHandler {
	int leftMouseDownTimes = 0;
	boolean isLeftMouseDown = false;
	int lastLeftX = 0;
	int lastLeftY = 0;
	
	int rightMouseDownTimes = 0;
	boolean isRightMouseDown = false;
	int lastRightX = 0;
	int lastRightY = 0;
	void clasifyMouseEvent(){
		if (Mouse.isButtonDown(0)){
			//ȥ����������Ϣ��ÿ���ΰ�������һ��
			++leftMouseDownTimes;
			if (leftMouseDownTimes >= 2){
				if (isLeftMouseDown)
					onLeftMouseMotion();
				else {
					isLeftMouseDown = true;
					onLeftMouseDown();
					lastLeftX = Mouse.getX();
					lastLeftY = Mouse.getY();
				}
				leftMouseDownTimes = 0;
				lastLeftX = Mouse.getX();
				lastLeftY = Mouse.getY();
			}
		}
		else if (isLeftMouseDown){
			isLeftMouseDown = false;
			leftMouseDownTimes = 0;
			onLeftMouseUp();
		}
		
		if (Mouse.isButtonDown(1)){
			//ȥ����������Ϣ��ÿ���ΰ�������һ��
			++rightMouseDownTimes;
			if (rightMouseDownTimes >= 2){
				if (isRightMouseDown)
					onRightMouseMotion();
				else {
					isRightMouseDown = true;
					onRightMouseDown();
				}
				rightMouseDownTimes = 0;
				lastRightX = Mouse.getX();
				lastRightY = Mouse.getY();
			}
		}
		else if (isRightMouseDown){
			isRightMouseDown = false;
			rightMouseDownTimes = 0;
			onRightMouseUp();
		}
	}
	
	abstract void onLeftMouseDown();
	abstract void onLeftMouseUp();
	abstract void onLeftMouseMotion();
	abstract void onRightMouseDown();
	abstract void onRightMouseUp();
	abstract void onRightMouseMotion();
}