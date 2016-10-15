package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

//import javax.vecmath.Matrix3d;
/**
 * COMMENT: Comment Road
 * 
 * @author malcolmr
 */
public class Road {

	private List<Double> myPoints;
	private double myWidth;
	public final boolean debug = Game.debug;
	private Terrain myTerrain;

	public double altitude(double x, double z) {
		return myTerrain.altitude(x, z);
	}

	public void draw(GL2 gl, Texture texture) {

		// 1 �ظ���t=0~1��ÿ�����x , z -> points[2]

		// 2 ͨ�� x, z ���altitude -> y
		// 3 ���t=0~0.1֮����������� ��ʾΪtangent[3],�����t=0������,��t=0.1���޹�
		// 4 ����tangent��ת90��(��rotate Matrix)
		// 5 ����tangent scale ���ȵ�1/2 (��scale matrix)
		// 6 ��vector+ (x,y,z)�ȵ�·�ص�����

		// 7 �ظ�����ֱ�����t=0.9����������,ȡt=0.9~1������,���t=1.0�����ߵ���t=0.9������
		
		gl.glColor3d(110 / 255d, 110 / 255d, 110 / 255d);

		float textureTop, textureBottom, textureLeft, textureRight;
		TextureCoords textureCoords = texture.getImageTexCoords();
		textureTop = textureCoords.top();
		textureBottom = textureCoords.bottom();
		textureLeft = textureCoords.left();
		textureRight = textureCoords.right();
		texture.enable(gl); 
		texture.bind(gl);

		double offset = 0.001;
		double roadSegment = 0.01;
		for (double t = 0.0; t <= 0.9; t += roadSegment) {
			double[] point0 = point(t);
			double x0 = point0[0]; // ��B
			double z0 = point0[1];
			double y0 = altitude(x0, z0) + offset;
			double[] midpoint = { x0, y0, z0 };

			double t1 = t + roadSegment;
			double[] point1 = point(t1);
			double x1 = point1[0]; // ��A
			double z1 = point1[1];
			double y1 = altitude(x1, z1) + offset;

			double[] tangent = { x1 - x0, y1 - y0, z1 - z0, 1 }; // for matrix
			// calculation
			if (debug) {
				System.out.println("In Road: t is " + t + " point is " + x0
						+ " " + y0 + " " + z0 + " ");
				System.out.println(" Tangent is " + tangent[0] + " "
						+ tangent[1] + " " + tangent[2] + " ");
			}
			double[] scaleRotateTangent = new double[4];
			
			//ʵ������tangent ��� ��·���normal �͵����µĺ���tangent,Ȼ����normaliseһ��
			double[] pointA = {Math.floor(x0),myTerrain.altitude(Math.floor(x0), Math.floor(z0)),Math.floor(z0)};
			double[] pointB = {Math.floor(x0),myTerrain.altitude(Math.floor(x0), Math.ceil(z0)),Math.ceil(z0)};
			double[] pointC = {Math.ceil(x0),myTerrain.altitude(Math.ceil(x0), Math.ceil(z0)),Math.ceil(z0)};
			double[] planeNormal = MathUtil.normal(pointA, pointB, pointC);
			double[] rotateTangent = MathUtil.crossProduct(planeNormal, tangent);
			double[] normalisedVector = MathUtil.normaliseVector(rotateTangent);
			double[] nv4cal = {normalisedVector[0],normalisedVector[1],normalisedVector[2],1};
			scaleRotateTangent = MathUtil.multiply(
					MathUtil.scaleMatrix(myWidth / 2),
					nv4cal);
			
			
			double[] rightbottom = { scaleRotateTangent[0] + midpoint[0],
					scaleRotateTangent[1] + midpoint[1],
					scaleRotateTangent[2] + midpoint[2] };
			double[] leftbottom = { -scaleRotateTangent[0] + midpoint[0],
					-scaleRotateTangent[1] + midpoint[1],
					-scaleRotateTangent[2] + midpoint[2] };
			double[] lefttop = { -scaleRotateTangent[0] + x1,
					-scaleRotateTangent[1] + y1, -scaleRotateTangent[2] + z1 };
			double[] righttop = { scaleRotateTangent[0] + x1,
					scaleRotateTangent[1] + y1, scaleRotateTangent[2] + z1 };
			double[] normal = MathUtil
					.normal(leftbottom, rightbottom, righttop);
			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glNormal3d(-normal[0], -normal[1], -normal[2]);
			gl.glTexCoord2f(textureLeft, textureBottom);
			gl.glVertex3d(rightbottom[0], rightbottom[1], rightbottom[2]);// --
			gl.glTexCoord2f(textureRight, textureBottom);
			gl.glVertex3d(righttop[0], righttop[1], righttop[2]); // --
			gl.glTexCoord2f(textureRight, textureTop);
			gl.glVertex3d(lefttop[0], lefttop[1], lefttop[2]);// --
			gl.glTexCoord2f(textureLeft, textureTop);
			gl.glVertex3d(leftbottom[0], leftbottom[1], leftbottom[2]);// --
			gl.glEnd();
			if (debug) {
				System.out.println("In Road: normal " + normal[0] + " "
						+ normal[1] + " " + normal[2] + " ");
			}
		}

	}

