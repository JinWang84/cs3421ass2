package ass2.spec;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * COMMENT: Comment Game
 * 
 * @author malcolmr
 */
@SuppressWarnings("serial")
public class Game extends JFrame {
	private Terrain myTerrain;

	public Game(Terrain terrain) {
		super("Assignment 2");
		myTerrain = terrain;
	}

	private static JToggleButton sunButton;
	private static JToggleButton CameraButton;
	public static JLabel label;
	public static JLabel label2;
	public static boolean worldCamera = false;
	public static boolean originSun = false;
	public static boolean debug = false;

	/**
	 * Run the game.
	 * 
	 */
	public void run() {
		label = new JLabel();
		label2 = new JLabel();
		label.setForeground(new java.awt.Color(204, 204, 255));
		label.setText("<HTML>THE Label<P><P>Sample Output</HTML>");
		Camera camera = new Camera(myTerrain);
		System.out.println(myTerrain.getGridAltitude(1, 1));
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLJPanel panel = new GLJPanel(glcapabilities);

		panel.addGLEventListener(camera);
		panel.addKeyListener(new Keyboard(camera));
		panel.setFocusable(true);
		panel.requestFocus();
		sunButton = new javax.swing.JToggleButton();
		CameraButton = new javax.swing.JToggleButton();
		sunButton.setText("Original Sun light");
		sunButton.setFocusable(false);
		sunButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sunButtonActionPerformed(evt);
			}
		});

		CameraButton.setFocusable(false);
		CameraButton.setText("World Camera");
		CameraButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CameraButtonActionPerformed(evt);
			}
		});
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(18, 18, 18)
								.addComponent(label,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										259,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										391, Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
																.addComponent(
																		CameraButton,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		sunButton,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addComponent(
														label2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														122,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		sunButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		CameraButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		508,
																		Short.MAX_VALUE)
																.addComponent(
																		label2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		213,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(
														label,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														587, Short.MAX_VALUE))
								.addContainerGap()));

		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();

		getContentPane().add(panel, BorderLayout.CENTER);

		setSize(800, 800);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	protected void CameraButtonActionPerformed(ActionEvent evt) {
		if (worldCamera == false) {
			worldCamera = true;
		} else {
			worldCamera = false;
		}

	}

	protected void sunButtonActionPerformed(ActionEvent evt) {
		if (originSun == false) {
			originSun = true;
		} else {
			originSun = false;
		}

	}

	/**
	 * Load a level file and display it.
	 * 
	 * @param args - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {		
		Terrain terrain = LevelIO.load(new File(args[0]));		
		Game game = new Game(terrain);
		game.run();
	}
}
