package ass2.spec;

import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;

import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import javax.swing.JLabel;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Camera implements GLEventListener {
	final public boolean debug = Game.debug;
	double xtrans = 0.0;
	double ytrans = 0.0;
	double ztrans = 0.0;
	int yrotate = 0;
	Terrain myTerrain;
	GLU glu = new GLU();
	float ambient = 0.7f;
	float diffuse = 1.5f;
	JLabel myLabel = Game.label;
	boolean lightEnable = true;
	double cameraAvatarDistance = 5.0;
	boolean flipped = false;
	public double avatarAngle = 0.0;
	public double angleStep = 10.0;
	private double worldRotate = 0.0;
	public double time = 0; // 0~24, 25frame per hour in world
	public double[] lightDirAt8 = { -1, 0, 0, 1 };
	double[] color = { 1.0, 1.0, 1.0 };
	
	public Camera(Terrain theTerrain) {
		myTerrain = theTerrain;
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		GL2 gl = arg0.getGL().getGL2();
		if (debug) {
			System.out.println("display method working");
		}
		gl.glClearColor(0.0f, 245 / 255f, 1.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (Game.worldCamera == false) {
			double avatarRadius = avatarAngle * Math.PI / 180.0;
			double eyex = myTerrain.getAvatar().x - Math.sin(avatarRadius)
					* cameraAvatarDistance;
			double eyey = myTerrain.getAvatar().getY() + 3.0;
			double eyez = myTerrain.getAvatar().z - Math.cos(avatarRadius)
					* cameraAvatarDistance;

			if (!flipped) {
				glu.gluLookAt(eyex, eyey, eyez, myTerrain.getAvatar().x,
						myTerrain.getAvatar().getY(), myTerrain.getAvatar().z,
						0, 1, 0);
			} else {
				glu.gluLookAt(eyex, -eyey, eyez, myTerrain.getAvatar().x,
						myTerrain.getAvatar().getY(), myTerrain.getAvatar().z,
						0, -1, 0);
			}

		} else { // For World Camera
			double[] worldMidPoint = {
					myTerrain.size().getWidth() / 2,
					myTerrain.altitude(myTerrain.size().getWidth() - 1,
							myTerrain.size().getHeight() - 1) / 2,
					myTerrain.size().getHeight() / 2 };
			double eyex = worldMidPoint[0]
					+ Math.sin(worldRotate * Math.PI / 180)
					* (cameraAvatarDistance + ztrans);
			double eyey = worldMidPoint[0];
			double eyez = worldMidPoint[2]
					- Math.cos(worldRotate * Math.PI / 180)
					* (cameraAvatarDistance + ztrans);
			if(eyex < myTerrain.size().getWidth()-1 && eyex>=0 && eyez>=0 &&
			eyez < myTerrain.size().getHeight()-1 && 	
					myTerrain.altitude(eyex, eyez) >= eyey){
				eyey += 5;
			}
			
			if (!flipped) {
				glu.gluLookAt(eyex, eyey, eyez, worldMidPoint[0],
						worldMidPoint[1], worldMidPoint[2], 0, 1, 0);
			} else {
				glu.gluLookAt(eyex, -eyey, eyez, worldMidPoint[0],
						worldMidPoint[1], worldMidPoint[2], 0, -1, 0);
			}

		}
		gl.glPushMatrix();

		if (lightEnable) {
			setLighting(gl);
		}
		drawShape(gl);
		myLabel.setText("<HTML>Avatar Position<P>X:" + myTerrain.getAvatar().x
				+ "<P>Y:" + myTerrain.getAvatar().y + "<P>Z:"
				+ myTerrain.getAvatar().z + "<P>angle:" + avatarAngle
				+ "<P>Light:" + "<P>Sunlight direction:"
				+ myTerrain.getSunlight()[0] + " " + myTerrain.getSunlight()[1]
				+ " " + myTerrain.getSunlight()[2] + " " + "<P>luminance"
				+ "<P>ambient " + ambient + "<P>diffuse" + diffuse + "<P>Time"
				+ time + "<P>sun Color: " + color[0] * 255 + " " + color[1]
				* 255 + " " + color[2] * 255 + "</HTML>");
	}



	private void setLighting(GL2 gl) {
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glPushMatrix();
		if (Game.originSun == false) {
			time += 0.04;
			if (time >= 24) {
				time = 0;
			}
			double sunRotate = time * 15;
			gl.glRotated(sunRotate, 0.0, 0.0, 1.0); 
			if (debug) {
				System.out.println("LIghting lumi:ambient " + ambient
						+ " diffuse " + diffuse);
			}
			if (time >= 0 && time < 8) { // ��
				color[0] = 0.0;
				color[1] = 102 / 255d;
				color[2] = 1.0;
				// blue = 0,102,255
			} else if (time >= 8 && time <= 18) { // ��->��
				// yellow = 255,255,51
				color[0] = (time - 8) / (18 - 8); // 0->255
				color[1] = (102 + (255 - 102) * (time - 8) / (18 - 8)) / 255; // 102->255
				color[2] = (255 - (255 - 51) * (time - 8) / (18 - 8)) / 255; // 255->51
			} else if (time > 18 && time <= 19) {
				color[0] = 1.0;
				color[1] = 1.0;
				color[2] = 51 / 255d;
			} else if (time > 19 && time < 24) {// ��->��
				color[0] = 1 - (time - 19) / (24 - 19); // 255->0
				color[1] = (255 - (255 - 102) * (time - 19) / (24 - 19)) / 255;
				color[2] = (51 + (255 - 51) * (time - 19) / (24 - 19)) / 255;
			}
			float[] a = new float[4];
			a[0] = (float) (color[0] * ambient);
			a[1] = (float) (color[1] * ambient);
			a[2] = (float) (color[2] * ambient);
			// a[0] = a[1] = a[2] = ambient;
			a[3] = 1.0f;
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, a, 0);
			float[] ambientPos = new float[] { 1.0f, 0.0f, 1.0f, 1.0f }; // position
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, ambientPos, 0);

			float[] d = new float[4];
			d[0] = (float) (color[0] * diffuse);
			d[1] = (float) (color[1] * diffuse);
			d[2] = (float) (color[2] * diffuse);
			// d[0] = d[1] = d[2] = diffuse;
			d[3] = 1.0f;
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, d, 0);
			float[] diffusePos = new float[] { 1.0f, 5.0f, 5.0f, 0.0f }; // direction
			diffusePos[0] = myTerrain.getSunlight()[0];
			diffusePos[1] = myTerrain.getSunlight()[1];
			diffusePos[2] = myTerrain.getSunlight()[2];

			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, diffusePos, 0);
			// gl.glLightf(GL2.GL_LIGHT1, GL2.GL_QUADRATIC_ATTENUATION , (float)
			// 0.5);

		} else { //use default light
			float[] a = new float[4];
			a[0] = a[1] = a[2] = ambient;
			a[3] = 1.0f;
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, a, 0);
			float[] ambientPos = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, ambientPos, 0);

			float[] d = new float[4];
			d[0] = d[1] = d[2] = diffuse;
			d[3] = 1.0f;
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, d, 0);
			float[] diffusePos = new float[] { 1.0f, 5.0f, 5.0f, 0.0f }; // ������
			diffusePos[0] = myTerrain.getSunlight()[0];
			diffusePos[1] = myTerrain.getSunlight()[1];
			diffusePos[2] = myTerrain.getSunlight()[2];
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, diffusePos, 0);

		}
		gl.glPopMatrix();
	}

	private void drawShape(GL2 gl) {
		drawCoor(gl);
		myTerrain.draw(gl, groundTexture, treeTexture, roadTexture);

	}

	private void drawCoor(GL2 gl) {
		gl.glColor3f(0.0f, 0.0f, 1.0f);// blue
		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(30.0, 0.0, 0.0);
		gl.glColor3d(0.0, 1.0, 0.0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 30, 0);
		gl.glColor3d(0.0, 0.0, 1.0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, 30);
		gl.glEnd();
	}

	Texture groundTexture;
	Texture treeTexture;
	Texture leafTexture;
	Texture roadTexture;

	private void setTexture(GL2 gl) {
		try {
			groundTexture = TextureIO.newTexture(getClass().getClassLoader()
					.getResource("ground.jpg"), false, ".jpg");
			treeTexture = TextureIO.newTexture(getClass().getClassLoader()
					.getResource("tree.jpg"), false, ".jpg");
			roadTexture = TextureIO.newTexture(getClass().getClassLoader()
					.getResource("road.jpg"), false, ".jpg");
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL2 gl = arg0.getGL().getGL2();
		gl.glLoadIdentity();
		if (lightEnable) {

		}
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK,
				GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		setTexture(gl);

	}
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		final GLU glu = new GLU();
		if (debug) {
			System.out.println("reshape method working");
		}

		final GL2 gl = glDrawable.getGL().getGL2();

		gl.glEnable(GL.GL_DEPTH_TEST);
		if (lightEnable) {
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_LIGHT1);
			// normalise normals (!)
			// this is necessary to make lighting work properly
			gl.glEnable(GL2.GL_NORMALIZE);
		}
		if (height <= 0)
			height = 1;
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 50.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void rotateLeft() {
		if (debug) {
			System.out.println("keyboard: Rotata Right");
		}
		worldRotate += 10;

	}

	public void rotateRight() {
		if (debug) {
			System.out.println("keyboard: Rotate LEft");
		}
		worldRotate += -10;
	}

	public void flip() {

		if (flipped == true) {
			flipped = false;
		} else {
			flipped = true;
		}
	}

	/**
	 * The camera should move up and down following the terrain. So if you move
	 * it forward up a hill, the camera should move up the hill.
	 * 
	 */
	public void stepBackward() {
		if (debug) {
			System.out.println("keyboard: Step Forward");
		}
		ztrans += 0.5;

	}

	public void stepForward() {
		if (debug) {
			System.out.println("keyboard: Step Backward");
		}
		ztrans += -0.5;
	}

	public void ambientUp() {
		ambient += 0.1f;
	}

	public void ambientDown() {
		ambient += -0.1f;
	}

	public void diffuseUp() {
		diffuse += 0.1f;
	}

	public void diffuseDown() {
		diffuse += -0.1f;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	public void avatarStepForward() {
		myTerrain.getAvatar().stepForawrd(avatarAngle);
	}

	public void avatarStepBorward() {
		myTerrain.getAvatar().stepBackward(avatarAngle);
	}

	public void avatarRotateCCW() {
		avatarAngle += angleStep;
	}

	public void avatarRotateCW() {
		avatarAngle -= angleStep;
	}

}
