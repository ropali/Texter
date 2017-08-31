/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texter;

import static com.sun.glass.ui.Cursor.setVisible;
import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author ropali
 */
public class Find extends JDialog implements ActionListener {

    JTextField searchText, replaceText;
    JCheckBox cbCase, cbWhole;
    JRadioButton up, down;
    JLabel statusInfo;
    JFrame owner;
    JPanel north, center, south;
    boolean btnEnable = false;
    JButton nextBtn;

    boolean foundOne, isReplace;

    public Find(JFrame owner, boolean isReplace) {
        super(owner, true);
        this.isReplace = isReplace;

        north = new JPanel();
        center = new JPanel();
        south = new JPanel();

        if (isReplace) {
            setTitle("Find And Replace");
        } else {
            setTitle("Find");
            setFindPanel(north);
        }

        addComponent(center);
        statusInfo = new JLabel("Status Info : ");
        south.add(statusInfo);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                dispose();
            }
            
        });
        
        getContentPane().add(north, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);

    }

    private void setFindPanel(JPanel north) {
        nextBtn = new JButton("Find Next");
        nextBtn.addActionListener(this);
        nextBtn.setEnabled(btnEnable);

        searchText = new JTextField(20);
        searchText.addActionListener(this);

        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean state = (searchText.getDocument().getLength() > 0);
                nextBtn.setEnabled(true);
                foundOne = false;
            }

        });

        north.add(new JLabel("Find word"));
        north.add(searchText);
        north.add(nextBtn);

    }

    
    private void addComponent(JPanel center) {
        JPanel east = new JPanel();
        JPanel west = new JPanel();
        center.setLayout(new GridLayout(1, 2));
        east.setLayout(new GridLayout(2, 1));
        west.setLayout(new GridLayout(2, 1));

        cbCase = new JCheckBox("Match Case", true);
        cbWhole = new JCheckBox("Match Word", true);

        ButtonGroup group = new ButtonGroup();

        up = new JRadioButton("Search Up", false);
        down = new JRadioButton("Search Down", true);
        group.add(up);
        group.add(down);

        east.add(cbCase);
        east.add(cbWhole);

        east.setBorder(BorderFactory.createTitledBorder("Search Options:"));

        west.add(up);
        west.add(down);
        west.setBorder(BorderFactory.createTitledBorder("Search Direction:"));

        center.add(east);
        center.add(west);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(replaceText)) {
            validate();
        }
        process();
    }

    private void process() {
        if (isReplace) {
            statusInfo.setText("Replacing " + searchText.getText());
        } else {
            statusInfo.setText("Searching " + searchText.getText());
        }

        int caret = Texter.getArea().getCaretPosition();
        String word = getWord();
        String text = getAllText();

        caret = search(text, word, caret);

        if (caret < 0) {
            endResult(false, 0);

        }
    }

    private void endResult(boolean isReplaceAll, int tally) {
        String msg = "";
        if (isReplaceAll) {
            if(tally == 0){
                msg = searchText.getText() + " not found!";
            }
            else if(tally == 1){
                msg = "One change was made to "+searchText.getText();     
            }else{
                msg = ""+tally+" changes were made to "+searchText.getText();
            
            }

        } else {
            String str = "";
            if (isSearchDown()) {
                str = "Search Down";
            } else {
                str = "Search Up";
            }

            if (foundOne && !isReplace) {
                msg = "End of " + str + " for " + searchText.getText();
            } else {
                msg = "End of replace " + str + searchText.getText() + " with " + replaceText.getText();

            }
        }
        statusInfo.setText(msg);
    }

    private int search(String text, String word, int caret) {
        boolean found = false;
        int all = text.length();
        int check = word.length();
        int add = 0;

        if (isSearchDown()) {
            for (int i = caret + 1; i < (all - check); i++) {
                String temp = text.substring(i, (i + check));
                if (temp.equals(word)) {
                    if (isWholeWordSelect()) {
                        if (checkWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        } else { //not whole word
                            caret = i;
                            found = true;
                            break;
                        }
                    }
                }

            }
        } else {
            add = caret;
            for (int i = caret - 1; i >= check; i--) {
                add--;
                String temp = text.substring((i - check), i);
                if (temp.equals(word)) {
                    if (isWholeWordSelect()) {
                        if (checkWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        } else { //not whole word
                            caret = i;
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        Texter.getArea().setCaretPosition(0);
        if (found) {
            Texter.getArea().requestFocus();
            if (isSearchDown()) {
                Texter.getArea().select(caret, caret + check);
            } else {
                Texter.getArea().select(caret - check, caret);
            }
            foundOne = true;
            return caret;
        }

        return -1;
    }

    private boolean isSearchDown() {
        return down.isSelected();
    }

    private boolean isSearchUp() {
        return up.isSelected();
    }

    private boolean isWholeWordSelect() {
        return cbWhole.isSelected();
    }

    private String getAllText() {
        if (caseNotSelected()) {
            return Texter.getArea().getText().toLowerCase();
        }
        return Texter.getArea().getText();
    }

    private String getWord() {
        if (caseNotSelected()) {
            return searchText.getText().toLowerCase();
        }
        return searchText.getText();
    }

    private boolean caseNotSelected() {
        return !cbCase.isSelected();
    }

    private boolean checkWholeWord(int check, String text, int add, int caret) {
        int offsetLeft = (caret + add) - 1;
        int offsetRight = (caret + add) - check;

        if ((offsetLeft < 0) || offsetRight > text.length()) {
            return true;
        }
        return ((!Character.isLetterOrDigit(text.charAt(offsetLeft))) && (!Character.isLetterOrDigit(text.charAt(offsetRight))));
    }
    
    private void replaceAll(){
        String word = searchText.getText();
        String text = Texter.getArea().getText();
        String insert = replaceText.getText();
        
        StringBuffer sb = new StringBuffer(text);
        
        int diff = insert.length() - word.length();
        int offset = 0;
        int tally = 0;
        
        for (int i = 0; i < diff; i++) {
            String temp = text.substring(i, i + word.length());
            if(temp.equals(word) && checkWholeWord(word.length(), text, 0, i)){
                tally++;
                sb.replace(i+offset, offset+word.length(), insert);
                offset += diff;
            }
        }
        
        Texter.getArea().setText(sb.toString());
        endResult(true, tally);
        Texter.getArea().setCaretPosition(0);
    }
}
