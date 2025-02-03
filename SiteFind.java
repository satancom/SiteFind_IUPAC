/*
 * Modified version of SiteFind
 * Original Copyright (C) 2005 Paul M Evans
 * Modifications by Max Saltykov, 2025
 * Changes:
 * - Now supports IUPAC codes in restriction enzymes' cut sites
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SiteFind extends JFrame implements NextPanelListener {
    static int numPanels = 8;
    NextPanel panelList[];
    int curPanel;
    Map<String, RestrictionEnzyme> enzymeList;

    public SiteFind() {
        setTitle("SiteFind");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelList = new NextPanel[numPanels];
        curPanel = 0;
        enzymeList = new TreeMap<>();

        panelList[0] = new WelcomePanel(this);
        panelList[1] = new WildTypeDNAPanel(this);
        panelList[2] = new FrameSelectionPanel(this);
        panelList[3] = new MutantResiduePanel(this);
        panelList[4] = new RestrictionListPanel(this, enzymeList);
        panelList[5] = new ResultsFormatPanel(this);
        panelList[6] = new SearchStatusPanel(this, enzymeList, getWidth());
        panelList[7] = new ResultsPanel(this);

        add(panelList[0]);
        setVisible(true);
    }

    public void nextClicked(String results1, String results2, boolean defaultOutputFormat) {
        getContentPane().remove(panelList[curPanel]);
        if (++curPanel >= numPanels) curPanel = 0;
        getContentPane().add(panelList[curPanel]);
        getContentPane().validate();
        getContentPane().repaint();
        panelList[curPanel].hasFocus(results1, results2, defaultOutputFormat);
    }

    public void backClicked() {
        getContentPane().remove(panelList[curPanel]);
        if (--curPanel < 0) curPanel = numPanels - 1;
        getContentPane().add(panelList[curPanel]);
        getContentPane().validate();
        getContentPane().repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SiteFind());
    }
}
