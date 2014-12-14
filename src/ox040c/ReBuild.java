package ox040c;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.DataBufferByte;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class ReBuild {
    ArrayList<Short[]> indicesArrayList = new ArrayList<Short[]>();
    ArrayList<Float[]> verticesArrayList = new ArrayList<Float[]>();
    ArrayList<Float[]> veinArrayList = new ArrayList<Float[]>();
    ByteBuffer byteBuffer = null;

    public static void main(String[] args) {
        new ReBuild().execute();
    }

    public ReBuild() {

        try {
            FileInputStream inputStream = new FileInputStream("res/models/cow_vn.obj");
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine().trim();
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;
                Pattern pattern = Pattern.compile("v(\\s-?\\d\\d*\\.\\d\\d*){3}");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    pattern = Pattern.compile("(-?\\d\\d*\\.\\d\\d*)");
                    matcher = pattern.matcher(line);
                    Float[] floats = new Float[3];
                    int i = 0;
                    while (matcher.find()) {
                        floats[i] = Float.valueOf(matcher.group());
                        ///System.out.println(floats[i]);
                        ++i;
                    }
                    verticesArrayList.add(floats);
                    continue;
                }
                pattern = Pattern.compile("vt(\\s-?\\d\\d*\\.\\d\\d*){2}");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    pattern = Pattern.compile("(-?\\d\\d*\\.\\d\\d*)");
                    matcher = pattern.matcher(line);
                    Float[] floats = new Float[2];
                    int i = 0;
                    while (matcher.find()) {
                        floats[i] = Float.valueOf(matcher.group());
                        //System.out.print(floats[i] + " ");
                        ++i;
                    }
                    veinArrayList.add(floats);
                    //System.out.println();
                    continue;
                }
                pattern = Pattern.compile("f(\\s\\d*\\/\\d*){3}");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    pattern = Pattern.compile("\\d\\d*");
                    matcher = pattern.matcher(line);
                    Short[] shorts = new Short[6];
                    int i = 0;
                    while (matcher.find()) {
                        shorts[i] = Short.valueOf(matcher.group());
                        //System.out.print(shorts[i] + " ");
                        ++i;
                    }
                    indicesArrayList.add(shorts);
                    //System.out.println();
                    continue;
                }
            }
            in.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Display.setTitle("物理几何模型");
            Display.setFullscreen(false);
            DisplayMode mode = new DisplayMode(1024, 600);
            Display.setDisplayMode(mode);
            Display.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void drawModel() {
        glEnable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        glBegin(GL_TRIANGLES);

        for (int i = 0; i < indicesArrayList.size(); i++) {
            for (int j = 0; j < 3; j++) {
                int vi = indicesArrayList.get(i)[2 * j];
                int ni = indicesArrayList.get(i)[2 * j + 1];
                float f1, f2, f3;

                float glScale = 1.0f;

                f1 = veinArrayList.get(ni - 1)[0];
                f2 = veinArrayList.get(ni - 1)[1];
                glTexCoord2f(f1, f2);
                f1 = verticesArrayList.get(vi - 1)[0] / glScale;
                f2 = verticesArrayList.get(vi - 1)[1] / glScale;
                f3 = verticesArrayList.get(vi - 1)[2] / glScale;
                glVertex3f(f1, f2, f3);
            }
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }

    public void drawCoordinate(float red, float green, float blue) {
        glLineWidth(5f);
        glColor3f(red, green, blue);
        ;
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, 10);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 10, 0);
        glVertex3f(0, 0, 0);
        glVertex3f(10, 0, 0);
        glEnd();
    }

    public void execute() {
        InputStream imageStream;
        BufferedImage bufferImage = null;
        try {
            imageStream = new FileInputStream("res/models/texture2.bmp");
            bufferImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Raster raster = bufferImage.getData();
        byte[] bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
        byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(bytes, 0, bytes.length);
        byteBuffer.position(0);
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);//此为纹理过滤参数设置
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, raster.getWidth(), raster.getHeight(), 0,
                GL_RGB, GL_UNSIGNED_BYTE, byteBuffer);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
        glGetFloat(GL_MODELVIEW_MATRIX, originMatrix);

        glGetFloat(GL_MODELVIEW_MATRIX, conversionMatrix);

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);
            glPopMatrix();
            eventHandler.classifyMouseEvent();
            glPushMatrix();

            drawModel();
            drawCoordinate(1, 0, 0);
            glLoadIdentity();
            glMultMatrix(originMatrix);
            drawCoordinate(0, 0, 1);
            Display.update();
        }
        Display.destroy();
    }


    void onLeftMouseDown() {

    }

    void onLeftMouseUp() {

    }

    FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
    FloatBuffer originMatrix = BufferUtils.createFloatBuffer(16);
    FloatBuffer conversionMatrix = BufferUtils.createFloatBuffer(16);
    boolean isRotated = false;

    public FloatBuffer getInverseMatrix(FloatBuffer m) {
        FloatBuffer inv = BufferUtils.createFloatBuffer(m.capacity());
        inv.put(0, (m.get(5) * m.get(10) * m.get(15) -
                m.get(5) * m.get(11) * m.get(14) -
                m.get(9) * m.get(6) * m.get(15) +
                m.get(9) * m.get(7) * m.get(14) +
                m.get(13) * m.get(6) * m.get(11) -
                m.get(13) * m.get(7) * m.get(10)));

        inv.put(4, (-m.get(4) * m.get(10) * m.get(15) +
                m.get(4) * m.get(11) * m.get(14) +
                m.get(8) * m.get(6) * m.get(15) -
                m.get(8) * m.get(7) * m.get(14) -
                m.get(12) * m.get(6) * m.get(11) +
                m.get(12) * m.get(7) * m.get(10)));

        inv.put(8, (m.get(4) * m.get(9) * m.get(15) -
                m.get(4) * m.get(11) * m.get(13) -
                m.get(8) * m.get(5) * m.get(15) +
                m.get(8) * m.get(7) * m.get(13) +
                m.get(12) * m.get(5) * m.get(11) -
                m.get(12) * m.get(7) * m.get(9)));

        inv.put(12, (-m.get(4) * m.get(9) * m.get(14) +
                m.get(4) * m.get(10) * m.get(13) +
                m.get(8) * m.get(5) * m.get(14) -
                m.get(8) * m.get(6) * m.get(13) -
                m.get(12) * m.get(5) * m.get(10) +
                m.get(12) * m.get(6) * m.get(9)));

        inv.put(1, (-m.get(1) * m.get(10) * m.get(15) +
                m.get(1) * m.get(11) * m.get(14) +
                m.get(9) * m.get(2) * m.get(15) -
                m.get(9) * m.get(3) * m.get(14) -
                m.get(13) * m.get(2) * m.get(11) +
                m.get(13) * m.get(3) * m.get(10)));

        inv.put(5, (m.get(0) * m.get(10) * m.get(15) -
                m.get(0) * m.get(11) * m.get(14) -
                m.get(8) * m.get(2) * m.get(15) +
                m.get(8) * m.get(3) * m.get(14) +
                m.get(12) * m.get(2) * m.get(11) -
                m.get(12) * m.get(3) * m.get(10)));

        inv.put(9, (-m.get(0) * m.get(9) * m.get(15) +
                m.get(0) * m.get(11) * m.get(13) +
                m.get(8) * m.get(1) * m.get(15) -
                m.get(8) * m.get(3) * m.get(13) -
                m.get(12) * m.get(1) * m.get(11) +
                m.get(12) * m.get(3) * m.get(9)));

        inv.put(13, (m.get(0) * m.get(9) * m.get(14) -
                m.get(0) * m.get(10) * m.get(13) -
                m.get(8) * m.get(1) * m.get(14) +
                m.get(8) * m.get(2) * m.get(13) +
                m.get(12) * m.get(1) * m.get(10) -
                m.get(12) * m.get(2) * m.get(9)));

        inv.put(2, (m.get(1) * m.get(6) * m.get(15) -
                m.get(1) * m.get(7) * m.get(14) -
                m.get(5) * m.get(2) * m.get(15) +
                m.get(5) * m.get(3) * m.get(14) +
                m.get(13) * m.get(2) * m.get(7) -
                m.get(13) * m.get(3) * m.get(6)));

        inv.put(6, (-m.get(0) * m.get(6) * m.get(15) +
                m.get(0) * m.get(7) * m.get(14) +
                m.get(4) * m.get(2) * m.get(15) -
                m.get(4) * m.get(3) * m.get(14) -
                m.get(12) * m.get(2) * m.get(7) +
                m.get(12) * m.get(3) * m.get(6)));

        inv.put(10, (m.get(0) * m.get(5) * m.get(15) -
                m.get(0) * m.get(7) * m.get(13) -
                m.get(4) * m.get(1) * m.get(15) +
                m.get(4) * m.get(3) * m.get(13) +
                m.get(12) * m.get(1) * m.get(7) -
                m.get(12) * m.get(3) * m.get(5)));

        inv.put(14, (-m.get(0) * m.get(5) * m.get(14) +
                m.get(0) * m.get(6) * m.get(13) +
                m.get(4) * m.get(1) * m.get(14) -
                m.get(4) * m.get(2) * m.get(13) -
                m.get(12) * m.get(1) * m.get(6) +
                m.get(12) * m.get(2) * m.get(5)));

        inv.put(3, (-m.get(1) * m.get(6) * m.get(11) +
                m.get(1) * m.get(7) * m.get(10) +
                m.get(5) * m.get(2) * m.get(11) -
                m.get(5) * m.get(3) * m.get(10) -
                m.get(9) * m.get(2) * m.get(7) +
                m.get(9) * m.get(3) * m.get(6)));

        inv.put(7, (m.get(0) * m.get(6) * m.get(11) -
                m.get(0) * m.get(7) * m.get(10) -
                m.get(4) * m.get(2) * m.get(11) +
                m.get(4) * m.get(3) * m.get(10) +
                m.get(8) * m.get(2) * m.get(7) -
                m.get(8) * m.get(3) * m.get(6)));

        inv.put(11, (-m.get(0) * m.get(5) * m.get(11) +
                m.get(0) * m.get(7) * m.get(9) +
                m.get(4) * m.get(1) * m.get(11) -
                m.get(4) * m.get(3) * m.get(9) -
                m.get(8) * m.get(1) * m.get(7) +
                m.get(8) * m.get(3) * m.get(5)));

        inv.put(15, (m.get(0) * m.get(5) * m.get(10) -
                m.get(0) * m.get(6) * m.get(9) -
                m.get(4) * m.get(1) * m.get(10) +
                m.get(4) * m.get(2) * m.get(9) +
                m.get(8) * m.get(1) * m.get(6) -
                m.get(8) * m.get(2) * m.get(5)));
        float det = m.get(0) * inv.get(0) + m.get(1) * inv.get(4)
                + m.get(2) * inv.get(8) + m.get(3) * inv.get(12);
        if (det < 0.0001 && det > -0.0001)
            return null;
        det = 1.0f / det;
        for (int i = 0; i < 16; i++)
            inv.put(i, (inv.get(i) * det));
        inv.rewind();
        m.rewind();
        return inv;
    }

    private EventHandler eventHandler = new EventHandler() {

        @Override
        void onRightMouseUp() {

        }

        @Override
        void onRightMouseMotion() {

            int vectorX = Mouse.getDX();
            int vectorY = Mouse.getDY();
            int vectorZ = 0;
            glLoadIdentity();
            glTranslatef(vectorX / 200.0f, vectorY / 200.0f, vectorZ);
            glGetFloat(GL_MODELVIEW_MATRIX, matrix);
            glMultMatrix(originMatrix);

            glMultMatrix(conversionMatrix);

            conversionMatrix = mulMatrix(matrix, conversionMatrix);
        }

        @Override
        void onRightMouseDown() {

        }

        @Override
        void onLeftMouseUp() {

        }

        @Override
        void onLeftMouseMotion() {

            int vectorX = Mouse.getDX();
            int vectorY = Mouse.getDY();
            int vectorZ = 0;
            int normalX = vectorY * 1 - 0 * vectorZ;
            int normalY = vectorZ * 0 - 1 * vectorX;
            int normalZ = 0;
            glLoadIdentity();
            float angle = (float) Math.sqrt((double) (vectorX * vectorX + vectorY * vectorY));

            glRotatef(angle, normalX, normalY, normalZ);
            glGetFloat(GL_MODELVIEW_MATRIX, matrix);
            glMultMatrix(originMatrix);

            glMultMatrix(conversionMatrix);

            conversionMatrix = mulMatrix(matrix, conversionMatrix);

        }

        @Override
        void onLeftMouseDown() {

        }
    };

    static FloatBuffer mulMatrix(FloatBuffer m1, FloatBuffer m2) {
        FloatBuffer m = BufferUtils.createFloatBuffer(16);
        for (int i = 0; i != 4; ++i) {
            for (int j = 0; j != 4; ++j) {
                m.put(j * 4 + i,
                        (m1.get(0 * 4 + i) * m2.get(j * 4 + 0)
                                + m1.get(1 * 4 + i) * m2.get(j * 4 + 1)
                                + m1.get(2 * 4 + i) * m2.get(j * 4 + 2)
                                + m1.get(3 * 4 + i) * m2.get(j * 4 + 3)));
            }
        }
        m1.rewind();
        m2.rewind();
        m.rewind();
        return m;
    }

    void printMatrix(FloatBuffer matrix) {
        for (int i = 0; i != 4; ++i) {//i是列数
            for (int j = 0; j != 4; ++j) {//j是行数
                System.out.print(matrix.get(j * 4 + i) + " ");
            }
            System.out.println();
        }
        matrix.rewind();
    }
}
