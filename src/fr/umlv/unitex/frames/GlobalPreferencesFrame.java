/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.listeners.LanguageListener;

/**
 * This class describes a frame that offers to the user to set his preferences.
 *
 * @author Sébastien Paumier
 */
public class GlobalPreferencesFrame extends JInternalFrame {
    private final JTextField privateDirectory = new JTextField("");
    private final JTextField textFont = new JTextField("");
    private final JTextField concordanceFont = new JTextField("");
    private final JTextField htmlViewer = new JTextField("");
    JTextField morphologicalDicViewer = new JTextField("");
    private final JCheckBox rightToLeftForCorpusCheckBox = new JCheckBox(
            "Right to left rendering for text");
    private final JCheckBox rightToLeftForGraphsCheckBox = new JCheckBox(
            "Right to left rendering for graphs");
    private final JCheckBox semiticCheckBox = new JCheckBox(
            "Semitic language");
    private final JCheckBox charByCharCheckBox = new JCheckBox(
            "Analyze this language char by char");
    private final JCheckBox morphologicalUseOfSpaceCheckBox = new JCheckBox(
            "Enable morphological use of space");
    private final JTextField packageDirectory = new JTextField("");
    final JTextField lexicalPackageDirectory = new JTextField("");
    private final DefaultListModel morphoDicListModel = new DefaultListModel();
    private Preferences pref;
    private final JCheckBox mustLogCheckBox = new JCheckBox(
            "Produce log information in directory:");
    private final JTextField loggingDirectory = new JTextField("");

    private final JCheckBox svnMonitoring = new JCheckBox(
    "Auto-monitor graphs for SVN conflicts",true);
    private final JCheckBox onlyCosmetic = new JCheckBox(
    	    "Use --only-cosmetic option for GrfDiff3",false);

    private JRadioButton[] encodingButtons=new JRadioButton[Encoding.values().length];
    
