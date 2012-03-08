package gui;


import util.CodeCompiler;
import util.Utility;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;


public class CompilerFrame extends JInternalFrame implements ActionListener
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea aTextArea = new JTextArea();
    JTextArea errorTextArea = new JTextArea();
    
    JTextField nameField = new JTextField("className");
    JTextField declarations = new JTextField("extends Ball");
    
    //JLabel
    
    
    public CompilerFrame( int width, int height ) 
    {
    	super( "Compiler Frame", true );

    	
        setPreferredSize(new Dimension(width,height));
        setSize(width,height);
        errorTextArea.setSize(width, 500);
        aTextArea.setSize(width, height-500);
        this.setClosable(true);
        

        aTextArea.setAutoscrolls(true);
        this.setVisible(true);
        
        getContentPane().add(createManagerToolbar(),BorderLayout.NORTH);
        
        
        JToolBar jt = new JToolBar();
        jt.setSize(new Dimension(50,30));
        jt.setPreferredSize(new Dimension(400,30));
        
        jt.setVisible(true);
        JLabel label = new JLabel("}  NOTE: use %THISCLASS% for references to itself.");
        jt.add( label);
        
        getContentPane().add(jt, BorderLayout.SOUTH);
        //getContentPane().add(desktop,BorderLayout.CENTER);
        
        this.add( new JScrollPane(aTextArea), BorderLayout.CENTER );
        //
        //JScrollPane p = new JScrollPane(errorTextArea);
        //p.add(jt);
        //this.add( p, BorderLayout.SOUTH );
        this.setLocation(50, 50);
        
        //
   }
    
//    getContentPane().add(createManagerToolbar(),BorderLayout.NORTH);
    //getContentPane().add(desktop,BorderLayout.CENTER);

    protected JToolBar createManagerToolbar() {
        
        JToolBar jt = new JToolBar();
        jt.setSize(new Dimension(50,30));
        jt.setPreferredSize(new Dimension(400,30));
        jt.setVisible(true);

    	JButton compile = new JButton("Compile");
    	
        //startGame.addActionListener(this);
    	
    	compile.addActionListener(this);
        
        jt.add(compile);
        
        JLabel label = new JLabel("    public class    ");
        jt.add( label);
        
        JTextField txt = nameField;
        jt.add(txt);
        
        label = new JLabel("    +    ");
        jt.add( label);
        
        txt = declarations;
        jt.add(txt);
        
        label = new JLabel("   {  ");
        jt.add( label);
        
        
        //jt.add(new )
        
        return jt;
    }

	public void actionPerformed(ActionEvent ev) 
	{
		if ("Compile".equals(ev.getActionCommand())) 
		{ 
            if( CodeCompiler.Compile( nameField.getText(), aTextArea.getText(), declarations.getText()) )
            {
                   Desktop.cFrame.RecreateNodes();
                   Utility.FocusOn( Desktop.cFrame );
            }
            else
            	System.out.println(CodeCompiler.getLastError());
        }
	}
}
