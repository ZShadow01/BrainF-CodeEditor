import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

import java.awt.event.KeyEvent;
import java.io.*;

public class MainScreen {
    private JFrame frame;
    private JTextArea textArea;

    private JMenuBar menuBar;
    private JLabel currentFileLabel;
    private String currentFile;
    private String originalText;
    private final String title = "Brainf*ck Code Editor - Basic";

    public MainScreen() {
        this.currentFile = null;
        this.originalText = null;
        this.initialize();
    }

    private void initialize() {
        this.frame = new JFrame(this.title);
        BorderLayout layout = new BorderLayout();
        layout.setHgap(10);
        this.frame.setLayout(layout);

        // Menu bar
        this.menuBar = new JMenuBar();
        this.initializeMenuBar();
        this.frame.setJMenuBar(this.menuBar);

        // File label
        JPanel panel = new JPanel(new FlowLayout());
        this.currentFileLabel = new JLabel(this.currentFile);
        panel.add(this.currentFileLabel);
        this.frame.add(panel, BorderLayout.NORTH);

        // Text area
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        TextLineNumber textLineNumber = new TextLineNumber(this.textArea);
        this.textArea.getDocument().addDocumentListener(this.checkProgramChanges());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(textLineNumber);
        this.frame.add(scrollPane, BorderLayout.CENTER);

        this.checkChanges();

        // "Margin"
        this.frame.add(new JPanel(), BorderLayout.EAST);
        this.frame.add(new JPanel(), BorderLayout.WEST);
        this.frame.add(new JPanel(), BorderLayout.SOUTH);

        this.frame.pack();
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(500, 500);
    }

    private void initializeMenuBar() {
        // Menus
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenu runMenu = new JMenu("Run");
        runMenu.setMnemonic(KeyEvent.VK_R);

        // New
        JMenuItem newMenuItem = new JMenuItem("New File");
        newMenuItem.addActionListener(e -> this.doNewProgram());
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.setActionCommand("New");

        fileMenu.add(newMenuItem);

        fileMenu.addSeparator();

        // Save
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(e -> this.doSaveProgram());
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.setActionCommand("Save");

        fileMenu.add(saveMenuItem);

        // Save as
        JMenuItem saveAsMenuItem = new JMenuItem("Save as");
        saveAsMenuItem.addActionListener(e -> this.doSaveAsProgram());
        saveAsMenuItem.setMnemonic(KeyEvent.VK_S);
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsMenuItem.setActionCommand("SaveAs");

        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        // Open
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(e -> this.doOpenProgram());
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setActionCommand("Open");

        fileMenu.add(openMenuItem);

        fileMenu.addSeparator();

        // Close
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> this.terminate());
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setActionCommand("Exit");

        fileMenu.add(exitMenuItem);

        // Run
        JMenuItem runMenuItem = new JMenuItem("Run");
        runMenuItem.addActionListener(e -> this.doRunProgram());
        runMenuItem.setMnemonic(KeyEvent.VK_N);
        runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK));
        runMenuItem.setActionCommand("Run");

        runMenu.add(runMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(runMenu);
    }

    private void doNewProgram() {
        FileDialog fileDialog = new FileDialog(this.frame);
        fileDialog.setVisible(true);
        String path = fileDialog.getFile();

        if (path == null) {
            return;
        }

        if (!path.endsWith(".bf")) {
            path = path + ".bf";
        }

        File file = new File(path);
        try {
            if (file.createNewFile()) {
                this.updateCurrentFile(path);
            } else {
                // File already exists
                System.out.println("File already exists");
                JOptionPane.showMessageDialog(this.frame, "This file already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doSaveProgram() {
        if (this.currentFile == null) {
            this.doSaveAsProgram();
        } else {
            try {
                File file = new File(this.currentFile);
                FileWriter writer = new FileWriter(file);
                writer.write(this.textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.originalText = this.textArea.getText();
        this.checkChanges();
    }

    private void doSaveAsProgram() {
        FileDialog fileDialog = new FileDialog(this.frame);
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setVisible(true);
        String path = fileDialog.getFile();

        if (path == null) {
            return;
        }

        if (!path.endsWith(".bf")) {
            path = path + ".bf";
        }

        try {
            File file = new File(path);

            FileWriter writer = new FileWriter(file);
            writer.write(this.textArea.getText());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.updateCurrentFile(path);
    }

    private void doOpenProgram() {
        FileDialog fileDialog = new FileDialog(this.frame);
        fileDialog.setFilenameFilter((dir, name) -> name.endsWith(".bf"));
        fileDialog.setFile("*.bf");
        fileDialog.setVisible(true);
        String path = fileDialog.getFile();

        if (path == null) {
            return;
        }

        try {
            File file = new File(path);

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            this.textArea.setText(builder.toString());
        } catch (IOException err) {
            err.printStackTrace();
        }

        this.originalText = this.textArea.getText();
        this.checkChanges();
        this.updateCurrentFile(path);
    }

    private void doRunProgram() {
        if (this.currentFile == null) {
            this.doSaveProgram();

            if (this.currentFile == null) {
                return;
            }
        }

        try {
            Runtime.getRuntime().exec(new String[] { "cmd", "/c", "Start", "CBrainFInterpreter.exe", this.currentFile });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkChanges() {
        String currentText = this.textArea.getText();
        if (!currentText.equals(this.originalText)) {
            this.currentFileLabel.setText(this.currentFile == null ? "[Not saved]" : this.currentFile + " [Not saved]");
        } else {
            this.currentFileLabel.setText(this.currentFile);
        }
    }

    private DocumentListener checkProgramChanges() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkChanges();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkChanges();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkChanges();
            }
        };
    }

    private void updateCurrentFile(String newFile) {
        this.currentFile = newFile;
        this.currentFileLabel.setText(newFile);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public void terminate() {
        this.frame.setVisible(false);
        this.frame.dispose();
    }
}
