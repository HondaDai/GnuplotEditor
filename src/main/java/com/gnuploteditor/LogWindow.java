package com.gnuploteditor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by HondaDai on 2015/03/13.
 */
public class LogWindow extends JFrame {


    public static LogWindow instance = new LogWindow();

    private JTextArea output;
    private int defaultHeight = 130;


    private LogWindow() {
        this.setLayout(new GridLayout(1, 1));
        this.setSize(800, defaultHeight);

        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ((JPanel) this.getContentPane()).setBackground(Color.white);

        output = new JTextArea();
        output.setEditable(false);
        this.add(output);

    }

    public void setOutput(String msg) {
        if ( this.isVisible() ) {
            output.setText(msg);
        }
    }

    @Override
    public void setVisible(boolean v) {
        if (v) {
            this.setBounds(
                    GnuplotEditor.instance.getX(),
                    GnuplotEditor.instance.getY()+GnuplotEditor.instance.getHeight() - defaultHeight,
                    GnuplotEditor.instance.getWidth(),
                    defaultHeight
            );
            GnuplotEditor.instance.setBounds(
                    GnuplotEditor.instance.getX(),
                    GnuplotEditor.instance.getY(),
                    GnuplotEditor.instance.getWidth(),
                    GnuplotEditor.instance.getHeight() - defaultHeight
            );
        }
        super.setVisible(v);
    }

}