	/**
	 * Create a new road starting at the specified point
	 */
	public Road(double width, double x0, double y0) {
		myWidth = width;
		myPoints = new ArrayList<Double>();
		myPoints.add(x0);
		myPoints.add(y0);
	}

	/**
	 * Create a new road with the specified spine
	 * 
	 * @param width
	 * @param spine
	 */
	public Road(double width, double[] spine, Terrain terrain) {
		myTerrain = terrain;
		myWidth = width;
		myPoints = new ArrayList<Double>();
		for (int i = 0; i < spine.length; i++) {
			myPoints.add(spine[i]);
		}
	}

	/**
	 * The width of the road.
	 * 
	 * @return
	 */
	public double width() {
		return myWidth;
	}

	/**
	 * Add a new segment of road, beginning at the last point added and ending
	 * at (x3, y3). (x1, y1) and (x2, y2) are interpolated as bezier control
	 * points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void addSegment(double x1, double y1, double x2, double y2,
			double x3, double y3) {
		myPoints.add(x1);
		myPoints.add(y1);
		myPoints.add(x2);
		myPoints.add(y2);
		myPoints.add(x3);
		myPoints.add(y3);
	}

	/**
	 * Get the number of segments in the curve
	 * 
	 * @return
	 */
	public int size() {
		return myPoints.size() / 6;
	}

	/**
	 * Get the specified control point.
	 * 
	 * @param i
	 * @return
	 */
	public double[] controlPoint(int i) {
		double[] p = new double[2];
		p[0] = myPoints.get(i * 2);
		p[1] = myPoints.get(i * 2 + 1);
		return p;
	}

	/**
	 * Get a point on the spine. The parameter t may vary from 0 to size().
	 * Points on the kth segment take have parameters in the range (k, k+1).
	 * 
	 * @param t
	 * @return
	 */
	public double[] point(double t) {
		return perfectBezier(t);
	}

	public double[] perfectBezier(double t) {
		double[] p = new double[2];

		int l = myPoints.size() / 2 - 1;
		for (int k = 0; k < myPoints.size() / 2; k++) { 
			p[0] += bernstein(l, k, t) * myPoints.get(2 * k); // x=0,2,4,6

			p[1] += bernstein(l, k, t) * myPoints.get(2 * k + 1); // y=1,3,5,7
			if (debug) {

				System.out.println("bernstein: " + bernstein(l, k, t)
						+ " point-x: " + myPoints.get(2 * k) + " point-y: "
						+ myPoints.get(2 * k + 1));
				System.out.println("result: x " + p[0] + " y " + p[1]);

			}

		}
		if (debug) {
			System.out.println("--------------------------p is " + p[0] + " "
					+ p[1]);

		}

		return p;
	}

	/**
	 * Calculate the Bezier coefficients
	 * 
	 * @param i
	 * @param t
	 * @return
	 */
	public double b(int i, double t) {

		switch (i) {

		case 0:
			return (1 - t) * (1 - t) * (1 - t);

		case 1:
			return 3 * (1 - t) * (1 - t) * t;

		case 2:
			return 3 * (1 - t) * t * t;

		case 3:
			return t * t * t;
		}

		// this should never happen
		throw new IllegalArgumentException("" + i);
	}

	/**
	 * coeffienct for Bezier with unlimited L and K
	 * 
	 * @param i
	 * @param t
	 * @return
	 */
	private double coefficient(double l, double k) {
		if (l == k || k == 0) {
			return 1;
		}
		return MathUtil.factorial(l)
				/ (MathUtil.factorial(k) * MathUtil.factorial(l - k));
	}

	public double bernstein(int l, int k, double t) {

		return coefficient(l, k) * Math.pow(1 - t, l - k) * Math.pow(t, k);
	}

}
