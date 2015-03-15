package com.gnuploteditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by HondaDai on 2015/03/13.
 */
public class Menu extends JPopupMenu {

    public Menu() {
        initWorkingDirectoryMenu();
        initUpdateRateMenu();
        this.addSeparator();
        initExportImageMenu();
        initOpenLogWindowMenu();
    }

    public void initWorkingDirectoryMenu() {

        JMenu parent = new JMenu("Working Directory");
        this.add(parent);

        JMenuItem item;
        item = new JMenuItem("Current: "+GnuplotEditor.instance.getWorkingDirectory().getAbsoluteFile());
        parent.add(item);

        item = new JMenuItem("Choose...");
        parent.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(GnuplotEditor.instance.getWorkingDirectory().getAbsoluteFile());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(GnuplotEditor.instance) == JFileChooser.APPROVE_OPTION) {


                    if (chooser.getSelectedFile().isDirectory()) {
                        GnuplotEditor.instance.setWorkingDirectory( chooser.getSelectedFile() );
                    } else {
                        GnuplotEditor.instance.setWorkingDirectory( chooser.getCurrentDirectory() );
                    }
                }
            }
        });
    }

    public void initUpdateRateMenu() {
        JMenu parent = new JMenu("Update Rate");
        this.add(parent);

        ButtonGroup group = new ButtonGroup();
        for (final int rate : new int[]{50, 100, 300, 500, 700}){
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(Integer.toString(rate)+" ms");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GnuplotEditor.instance.setUpdateRate(rate);
                }
            });
            if (GnuplotEditor.instance.getUpdateRate() == rate)
                item.setSelected(true);
            parent.add(item);
        }

    }

    public void initExportImageMenu() {
        JMenuItem item = new JMenuItem("Export Image...");
        this.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GnuplotEditor.instance.exportImage();

            }
        });

    }

    public void initOpenLogWindowMenu() {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem("Open LogWindow");
        item.setSelected(LogWindow.instance.isVisible());

        this.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GnuplotEditor.instance.openLogWindow( !LogWindow.instance.isVisible() );
            }
        });

    }


}