    GlobalPreferencesFrame() {
        super("", true, true, false, false);
        setContentPane(constructPanel());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        Config.addLanguageListener(new LanguageListener() {
            public void languageChanged() {
                reset();
            }
        });
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(constructUpPanel(), BorderLayout.CENTER);
        panel.add(constructDownPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel constructUpPanel() {
        JPanel upPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.addTab("Directories", constructPage1());
        tabbedPane.addTab("Language & Presentation", constructPage2());
        tabbedPane.addTab("Morphological dictionaries", constructPage4());
        tabbedPane.addTab("SVN", constructSvnPage());
        tabbedPane.addTab("Encoding", constructEncodingPage());
        upPanel.add(tabbedPane);
        return upPanel;
    }

    private JPanel constructSvnPage() {
		JPanel p=new JPanel(null);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		svnMonitoring.setSelected(ConfigManager.getManager().svnMonitoring(null));
		p.add(svnMonitoring);
		onlyCosmetic.setSelected(ConfigManager.getManager().onlyCosmetic(null));
		p.add(onlyCosmetic);
		p.add(Box.createVerticalGlue());
		return p;
	}

    private JPanel constructEncodingPage() {
    	JPanel p=new JPanel(null);
		p.setBorder(BorderFactory.createTitledBorder("Select encoding to be used by Unitex:"));
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		ButtonGroup bg=new ButtonGroup();
		int i=0; 
		for (Encoding e:Encoding.values()) {
		encodingButtons[i]=new JRadioButton(e.toString(),e==ConfigManager.getManager().getEncoding(null));
			final Encoding e2=e;
			final int j=i;
			encodingButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (encodingButtons[j].isSelected()) {
						pref.encoding=e2;
					}
				}
			});
			p.add(encodingButtons[i]);
			bg.add(encodingButtons[i]);
			i++;
		}
		p.add(Box.createVerticalGlue());
		return p;
	}

	private JPanel constructDownPanel() {
        JPanel downPanel = new JPanel(new BorderLayout());
        JPanel tmp = new JPanel(new GridLayout(1, 2));
        tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
        JPanel tmp1 = new JPanel(new BorderLayout());
        JPanel tmp2 = new JPanel(new BorderLayout());
        tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
        tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
        Action okAction = new AbstractAction("OK") {
            public void actionPerformed(ActionEvent arg0) {
            	pref.svnMonitoring=svnMonitoring.isSelected();
            	pref.onlyCosmetic=onlyCosmetic.isSelected();
                pref.semitic = semiticCheckBox.isSelected();
                pref.rightToLeftForText = rightToLeftForCorpusCheckBox.isSelected();
                pref.rightToLeftForGraphs = rightToLeftForGraphsCheckBox.isSelected();
                pref.info.rightToLeft = pref.rightToLeftForGraphs;
                if (htmlViewer.getText().equals(""))
                    pref.htmlViewer = null;
                else
                    pref.htmlViewer = new File(htmlViewer.getText());
                pref.morphologicalDic = getFileList(morphoDicListModel);
                pref.charByChar = charByCharCheckBox.isSelected();
                pref.morphologicalUseOfSpace = morphologicalUseOfSpaceCheckBox
                        .isSelected();
                if (packageDirectory.getText().equals(""))
                    pref.graphRepositoryPath = null;
                else {
                    File f = new File(packageDirectory.getText());
                    if (!f.exists()) {
                        JOptionPane.showMessageDialog(null,
                                "The graph repository\ndoes not exist.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!f.isDirectory()) {
                        JOptionPane
                                .showMessageDialog(
                                        null,
                                        "The path given for the graph repository\n is not a directory path.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    pref.graphRepositoryPath = f;
                }

                if (loggingDirectory.getText().equals("") && mustLogCheckBox.isSelected()) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "Cannot log in an empty directory path.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String logDir = loggingDirectory.getText();
                File f = new File(logDir);
                if (f.exists() && !f.isDirectory()) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "The path given for the graph repository\n is not a directory path.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!f.exists()) {
                    f.mkdir();
                }
                pref.mustLog = mustLogCheckBox.isSelected();
                pref.loggingDir = f;
                ConfigManager.getManager().savePreferences(pref,null);
                /* We save the user directory */
                if (!privateDirectory.getText().equals("")) {
                    File rep = new File(privateDirectory.getText());
                    if (!rep.equals(Config.getUserDir())) {
                        File userFile;
                        if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM) {
                            userFile = new File(Config.getUnitexDir(), "Users");
                            userFile = new File(userFile, Config.getUserName()
                                    + ".cfg");
                        } else {
                            userFile = new File(System.getProperty("user.home"), ".unitex.cfg");
                        }
                        if (userFile.exists())
                            userFile.delete();
                        try {
                            userFile.createNewFile();
                            FileOutputStream stream = new FileOutputStream(userFile);
                            stream.write(rep.getAbsolutePath().getBytes("UTF8"));
                            stream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        String message = "Your private Unitex directory is now:\n\n";
                        message = message + rep + "\n\n";
                        message = message
                                + "You must relaunch Unitex to take this change into account.";
                        JOptionPane.showMessageDialog(null, message, "",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
                setVisible(false);
            }
        };
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        };
        JButton OK = new JButton(okAction);
        JButton CANCEL = new JButton(cancelAction);
        tmp1.add(OK);
        tmp2.add(CANCEL);
        tmp.add(tmp1);
        tmp.add(tmp2);
        downPanel.add(tmp, BorderLayout.EAST);
        return downPanel;
    }

    ArrayList<File> getFileList(DefaultListModel model) {
        ArrayList<File> list = new ArrayList<File>();
        for (int i = 0; i < model.size(); i++) {
            list.add((File) model.get(i));
        }
        return list;
    }

    private JComponent constructPage1() {
        JPanel page1 = new JPanel(new GridBagLayout());
        page1.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel label = new JLabel(
                "Private Unitex directory (where all user's data is to be stored):");
        privateDirectory.setEditable(false);
        privateDirectory.setBackground(Color.WHITE);
        Action privateDirAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setDialogTitle("Choose your private directory");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                    return;
                if (f.getSelectedFile().equals(Config.getUnitexDir())) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "You cannot choose the Unitex directory as your private one",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                privateDirectory.setText(f.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setPrivateDirectory = new JButton(privateDirAction);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        page1.add(label, gbc);
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        page1.add(privateDirectory, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        page1.add(setPrivateDirectory, gbc);
        JLabel label2 = new JLabel("Graph repository:");
        packageDirectory.setBackground(Color.WHITE);
        Action packageDirAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setDialogTitle("Choose your graph package directory");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                packageDirectory.setText(f.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setPackageDirectory = new JButton(packageDirAction);
        gbc.insets = new Insets(20, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        page1.add(label2, gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        page1.add(packageDirectory, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        page1.add(setPackageDirectory, gbc);

        ///////////////////////
        /*
        JLabel lexicalRepositoryLabel = new JLabel("Lexical repository:");
        lexicalPackageDirectory.setBackground(Color.WHITE);
        Action lexicalDirAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setDialogTitle("Choose your lexical package directory");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                lexicalPackageDirectory.setText(f.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setLexicalPackageDirectory = new JButton(lexicalDirAction);
        gbc.insets = new Insets(20, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        page1.add(lexicalRepositoryLabel, gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        page1.add(lexicalPackageDirectory, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        page1.add(setLexicalPackageDirectory, gbc);
		*/
        loggingDirectory.setEditable(false);
        loggingDirectory.setBackground(Color.WHITE);
        Action loggingDirAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setDialogTitle("Choose your logging directory");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                    return;
                loggingDirectory.setText(f.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setLoggingDirectory = new JButton(loggingDirAction);
        gbc.insets = new Insets(20, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        page1.add(mustLogCheckBox, gbc);
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        page1.add(loggingDirectory, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        page1.add(setLoggingDirectory, gbc);
        JButton clearLogs = new JButton("Clear all logs");
        clearLogs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (loggingDirectory.getText().equals("")) {
                    return;
                }
                int n = JOptionPane.showConfirmDialog(UnitexFrame.mainFrame,
                        "Are you sure you want to clear logs ?", "", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    File dir = new File(loggingDirectory.getText());
                    FileUtil.removeFile(new File(dir, "*.ulp"));
                    FileUtil.removeFile(new File(dir, "unitex_logging_parameters_count.txt"));
                }
            }
        });
        page1.add(clearLogs, gbc);
        gbc.weighty = 1;
        page1.add(new JPanel(null), gbc);
        return page1;
    }

    private JPanel constructPage2() {
        JPanel page2 = new JPanel(null);
        page2.setLayout(new BoxLayout(page2, BoxLayout.Y_AXIS));
        textFont.setEnabled(false);
        concordanceFont.setEnabled(false);
        textFont.setDisabledTextColor(Color.black);
        concordanceFont.setDisabledTextColor(Color.black);
        page2.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel yuyu = new JPanel(new GridLayout(5, 1));
        yuyu.add(charByCharCheckBox);
        yuyu.add(morphologicalUseOfSpaceCheckBox);
        yuyu.add(semiticCheckBox);
        yuyu.add(rightToLeftForCorpusCheckBox);
        yuyu.add(rightToLeftForGraphsCheckBox);
        page2.add(yuyu);
        JPanel tmp = new JPanel(new GridLayout(2, 1));
        tmp.setPreferredSize(new Dimension(180, 60));
        tmp.add(new JLabel("Text Font:"));
        JPanel tmp2 = new JPanel(new BorderLayout());
        tmp2.add(textFont, BorderLayout.CENTER);
        Action textFontAction = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                FontInfo i = InternalFrameManager.getManager().newFontDialog(
                        pref.textFont);
                if (i != null) {
                    pref.textFont = i;
                    textFont
                            .setText(" " + i.font.getFontName() + "  " + i.size);
                }
            }
        };
        JButton setTextFont = new JButton(textFontAction);
        tmp2.add(setTextFont, BorderLayout.EAST);
        tmp.add(tmp2);
        page2.add(tmp);
        JPanel tmp_ = new JPanel(new GridLayout(2, 1));
        tmp_.setPreferredSize(new Dimension(180, 60));
        tmp_.add(new JLabel("Concordance Font:"));
        JPanel tmp2_ = new JPanel(new BorderLayout());
        tmp2_.add(concordanceFont, BorderLayout.CENTER);
        Action concord = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                FontInfo i = InternalFrameManager.getManager().newFontDialog(
                        pref.concordanceFont);
                if (i != null) {
                    pref.concordanceFont = i;
                    concordanceFont.setText(" " + i.font.getFontName() + "  "
                            + i.size);
                }
            }
        };
        JButton setConcordanceFont = new JButton(concord);
        tmp2_.add(setConcordanceFont, BorderLayout.EAST);
        tmp_.add(tmp2_);
        page2.add(tmp_);
        JPanel htmlViewerPanel = new JPanel(new GridLayout(2, 1));
        htmlViewerPanel.setPreferredSize(new Dimension(180, 60));
        htmlViewerPanel.add(new JLabel("Html Viewer:"));
        JPanel tmp3_ = new JPanel(new BorderLayout());
        Action html = new AbstractAction("Set...") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setDialogTitle("Choose your html viewer");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                    return;
                htmlViewer.setText(f.getSelectedFile().getAbsolutePath());
            }
        };
        JButton setHtmlViewer = new JButton(html);
        tmp3_.add(htmlViewer, BorderLayout.CENTER);
        tmp3_.add(setHtmlViewer, BorderLayout.EAST);
        htmlViewerPanel.add(tmp3_);
        page2.add(htmlViewerPanel);
        JPanel graph = new JPanel();
        FlowLayout l = (FlowLayout) (graph.getLayout());
        l.setAlignment(FlowLayout.LEFT);
        JButton graphConfig = new JButton("Graph configuration");
        graphConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphPresentationInfo i = InternalFrameManager.getManager()
                        .newGraphPresentationDialog(pref.info, false);
                if (i != null) {
                    pref.info = i;
                }
            }
        });
        graph.add(graphConfig);
        page2.add(graph);
        return page2;
    }

    /**
     * Refreshes the frame.
     */
    void refresh() {
        textFont.setText("" + pref.textFont.font.getFontName() + "  "
                + pref.textFont.size + "");
        concordanceFont.setText("" + pref.concordanceFont.font.getName() + "  "
                + pref.concordanceFont.size + "");
        if (pref.htmlViewer == null) {
            htmlViewer.setText("");
        } else {
            htmlViewer.setText(pref.htmlViewer.getAbsolutePath());
        }
        semiticCheckBox.setSelected(pref.semitic);
        rightToLeftForCorpusCheckBox.setSelected(pref.rightToLeftForText);
        rightToLeftForGraphsCheckBox.setSelected(pref.rightToLeftForGraphs);
        charByCharCheckBox.setSelected(pref.charByChar);
        morphologicalUseOfSpaceCheckBox
                .setSelected(pref.morphologicalUseOfSpace);
        if (pref.graphRepositoryPath == null) {
            packageDirectory.setText("");
        } else {
            packageDirectory.setText(pref.graphRepositoryPath.getAbsolutePath());
        }
        mustLogCheckBox.setSelected(pref.mustLog);
        if (pref.loggingDir == null) {
            loggingDirectory.setText("");
        } else {
            loggingDirectory.setText(pref.loggingDir.getAbsolutePath());
        }
        morphoDicListModel.clear();
        ArrayList<File> dictionaries=ConfigManager.getManager().morphologicalDictionaries(null);
        if (dictionaries != null) {
            for (File f : dictionaries) {
                morphoDicListModel.addElement(f);
            }
        }
        for (int i=0;i<Encoding.values().length;i++) {
        	encodingButtons[i].setSelected(pref.encoding==Encoding.values()[i]);
        }
    }

    private JPanel constructPage4() {
        JPanel p = new JPanel(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel p_ = new JPanel(new GridLayout(2, 1));
        p_
                .add(new JLabel(
                        "Choose the .bin dictionaries to use in Locate's morphological"));
        p_.add(new JLabel("mode:"));
        p.add(p_);
        JPanel p2 = new JPanel(new BorderLayout());
        morphoDicListModel.clear();
        ArrayList<File> dictionaries=ConfigManager.getManager().morphologicalDictionaries(null);
        if (dictionaries != null) {
            for (File f : dictionaries) {
                morphoDicListModel.addElement(f);
            }
        }
        final JList list = new JList(morphoDicListModel);
        list.setPreferredSize(new Dimension(200, 400));
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList l,
                                                          Object value, int index, boolean isSelected1,
                                                          boolean cellHasFocus) {
                File f = (File) value;
                return super.getListCellRendererComponent(l, f
                        .getAbsolutePath(), index, isSelected1, cellHasFocus);
            }
        });
        JScrollPane scroll = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        p2.add(scroll);
        JPanel down = new JPanel(new BorderLayout());
        JPanel tmp = new JPanel(new GridLayout(1, 2));
        tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
        JPanel tmp1 = new JPanel(new BorderLayout());
        JPanel tmp2 = new JPanel(new BorderLayout());
        tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
        tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
        Action addAction = new AbstractAction("Add") {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser f = new JFileChooser();
                f.setMultiSelectionEnabled(true);
                f.addChoosableFileFilter(new PersonalFileFilter("bin",
                        "Binary dictionary"));
                f.setDialogTitle("Choose your morphological dictionaries");
                f.setDialogType(JFileChooser.OPEN_DIALOG);
                if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File[] files = f.getSelectedFiles();
                if (files == null)
                    return;
                for (File file : files) {
                    morphoDicListModel.addElement(file);
                }
            }
        };
        Action removeAction = new AbstractAction("Remove") {
            public void actionPerformed(ActionEvent arg0) {
                int[] indices = list.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    morphoDicListModel.remove(indices[i]);
                }
            }
        };
        JButton addButton = new JButton(addAction);
        JButton removeButton = new JButton(removeAction);
        tmp1.add(addButton);
        tmp2.add(removeButton);
        tmp.add(tmp1);
        tmp.add(tmp2);
        down.add(tmp, BorderLayout.EAST);
        p2.add(down, BorderLayout.SOUTH);
        p.add(p2);
        return p;
    }

    void reset() {
        pref = ConfigManager.getManager().getPreferences(null);
        setTitle("Preferences for " + Config.getCurrentLanguage());
        privateDirectory.setText(Config.getUserDir().getAbsolutePath());
        refresh();
    }
}
