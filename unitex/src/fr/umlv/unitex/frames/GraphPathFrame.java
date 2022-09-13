/**
 * Unitex
 *
 * Copyright (C) 2001-2021 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Fst2ListCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class GraphPathFrame extends JInternalFrame implements
  MultiInstanceFrameFactoryObserver<GraphFrame> {
    private List<GraphFrame> graphFrames;
    private GraphFrame currentFrame;
    MultiCommands preprocessCommands;
    Boolean flattenMode = false;
    String flattenDepth = "10";
		
    final ItemListener flattenCheckBoxListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if ( flattenCheckbox.isSelected() ) {
                            flattenOptionButton.setEnabled(true);
                    } else {
                            flattenOptionButton.setEnabled(false);
                    }
            }
    };
    
    final ItemListener maxSeqListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if ( maxSeqCheckbox.isSelected() ) {
                            maxSeqSpinner.setEnabled(true);
                    } else {
                            maxSeqSpinner.setEnabled(false);
                    }
            }
    };
    
    ListDataListener listListener = new ListDataListener() {
		@Override
		public void intervalRemoved(ListDataEvent e) {
			/* */
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			final int n = outputArea.getModel().getSize();
			setTitle(n + " line" + (n > 1 ? "s" : ""));
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			/* */
		}
	};

    /**
     * Creates new form GPF
     */
    public GraphPathFrame() {
        currentFrame = GlobalProjectManager.search(null)
          .getFrameManagerAs(InternalFrameManager.class)
          .getCurrentFocusedGraphFrame();
        graphFrames = GlobalProjectManager.search(null)
          .getFrameManagerAs(InternalFrameManager.class)
          .getGraphFrames();
        setGraphPathFrame();
    }

    private void setGraphPathFrame() {
        if (graphFrames.isEmpty()) {
            throw new AssertionError("graphFrames should not be empty in construction of GraphPathFrame");
        }
        if (currentFrame == null) {
            currentFrame = graphFrames.get(0);
        }
        initComponents();
        fillComboBox();
        setOutputFileDefaultName(currentFrame);
    }

    /**
     * This method return an empty string if the selected graph is null or unsaved, otherwise the selected graph name
     * */
    private String getSelectedGraphName() {
        GraphFrame graphFrame = (GraphFrame) inputGraphName.getSelectedItem();
        if (graphFrame == null || graphFrame.getGraph() == null) {
            return "";
        }
        return FileUtil.getFileNameWithoutExtension(graphFrame.getGraph());
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        graphFileLabel = new javax.swing.JLabel();
        outputFileLabel = new javax.swing.JLabel();
        inputGraphName = new javax.swing.JComboBox<GraphFrame>();
        outputFileName = new javax.swing.JTextField();
        setFileButton = new javax.swing.JButton();
        optionSeparator = new javax.swing.JSeparator();
        optionLabel = new javax.swing.JLabel();
        outputsLabel = new javax.swing.JLabel();
        ignoreOutputsButton = new javax.swing.JRadioButton();
        separateOutputsButton = new javax.swing.JRadioButton();
        alternateOutputsButton = new javax.swing.JRadioButton();
        exploreLabel = new javax.swing.JLabel();
        exploreRecButton = new javax.swing.JRadioButton();
        exploreIndepButton = new javax.swing.JRadioButton();
        maxSeqCheckbox = new javax.swing.JCheckBox();
        maxSeqSpinner = new javax.swing.JSpinner();
        flattenCheckbox = new javax.swing.JCheckBox();
        flattenOptionButton = new javax.swing.JButton();
        checkLoopsCheckbox = new javax.swing.JCheckBox();
        resultLabel = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        resultSeparator = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        outputArea = new fr.umlv.unitex.text.BigTextList();
        makeDicCheckBox = new javax.swing.JCheckBox();

        typeDictionaryGraphButtonGroup = new javax.swing.ButtonGroup();
        desiredOutputButtonGroup = new javax.swing.ButtonGroup();
        typeDictionaryGraphLabel = new javax.swing.JLabel();
        morphologicalButton = new javax.swing.JRadioButton();
        notMorphologicalButton = new javax.swing.JRadioButton();
        desiredOutputLabel = new javax.swing.JLabel();
        multidelafButton = new javax.swing.JRadioButton();
        delafButton = new javax.swing.JRadioButton();
        configurationFileLabel = new javax.swing.JLabel();
        configurationFileName = new javax.swing.JTextField();

        typeDictionaryGraphButtonGroup.add(morphologicalButton);
        typeDictionaryGraphButtonGroup.add(notMorphologicalButton);

        desiredOutputButtonGroup.add(multidelafButton);
        desiredOutputButtonGroup.add(delafButton);

        typeDictionaryGraphLabel.setText("Type of dictionary-graph:");
        typeDictionaryGraphLabel.setEnabled(false);

        morphologicalButton.setText("Morphological");
        morphologicalButton.setEnabled(false);
        morphologicalButton.addActionListener(e -> morphologicalButtonActionPerformed(e));

        notMorphologicalButton.setText("Not morphological");
        notMorphologicalButton.setSelected(true);
        notMorphologicalButton.setEnabled(false);
        notMorphologicalButton.addActionListener(e -> notMorphologicalButtonActionPerformed(e));

        desiredOutputLabel.setText("Desired output:");
        desiredOutputLabel.setEnabled(false);

        multidelafButton.setText("Multidelaf");
        multidelafButton.setEnabled(false);
        multidelafButton.setSelected(true);

        multidelafButton.addActionListener(e -> multidelafButtonActionPerformed(e));

        delafButton.setText("DELAF");
        delafButton.setEnabled(false);
        delafButton.addActionListener(e -> delafButtonActionPerformed(e));

        configurationFileLabel.setText("Configuration file:");
        configurationFileLabel.setEnabled(false);

        String pathFileName =
            Config.getUserCurrentLanguageDir().getAbsolutePath();
        String defaultConfigFileName = Paths.get(pathFileName, "/Dela/multi2delafconfig.txt")
            .toString();

        configurationFileName.setText(defaultConfigFileName);
        configurationFileName.setName(""); // NOI18N
        configurationFileName.setPreferredSize(new java.awt.Dimension(496, 25));
        configurationFileName.setEnabled(false);

        setConfigurationFile = new javax.swing.JButton();
        setConfigurationFile.setText("Set File");
        setConfigurationFile.setEnabled(false);
        setConfigurationFile.addActionListener(e -> setConfigurationFileActionPerformed(e));

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Explore graph paths");
        setToolTipText("");

        graphFileLabel.setText("Graph:");

        outputFileLabel.setText("Output file:");

        inputGraphName.setEditable(false);
        inputGraphName.setPreferredSize(new java.awt.Dimension(70, 25));
        inputGraphName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputGraphNameActionPerformed(evt);
            }
        });

        outputFileName.setText("jTextField1");
        outputFileName.setName(""); // NOI18N
        outputFileName.setPreferredSize(new java.awt.Dimension(70, 25));

        setFileButton.setText("Set File");
        setFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setFileButtonActionPerformed(evt);
            }
        });

        optionLabel.setText("Options");

        outputsLabel.setText("Outputs:");

        buttonGroup1.add(ignoreOutputsButton);
        ignoreOutputsButton.setSelected(true);
        ignoreOutputsButton.setText("Ignore");

        buttonGroup1.add(separateOutputsButton);
        separateOutputsButton.setText("Separate inputs and outputs");

        buttonGroup1.add(alternateOutputsButton);
        alternateOutputsButton.setText("Alternate inputs and outputs");

        exploreLabel.setText("Explore subraphs:");

        buttonGroup2.add(exploreRecButton);
        exploreRecButton.setSelected(true);
        exploreRecButton.setText("Recursively");
        exploreRecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreRecButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(exploreIndepButton);
        exploreIndepButton.setText("Independently, printing names of called subgraphs");
        exploreIndepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreIndepButtonActionPerformed(evt);
            }
        });

        maxSeqCheckbox.setText("Max sequences:");
        maxSeqCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxSeqCheckboxActionPerformed(evt);
            }
        });

        maxSeqSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        maxSeqSpinner.setEnabled(false);
        maxSeqSpinner.setValue(50);

        flattenCheckbox.setText("Flatten graphs");
        flattenCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenCheckboxActionPerformed(evt);
            }
        });

        flattenOptionButton.setText("Options");
        flattenOptionButton.setEnabled(false);
        flattenOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenOptionButtonActionPerformed(evt);
            }
        });

        checkLoopsCheckbox.setText("Check for loops");
        checkLoopsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkLoopsCheckboxActionPerformed(evt);
            }
        });

        resultLabel.setText("Results");

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        makeDicCheckBox.setText("Process as dictionary-graph");
        makeDicCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeDicCheckboxActionPerformed(evt);
            }
        });

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(outputArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                            Short.MAX_VALUE)
                        .addComponent(optionSeparator)
                        .addComponent(optionLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(graphFileLabel)
                                    .addComponent(outputFileLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup()
                                            .addComponent(outputFileName,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                            .addGap(18, 18, 18)
                                            .addComponent(setFileButton))
                                    .addComponent(inputGraphName,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 682,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(244, 244, 244)
                            .addComponent(configurationFileName)
                            .addGap(18, 18, 18)
                            .addComponent(setConfigurationFile))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(outputsLabel)
                                    .addComponent(exploreLabel)
                                    .addComponent(maxSeqCheckbox)
                                    .addComponent(makeDicCheckBox))
                            .addGap(30, 30, 30)
                            .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ignoreOutputsButton)
                                    .addComponent(exploreRecButton)
                                    .addComponent(maxSeqSpinner,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(morphologicalButton)
                                    .addComponent(multidelafButton)
                            )
                            .addGap(38, 38, 38)
                            .addGroup(layout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(exploreIndepButton)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(separateOutputsButton)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(flattenCheckbox)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(flattenOptionButton))
                                        .addComponent(notMorphologicalButton)
                                        .addComponent(delafButton)
                                    )
                                    .addGap(40, 40, 40)
                                    .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(checkLoopsCheckbox)
                                        .addComponent(alternateOutputsButton)))))
                        .addComponent(resultLabel)
                        .addComponent(resultSeparator)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(helpButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancelButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(runButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(50)
                            .addComponent(typeDictionaryGraphLabel))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(50)
                            .addComponent(desiredOutputLabel))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(50)
                            .addComponent(configurationFileLabel))

                )
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(graphFileLabel)
                    .addComponent(inputGraphName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputFileLabel)
                    .addComponent(outputFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setFileButton))
                .addGap(16, 16, 16)
                .addComponent(optionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputsLabel)
                    .addComponent(ignoreOutputsButton)
                    .addComponent(separateOutputsButton)
                    .addComponent(alternateOutputsButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exploreLabel)
                    .addComponent(exploreRecButton)
                    .addComponent(exploreIndepButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxSeqCheckbox)
                    .addComponent(maxSeqSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flattenCheckbox)
                    .addComponent(flattenOptionButton)
                    .addComponent(checkLoopsCheckbox))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(makeDicCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeDictionaryGraphLabel)
                    .addComponent(morphologicalButton)
                    .addComponent(notMorphologicalButton)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(desiredOutputLabel)
                    .addComponent(multidelafButton)
                    .addComponent(delafButton)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(configurationFileLabel)
                    .addComponent(configurationFileName)
                    .addComponent(setConfigurationFile)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(helpButton)
                    .addComponent(cancelButton)
                    .addComponent(runButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void maxSeqCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxSeqCheckboxActionPerformed
        if ( maxSeqCheckbox.isSelected() ) {
                maxSeqSpinner.setEnabled(true);
        } else {
                maxSeqSpinner.setEnabled(false);
        }
    }//GEN-LAST:event_maxSeqCheckboxActionPerformed

    private void flattenCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenCheckboxActionPerformed
        if ( flattenCheckbox.isSelected() ) {
                flattenOptionButton.setEnabled(true);
        } else {
                flattenOptionButton.setEnabled(false);
        }
    }//GEN-LAST:event_flattenCheckboxActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void exploreRecButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreRecButtonActionPerformed
        String selectedGraphName = getSelectedGraphName();
        if (selectedGraphName.isEmpty()) {
            outputFileName.setText("");
            return;
        }
        if(!makeDicCheckBox.isSelected()) {
            outputFileName.setText(selectedGraphName + "-recursive-paths.txt");
        }
        else {
            outputFileName.setText(selectedGraphName + "-recursive-paths.dic");
        }
    }//GEN-LAST:event_exploreRecButtonActionPerformed

    private void inputGraphNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputGraphNameActionPerformed
        GraphFrame f = (GraphFrame) inputGraphName.getSelectedItem();
        setOutputFileDefaultName(f);
    }//GEN-LAST:event_inputGraphNameActionPerformed

    private void exploreIndepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreIndepButtonActionPerformed
        String selectedGraphName = getSelectedGraphName();
        if (selectedGraphName.isEmpty()) {
            outputFileName.setText("");
            return;
        }
        if(!makeDicCheckBox.isSelected()) {
            outputFileName.setText(selectedGraphName + "-paths.txt");
        }
        else {
            outputFileName.setText(selectedGraphName + "-paths.dic");
        }
    }//GEN-LAST:event_exploreIndepButtonActionPerformed

    private void makeDicCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputGraphNameActionPerformed
        String selectedGraphName = getSelectedGraphName();
        if(makeDicCheckBox.isSelected()) {
            separateOutputsButton.setEnabled(false);
            alternateOutputsButton.setEnabled(false);
            ignoreOutputsButton.setEnabled(false);
            separateOutputsButton.setSelected(true);
            typeDictionaryGraphLabel.setEnabled(true);
            morphologicalButton.setEnabled(true);
            notMorphologicalButton.setEnabled(true);
            notMorphologicalButton.setSelected(true);
            multidelafButton.setSelected(true);
            exploreRecButton.setSelected(true);
            exploreRecButton.setEnabled(false);
            exploreIndepButton.setEnabled(false);
            if (selectedGraphName.isEmpty()) {
                outputFileName.setText("");
                return;
            }
            if (exploreRecButton.isSelected()) {
                outputFileName.setText(selectedGraphName + "-recursive-paths.dic");
            } else {
                outputFileName.setText(selectedGraphName + "-paths.dic");
            }

        }
        else {
            separateOutputsButton.setEnabled(true);
            alternateOutputsButton.setEnabled(true);
            ignoreOutputsButton.setEnabled(true);
            typeDictionaryGraphLabel.setEnabled(false);
            morphologicalButton.setEnabled(false);
            notMorphologicalButton.setEnabled(false);
            desiredOutputLabel.setEnabled(false);
            multidelafButton.setEnabled(false);
            delafButton.setEnabled(false);
            configurationFileLabel.setEnabled(false);
            configurationFileName.setEnabled(false);
            setConfigurationFile.setEnabled(false);
            notMorphologicalButton.setSelected(true);
            multidelafButton.setSelected(true);
            exploreRecButton.setEnabled(true);
            exploreIndepButton.setEnabled(true);
            if (selectedGraphName.isEmpty()) {
                outputFileName.setText("");
                return;
            }
            if (exploreRecButton.isSelected()) {
                outputFileName.setText(selectedGraphName + "-recursive-paths.txt");
            } else {
                outputFileName.setText(selectedGraphName + "-paths.txt");
            }
        }
    }//GEN-LAST:event_inputGraphNameActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if (inputGraphName.getSelectedItem() == null ||
          ((GraphFrame) inputGraphName.getSelectedItem()).getGraph() == null) {
            JOptionPane.showMessageDialog(UnitexFrame.mainFrame, 
              "Cannot explore graph paths for graph with no name, save the graph first", "Error",
              JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (outputFileName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(UnitexFrame.mainFrame, 
              "Cannot explore graph paths with empty file output", "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        Fst2ListCommand cmd = new Fst2ListCommand();
        final Grf2Fst2Command grfCmd = new Grf2Fst2Command();
        File fst2;
        File list; /* output file name */
        int n;
        cmd = cmd.morphologicalDic(ConfigManager.getManager().morphologicalDictionaries(null));
        if (maxSeqCheckbox.isSelected()) {
                try {
                    maxSeqSpinner.commitEdit();
                    n = (Integer) maxSeqSpinner.getValue();
                } catch (final NumberFormatException | ParseException e) {
                        JOptionPane.showMessageDialog(UnitexFrame.mainFrame,
                                        "You must specify a valid limit", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                cmd = cmd.limit(n);
        } else {
                cmd = cmd.noLimit();
        }
        if(makeDicCheckBox.isSelected()) {
            cmd = cmd.makeDic();
            if (morphologicalButton.isSelected()) {
                cmd.element("-M");
                if (delafButton.isSelected()) {
                    cmd.element("-C");
                    cmd.element(configurationFileName.getText());
                }
            }

        }
        else {
	        if (ignoreOutputsButton.isSelected()) {
	            cmd = cmd.ignoreOutputs();
	        } else {
	            cmd = cmd.separateOutputs(separateOutputsButton.isSelected());
	        }
        }
        if (ConfigManager.getManager().isKorean(null)) {
			      cmd = cmd.korean();
	      }
        if ( !checkLoopsCheckbox.isSelected() ) {
            cmd = cmd.noLoopCheck();
	      }
        String selectedGraphName = getSelectedGraphName();
        // check if flatten was checked or not
        if( !flattenCheckbox.isSelected() ) {
                grfCmd.grf(new File(selectedGraphName))
                        .enableLoopAndRecursionDetection(true).repositories()
                        .emitEmptyGraphWarning().displayGraphNames();
        } else if ( preprocessCommands == null ) {
                // if no specific option were given, preprocess with default
                File graphFile = new File(selectedGraphName);
                String name_fst2 = FileUtil.getFileNameWithoutExtension(graphFile);
                name_fst2 = name_fst2 + ".fst2";
                final MultiCommands commands = new MultiCommands();
                commands.addCommand(new Grf2Fst2Command().grf(graphFile)
                                .enableLoopAndRecursionDetection(true)
                                .tokenizationMode(null, graphFile).repositories()
                                .emitEmptyGraphWarning().displayGraphNames());
                commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
                                .resultType(flattenMode).depth(Integer.parseInt(flattenDepth)));
        }
        else {
                Launcher.exec(preprocessCommands, false);
        }

        fst2 = new File(FileUtil.getFileNameWithoutExtension(selectedGraphName) + ".fst2");
        if (exploreRecButton.isSelected()) {
                // set file to user input
                list = new File(outputFileName.getText());
                cmd = cmd.listOfPaths(fst2, list);
        } else {
                // we can't set non recursive file name to user selection yet because the name is hard coded in UnitexToolLogger (Fst2List.cpp line 1230)
                // if we change it here ShowPathsDo will throw a FileNotFoundException 
                // we will rename the file once the UnitexToolLogger process has completed
                // alternatively that process could be changed to remove the hard coding
                list = new File(selectedGraphName + "autolst.txt");
                cmd = cmd.listsOfSubgraph(fst2);
        }
        final MultiCommands commands = new MultiCommands();
        if ( !flattenCheckbox.isSelected() ) {
                commands.addCommand(grfCmd);
        }
        commands.addCommand(cmd);
        outputArea.reset();
        Launcher.exec(commands, true, new ShowPathsDo(list), false,true);
    }//GEN-LAST:event_runButtonActionPerformed

    private void flattenOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenOptionButtonActionPerformed
        File graphFile = new File(inputGraphName.getSelectedItem().toString());
        Map<String, Object> flattenOptions = UnitexFrame
            .flattenGraph(graphFile, flattenMode, flattenDepth);
        if (flattenOptions != null) {
            // unpack the commands and its options
            preprocessCommands = (MultiCommands) flattenOptions.get("commands");
            flattenMode = (boolean) flattenOptions.get("flattenMode");
            flattenDepth = (String) flattenOptions.get("flattenDepth");
        }
    }//GEN-LAST:event_flattenOptionButtonActionPerformed

    private void checkLoopsCheckboxActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkLoopsCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkLoopsCheckboxActionPerformed

    private void setFileButtonActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setFileButtonActionPerformed
        openOutputFile();
    }//GEN-LAST:event_setFileButtonActionPerformed

    private void morphologicalButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.isSelected) {
            desiredOutputLabel.setEnabled(true);
            multidelafButton.setEnabled(true);
            delafButton.setEnabled(true);
            multidelafButton.setSelected(true);
        }
    }

    private void notMorphologicalButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.isSelected) {
            desiredOutputLabel.setEnabled(false);
            multidelafButton.setEnabled(false);
            delafButton.setEnabled(false);
            configurationFileLabel.setEnabled(false);
            configurationFileName.setEnabled(false);
            setConfigurationFile.setEnabled(false);
            multidelafButton.setSelected(true);
        }
    }

    private void multidelafButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.isSelected) {
            configurationFileLabel.setEnabled(false);
            configurationFileName.setEnabled(false);
            setConfigurationFile.setEnabled(false);
        }
    }

    private void delafButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.isSelected) {
            configurationFileLabel.setEnabled(true);
            configurationFileName.setEnabled(true);
            setConfigurationFile.setEnabled(true);
        }
    }

    private void setConfigurationFileActionPerformed(java.awt.event.ActionEvent evt) {
        final int returnVal = Config.getExploreGraphOutputDialogBox().showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            // we return if the user has clicked on CANCEL
            return;
        }
        final String name;
        try {
            name = Config.getExploreGraphOutputDialogBox().getSelectedFile()
                .getCanonicalPath();
        } catch (final IOException e) {
            return;
        }
        configurationFileName.setText(name);
        //outputFileName.setText(name);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphPathFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphPathFrame().setVisible(true);
            }
        });
        
    }
    
    void close() {
        setVisible(false);
        outputArea.reset();
        outputArea.clearSelection();
        outputArea.getModel().removeListDataListener(listListener);
    }
    
    @Override
    public void onUpdate(ArrayList<GraphFrame> frames) {
        if (frames == null || frames.isEmpty()) {
            dispose();
            return;
        }
        graphFrames = frames;
        currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
          .getCurrentFocusedGraphFrame();
        if (currentFrame == null || currentFrame.getGraph() == null) {
            currentFrame = graphFrames.get(0);
        }
        fillComboBox();
        setOutputFileDefaultName(currentFrame);
    }

    private void fillComboBox() {
        ComboBoxToolTipRenderer renderer = new ComboBoxToolTipRenderer();
        ArrayList<String> tooltips = new ArrayList<String>();
        DefaultComboBoxModel<GraphFrame> model = new DefaultComboBoxModel<GraphFrame>();
        for (GraphFrame f : graphFrames) {
            model.addElement(f);
            if (f.getGraph() == null) {
                tooltips.add(f.toString());
            }
            else {
                tooltips.add(f.getGraph().getPath());
            }
        }
        renderer.setTooltips(tooltips);
        inputGraphName.setRenderer(renderer);
        inputGraphName.setModel(model);
    }

    public class ComboBoxToolTipRenderer extends DefaultListCellRenderer {
  
        private ArrayList<String> tooltips;
        @Override
        public JComponent getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
            JComponent comp = (JComponent) super.getListCellRendererComponent(list,
              value, index, isSelected, cellHasFocus);
      
            if (-1 < index && null != value && null != tooltips) {
              list.setToolTipText(tooltips.get(index));
            }
            return comp;
        }
        public void setTooltips(ArrayList<String> tooltips) {
          this.tooltips = tooltips;
        }
    }

    class ShowPathsDo implements ToDo {
		private final File name;

		ShowPathsDo(File name) {
			this.name = name;
		}

		@Override
		public void toDo(boolean success) {
			outputArea.load(name);
			outputArea.getModel().addListDataListener(listListener);
			
			try {
				// issue #61 - recursive path option invokes UnitexToolLogger which hard codes the name of the output file to GraphNameautolst.txt 
				// once that process has completed and loaded the file rename it using the user input if that differs from the default
				if (!name.getAbsolutePath().equals(outputFileName.getText())) {
					File dest = new File(outputFileName.getText());
					Files.move(name.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}  catch (final IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Could not save path list to " + outputFileName.getText(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} 
		}
	}
    
    void setInputGraphName(String string) {
    }
    
    private void setOutputFileDefaultName(GraphFrame graphFrame) {
      if (graphFrame == null || graphFrame.getGraph() == null) {
          outputFileName.setText("");
          return;
      }
      String graphName = FileUtil.getFileNameWithoutExtension(graphFrame.getGraph().getPath());
    	String extension = makeDicCheckBox.isSelected() ? ".dic" : ".txt";
    	 if(exploreRecButton.isSelected()) {
             outputFileName.setText(graphName + "-recursive-paths" + extension);
         }
         else {
             outputFileName.setText(graphName + "-paths" + extension);
         }
    }
    
    private void openOutputFile() {
        final int returnVal = Config.getExploreGraphOutputDialogBox().showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
                // we return if the user has clicked on CANCEL
                return;
        }
        final String name;
        try {
                name = Config.getExploreGraphOutputDialogBox().getSelectedFile()
                                .getCanonicalPath();
        } catch (final IOException e) {
                return;
        }
        outputFileName.setText(name);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox checkLoopsCheckbox;
    private javax.swing.JRadioButton exploreIndepButton;
    private javax.swing.JLabel exploreLabel;
    private javax.swing.JRadioButton exploreRecButton;
    private javax.swing.JCheckBox flattenCheckbox;
    private javax.swing.JButton flattenOptionButton;
    private javax.swing.JLabel graphFileLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JRadioButton ignoreOutputsButton;
    private javax.swing.JComboBox<GraphFrame> inputGraphName;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox maxSeqCheckbox;
    private javax.swing.JSpinner maxSeqSpinner;
    private javax.swing.JRadioButton alternateOutputsButton;
    private javax.swing.JLabel optionLabel;
    private javax.swing.JSeparator optionSeparator;
    private fr.umlv.unitex.text.BigTextList outputArea;
    private javax.swing.JLabel outputFileLabel;
    private javax.swing.JTextField outputFileName;
    private javax.swing.JLabel outputsLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JSeparator resultSeparator;
    private javax.swing.JButton runButton;
    private javax.swing.JButton setFileButton;
    private javax.swing.JRadioButton separateOutputsButton;
    private javax.swing.JCheckBox makeDicCheckBox;

    //Multi2Delaf
    private javax.swing.JLabel typeDictionaryGraphLabel;
    private javax.swing.JRadioButton morphologicalButton;
    private javax.swing.JRadioButton notMorphologicalButton;
    private javax.swing.JLabel desiredOutputLabel;
    private javax.swing.JRadioButton multidelafButton;
    private javax.swing.JRadioButton delafButton;
    private javax.swing.JLabel configurationFileLabel;
    private javax.swing.JTextField configurationFileName;
    private javax.swing.JButton setConfigurationFile;
    private javax.swing.ButtonGroup typeDictionaryGraphButtonGroup;
    private javax.swing.ButtonGroup desiredOutputButtonGroup;
    // End of variables declaration//GEN-END:variables
}
