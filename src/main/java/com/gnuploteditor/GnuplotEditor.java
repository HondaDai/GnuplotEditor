package com.gnuploteditor;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

/**
 * Created by HondaDai on 2015/03/11.
 */
public class GnuplotEditor extends JFrame {



    private JTextArea input;
    private JLabel output;
    private int updateRate = 100; //ms
    private boolean updateReq = false;
    private Timer updateTimer;

    private File source;
    private File dist;

    private DefaultExecutor executor = new DefaultExecutor();;
    private String compileMsg;

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;

    private GnuplotEditor() {

        this.setTitle("GnuplotEditor");
        this.setSize(800, 600);
        this.setLayout(new GridLayout(1, 2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input = new JTextArea();
        this.add(input);

        output = new JLabel();
        this.add(output);

        try {
            source = File.createTempFile("gnuploteditor", ".tmp");
            dist = File.createTempFile("gnuploteditor", ".png");
//            source = new File(Paths.get(System.getProperty("user.home"), ".gnuploteditor.tmp").toString());
//            dist = new File(Paths.get(System.getProperty("user.home"), ".gnuploteditor.png").toString());
//
//            if (!source.exists()) source.createNewFile();
//            if (!dist.exists()) dist.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (source != null && source.exists() ) source.delete();
                if (dist != null && dist.exists() ) dist.delete();
            }
        });

        initUpdateListener();
        initUpdateTimer();
        initMenu();

        updateRate = UserPref.getInt("UpdateRate", updateRate);
//        openLogWindow(UserPref.getBoolean("OpenLogWindow", false));

        input.setText("\n# GnuplotEditor v1.0\n\nset label \"Welcome to GnuplotEditor v1.0\" at 0,2 center\nset yrange [-2:3]\nplot sin(x), cos(x)\n");
        update();

        this.setVisible(true);
    }



    public void initUpdateListener() {

        input.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                runUpdate();
            }
            public void removeUpdate(DocumentEvent e) {
                runUpdate();
            }
            public void insertUpdate(DocumentEvent e) {
                runUpdate();
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                runUpdate();
            }
        });


    }

    public void initMenu() {
        this.addMouseListener(new MouseAdapter(){
        public void mousePressed(MouseEvent e){
            if (e.isPopupTrigger())
                pop(e);
        }

        public void mouseReleased(MouseEvent e){
            if (e.isPopupTrigger())
                pop(e);
        }

        private void pop(MouseEvent e){
            Menu menu = new Menu();
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    });

    }

    public void initUpdateTimer() {

        if (updateTimer != null) {
            updateTimer.stop();
        }

        updateTimer = new Timer(updateRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                updateReq = false;
            }
        });
        updateTimer.setRepeats(false);
    }


    private boolean runUpdate() {

        updateReq = true;
        if ( !updateTimer.isRunning() ) {
            updateTimer.start();
        }

        return true;
    }

    private void update() {
        if ( updateResult() ) {
            ImageIcon icon = readAndScaleImg(output.getWidth(), output.getHeight(), dist.getAbsolutePath());
            if (icon != null) {
                output.setIcon(icon);
            }
        }
    }

    private boolean updateResult() {

        try {

            String pngPath = dist.getAbsolutePath();
            if (isWindows) {
                pngPath = pngPath.replace("\\", "\\\\");
            }

            String text = "";
            text += "set terminal \"png\" size "+input.getWidth()+","+input.getHeight()+" \n";
            text += "set output \""+pngPath+"\" \n";
            text += input.getText();

            FileWriter fw = new FileWriter(source);
            fw.write(text);
            fw.close();

        } catch (IOException e) {
//            e.printStackTrace();
        }

        String line = "gnuplot -c "+source.getAbsolutePath();
        CommandLine cmdLine = CommandLine.parse(line);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(outputStream));
        try {
            int exitValue = executor.execute(cmdLine);
            if (exitValue == 1) return false;

        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            compileMsg = outputStream.toString();

            LogWindow.instance.setOutput(getCompileMsg());
        }

        return true;
    }


    private ImageIcon readAndScaleImg(int width, int height, String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
            if (img == null) return null;
            return new ImageIcon(img.getScaledInstance(width, width*img.getHeight()/img.getWidth(), Image.SCALE_SMOOTH));
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }


    public File getWorkingDirectory() {
        return executor.getWorkingDirectory();
    }

    public void setWorkingDirectory(File path) {
        executor.setWorkingDirectory(path);
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(int rate) {
        updateRate = rate;
        UserPref.putInt("UpdateRate", updateRate);
        initUpdateTimer();
    }

    public String getCompileMsg() {
        if (compileMsg.length() == 0) return source.getAbsolutePath()+"Update Success!";
        return compileMsg.replace("\""+source.getAbsolutePath()+"\", ", "");
    }

    public void exportImage() {
        try {
            Path target = Paths.get(this.getWorkingDirectory().getAbsolutePath(), "GnuplotEditor_" + System.currentTimeMillis()+".png");
            Files.copy(Paths.get(dist.getAbsolutePath()), target, StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(null, "Export Image to " + target.toAbsolutePath().toString(), "Export Success", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't Export Image.", "Export Fail", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openLogWindow(boolean b) {
        LogWindow.instance.setVisible( b );
//        UserPref.putBoolean("OpenLogWindow", LogWindow.instance.isVisible());
    }


    public static GnuplotEditor instance;
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        instance = new GnuplotEditor();

    }

}
