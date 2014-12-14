package ox040c;

import org.lwjgl.input.Mouse;

public abstract class EventHandler {
    int leftMouseDownTimes = 0;
    boolean isLeftMouseDown = false;

    public float getTranslateX() {
        return translateX;
    }

    public float getTranslateY() {
        return translateY;
    }

    float translateX = 0;
    float translateY = 0;

    public float getRotateX() {
        return rotateX;
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public float getRotateY() {
        return rotateY;
    }

    float rotateX = 0;
    float rotateY = 0;
    float rotateZ = 0;

    int rightMouseDownTimes = 0;
    boolean isRightMouseDown = false;

    void classifyMouseEvent() {
        if (Mouse.isButtonDown(0)) {
            //去掉连续的消息，每两次按键算作一次
            ++leftMouseDownTimes;
            if (leftMouseDownTimes >= 2) {
                if (isLeftMouseDown)
                    onLeftMouseMotion();
                else {
                    isLeftMouseDown = true;
                    onLeftMouseDown();
                }
                leftMouseDownTimes = 0;
            }
        } else if (isLeftMouseDown) {
            isLeftMouseDown = false;
            leftMouseDownTimes = 0;
            onLeftMouseUp();
        }

        if (Mouse.isButtonDown(1)) {
            //去掉连续的消息，每两次按键算作一次
            ++rightMouseDownTimes;
            if (rightMouseDownTimes >= 2) {
                if (isRightMouseDown)
                    onRightMouseMotion();
                else {
                    isRightMouseDown = true;
                    onRightMouseDown();
                }
                rightMouseDownTimes = 0;
            }
        } else if (isRightMouseDown) {
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
