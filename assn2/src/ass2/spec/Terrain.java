package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL2;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
	if (x < 0 || z < 0 || x > mySize.width - 1 || z > mySize.height - 1) {
	    return 0;
	}
	
        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.ceil(x);
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.ceil(z);
        
        double alBottomLeft = getGridAltitude(x1, z2); // altitude on bottom left
        double alBottomRight = getGridAltitude(x2, z2); // altitude on bottom right
        double alTopLeft = getGridAltitude(x1, z1); // altitude on top left
        double alTopRight = getGridAltitude(x2, z1); // altitude on top right
        
        double xBottomGrad = grad(x1, x2, alBottomLeft, alBottomRight); // gradient of bottom x 
        double xTopGrad = grad(x1, x2, alTopLeft, alTopRight); // gradient of top x
        
        double interBottom = xBottomGrad * (x - x1) + alBottomLeft; // bottom interpolated point
        double interTop = xTopGrad * (x - x1) + alTopLeft; // top interpolated point
        
        double yGrad = grad(z1, z2, interTop, interBottom);
        
        return yGrad * (z - z1) + interTop;        
    }
    
    private double grad(double x1, double x2, double y1, double y2) {
	return (y2 - y1) / (x2 - x1); // calculate the gradient
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }

    public void draw(GL2 gl) {
	for (int x = 0 ; x < mySize.getWidth() - 1 ; x++) {
		for (int z = 0 ; z < mySize.getHeight() - 1 ; z++) {
			Triangle triangleLeft = new Triangle (x, x, x + 1,
							      getGridAltitude(x, z), getGridAltitude(x, z + 1), getGridAltitude(x + 1, z),
							      z, z + 1, z);			
			double[] leftNormal = calculateNormal(triangleLeft);

			Triangle triangleRight = new Triangle (x, x + 1, x + 1,
	    						       getGridAltitude(x, z + 1), getGridAltitude(x + 1, z + 1), getGridAltitude(x + 1, z),
	    						       z + 1, z + 1, z);
			double[] rightNormal = calculateNormal(triangleRight);
	    			
			gl.glBegin(GL2.GL_TRIANGLES); // Left Triangle
				gl.glNormal3d(leftNormal[0],leftNormal[1],leftNormal[2]);
				gl.glVertex3d(triangleLeft.getX()[0], triangleLeft.getY()[0], triangleLeft.getZ()[0]); // Top
				gl.glVertex3d(triangleLeft.getX()[1], triangleLeft.getY()[1], triangleLeft.getZ()[1]); // Bottom Left
				gl.glVertex3d(triangleLeft.getX()[2], triangleLeft.getY()[2], triangleLeft.getZ()[2]); // Bottom Right
			gl.glEnd();
			
			gl.glBegin(GL2.GL_TRIANGLES); // Right Triangle
				gl.glNormal3d(rightNormal[0],rightNormal[1],rightNormal[2]);
		    		gl.glVertex3d(triangleRight.getX()[0], triangleRight.getY()[0], triangleRight.getZ()[0]); // Top
		    		gl.glVertex3d(triangleRight.getX()[1], triangleRight.getY()[1], triangleRight.getZ()[1]); // Bottom Right
		    		gl.glVertex3d(triangleRight.getX()[2], triangleRight.getY()[2], triangleRight.getZ()[2]); // Top Right
	    		gl.glEnd();
	    			
		}
	}
    }
	    
    public double[] calculateNormal(Triangle triangle) {
	double[] normal = {0,0,0};
		    	
	for (int i = 0 ; i < triangle.getVertices() ; i++) {
		normal[0] += (triangle.getY()[i] - triangle.getY()[i + 1]) *
				(triangle.getZ()[i] + triangle.getZ()[i + 1]);
	    	
		normal[1] += (triangle.getZ()[i] - triangle.getZ()[i + 1]) *
				(triangle.getX()[i] + triangle.getX()[i + 1]);
	    	
		normal[2] += (triangle.getX()[i] - triangle.getX()[i + 1]) *
				(triangle.getY()[i] + triangle.getY()[i + 1]);
	}
	
	return normal;
    }

}
