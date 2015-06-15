package jung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;


import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.PluggableRenderContext;

public class GraphVisualization {
	EntityGraph eg;
	
	private VisualizationViewer<Integer,String> vv;
	private Layout<Integer, String> layout;
	private DefaultModalGraphMouse gm;
	private String filename;
	
	public GraphVisualization(EntityGraph eg, String filename){
		this.eg = eg;
		this.filename = filename;
	}
	
	public void show(){
	
	layout = new FRLayout(this.eg.entityGraph);
    layout.setSize(new Dimension(1500,800)); // sets the initial size of the layout space - CircleLayout

    vv = new VisualizationViewer(layout);
//    BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
    vv.setPreferredSize(new Dimension(1600,900)); //Sets the viewing area size
    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);   
  
    vv.setBackground(Color.WHITE);
    vv.setForeground(Color.black);
    
    
//    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//    vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.BLACK));
    
    
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.gray));
    vv.getRenderContext().setEdgeStrokeTransformer(new ConstantTransformer(new BasicStroke(2.5f)));

    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR); 
    
//    vv.setVertexToolTipTransformer(new ToStringLabeller());
    
    
    gm = new DefaultModalGraphMouse();
    gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    gm.setMode(ModalGraphMouse.Mode.ANNOTATING);
    gm.setMode(ModalGraphMouse.Mode.PICKING);
   
    vv.setGraphMouse(gm); 
    vv.addKeyListener(gm.getModeKeyListener());
    
    
    JFrame frame = new JFrame("Graph Viewer - " + this.filename);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(vv);
    
    // Let's add a menu for changing mouse modes
    JMenuBar menuBar = new JMenuBar();
    JMenu modeMenu = gm.getModeMenu();
    modeMenu.setText("Mode");
    modeMenu.setIcon(null); // I'm using this in a main menu
    modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size so I can see the text
    
    menuBar.add(modeMenu);
    frame.setJMenuBar(menuBar);
    gm.setMode(ModalGraphMouse.Mode.PICKING);// Start off in editing mode
    frame.pack();
    frame.setVisible(true);  
	}
}
