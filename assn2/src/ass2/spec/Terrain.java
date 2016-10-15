package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;


//import sailing.objects.Island;

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
    final public boolean debug = Game.debug;
    boolean drawTriangle = false;
    
    public void draw(GL2 gl, Texture groundTexture, Texture treeTexture, Texture roadTexture){
    	   	
    	if(debug) {
		System.out.println("HEIGHT is "+ mySize.getHeight() + " width is " + mySize.getWidth());
	}
		
    	float textureTop, textureBottom, textureLeft, textureRight;
    	TextureCoords textureCoords = groundTexture.getImageTexCoords();
        textureTop = textureCoords.top();
        textureBottom = textureCoords.bottom();
        textureLeft = textureCoords.left();
        textureRight = textureCoords.right();
        
        groundTexture.enable(gl);  
        groundTexture.bind(gl);  
    	for(int i = 0; i< mySize.getHeight()-1 ; i++) { //loop from 1 to 9 
    		for(int y = 0; y< mySize.getWidth()-1; y++) {
    			if(debug) {
    				System.out.println("i:"+i+" y:"+y+" altitute:"+this.getGridAltitude(i, y));
    			}
    			//CCW
    			gl.glColor3d(102/255d, 102/255d, 0.0);
    	       // gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);  
	    	        if(!drawTriangle){
	    	        float[] difColor = {102/255f, 1.0f, 51/255f, 1};
	    	        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, difColor, 0);
	    			gl.glBegin(GL2.GL_QUADS);
	    			double[] a = {i,this.getGridAltitude(i, y),y};
	    			double[] b = {i,this.getGridAltitude(i, y+1),y+1};
	    			double[] c = {i+1,this.getGridAltitude(i+1, y+1),y+1};
	    			double[] n = MathUtil.normal(a, b, c);
	    			
	    			gl.glNormal3d(n[0], n[1], n[2]);
	    			System.out.println("aaaaaaaaaaaaaa"+n[0]+" "+n[1]+" "+n[2]);
	    			gl.glTexCoord2f(textureLeft, textureBottom);//����
	    			gl.glVertex3d(i,this.getGridAltitude(i, y),y);
	    			gl.glTexCoord2f(textureRight, textureBottom); //����
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1),y+1);
	    			gl.glTexCoord2f(textureRight, textureTop);//����
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1, y+1),y+1);
	    			gl.glTexCoord2f(textureLeft, textureTop);//����
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y),y);
	    			gl.glEnd();
    	        } else {
    	        	float[] difColor = {1.0f, 1.0f, 1.0f, 1};
	    	        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, difColor, 0);
	    			
    	        	double[] a = {i,this.getGridAltitude(i, y),y};
	    			double[] b = {i,this.getGridAltitude(i, y+1),y+1};
	    			double[] c = {i+1,this.getGridAltitude(i+1, y),y};
	    			double[] n = MathUtil.normal(a, b, c);
	    			
	    			gl.glNormal3d(n[0], n[1], n[2]);
	    			gl.glBegin(GL.GL_TRIANGLES);
	    			gl.glTexCoord2f(textureLeft, textureBottom);//����
	    			gl.glVertex3d(i,this.getGridAltitude(i, y),y);
	    			gl.glTexCoord2f(textureRight, textureBottom); //����
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1),y+1);
	    			gl.glTexCoord2f(textureLeft, textureTop);//����
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y),y);
	    			gl.glEnd();
	    			
	    			gl.glBegin(GL.GL_TRIANGLES);
	    			gl.glTexCoord2f(textureRight, textureBottom); //����
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1),y+1);
	    			gl.glTexCoord2f(textureRight, textureTop);//����
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1, y+1),y+1);
	    			gl.glTexCoord2f(textureLeft, textureTop);//����
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y),y);
	    			gl.glEnd();
    	        }
    			
    			if(!drawTriangle){
	    			gl.glColor3d(0.0, 0.0, 0.0);
	    			//CCW
	    			gl.glBegin(GL.GL_LINE_LOOP);
	    			gl.glVertex3d(i,this.getGridAltitude(i, y)+0.01,y);
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1)+0.01,y+1);
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1, y+1)+0.01,y+1);
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y)+0.01,y);
	    			gl.glEnd();
	    			gl.glDisable( GL.GL_POLYGON_OFFSET_FILL ); 
    			} else {
    				gl.glColor3d(0.0, 0.0, 0.0);
    				gl.glBegin(GL.GL_LINE_LOOP);
	    			gl.glVertex3d(i,this.getGridAltitude(i, y),y);
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1),y+1);
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y),y);
	    			gl.glEnd();
	    			
	    			gl.glBegin(GL.GL_LINE_LOOP);
	    			gl.glVertex3d(i,this.getGridAltitude(i, y+1)+0.01,y+1);
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1, y+1)+0.01,y+1);
	    			gl.glVertex3d(i+1,this.getGridAltitude(i+1,y)+0.01,y);
	    			gl.glEnd();
    			}
    		}
    	}
    	drawAvatar(gl);
    	drawTrees(gl,treeTexture);
    	drawRoad(gl, roadTexture);
    }
    
    public Avatar getAvatar(){
    	return avatar;
    }
    
    private Avatar avatar = new Avatar(this);
    public void drawAvatar(GL2 gl){    	
    	avatar.draw(gl);
    }
    
    public void drawTrees(GL2 gl,Texture TreeTexture){
    	for(Tree tree : myTrees){
    		tree.draw(gl,TreeTexture);
    	}
    }
    
    public void drawRoad(GL2 gl,Texture texture){
    	for(Road road: myRoads){
    		road.draw(gl, texture);
    	}
    }
    
    
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
	double altitude = 0;
        int xc = (int) Math.ceil(x);
    	int xf = (int) Math.floor(x);
    	int zc = (int) Math.ceil(z);
    	int zf = (int) Math.floor(z);    	
    	
    	//     (xf, zf)  (x, zf) (xc, zf)   
        // 	    +-------+-----+  
        //     	    |       |     |
        //          |       |     |
    	//  (xf, z) +-------+-----+ (xc, z)
        //          |     (x, z)  |
        //  	    +-------------+
        //     (xf, zc)          (xc, zc)

    	// altitude at (xf, z)
    	double alLeft = (z - zc) / (zf - zc) * (getGridAltitude(xf, zf) - getGridAltitude(xf, zc)) + getGridAltitude(xf, zc);
    	// altitude at (xc, z)
    	double alRight = (z - zc) / (zf - zc) * (getGridAltitude(xc, zf) - getGridAltitude(xc, zc)) + getGridAltitude(xc, zc);
    	// altitude at (x, z)
	altitude = (x - xc) / (xf - xc) * (alLeft - alRight) + alRight;
	
	return altitude;
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
        Tree tree = new Tree(x, y, z,this);
        myTrees.add(tree);
    }    

    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine,this);
        myRoads.add(road);        
    }

}
