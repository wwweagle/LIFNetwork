/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Libra
 */
public class NetCalcUI extends javax.swing.JFrame {

    /**
     * Creates new form NetCalcUI
     */
    public NetCalcUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timeGrp = new javax.swing.ButtonGroup();
        fc = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        btnOpenFile = new javax.swing.JButton();
        btnOpenFolder = new javax.swing.JButton();
        btnDefaultFile = new javax.swing.JButton();
        btnDefaultFolder = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTime = new javax.swing.JTextField();
        rdoMilliSec = new javax.swing.JRadioButton();
        rdoSec = new javax.swing.JRadioButton();
        txtGABARevP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRandProb = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtRandAmp = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtGFactor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        chkFireHis = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        btnRun = new javax.swing.JButton();
        btnSkip = new javax.swing.JButton();
        progBar = new javax.swing.JProgressBar();
        lblFileCount = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LIF Netork");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "File Input"));

        btnOpenFile.setText("Open File");
        btnOpenFile.setPreferredSize(new java.awt.Dimension(100, 25));
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        btnOpenFolder.setText("Open Folder");
        btnOpenFolder.setPreferredSize(new java.awt.Dimension(100, 25));
        btnOpenFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFolderActionPerformed(evt);
            }
        });

        btnDefaultFile.setText("Default File");
        btnDefaultFile.setPreferredSize(new java.awt.Dimension(100, 25));
        btnDefaultFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultFileActionPerformed(evt);
            }
        });

        btnDefaultFolder.setText("Default Folder");
        btnDefaultFolder.setPreferredSize(new java.awt.Dimension(100, 25));
        btnDefaultFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultFolderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDefaultFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnOpenFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDefaultFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(197, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));

        jLabel1.setText("Simulate Time");

        txtTime.setText("10");
        txtTime.setPreferredSize(new java.awt.Dimension(30, 20));
        txtTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimeActionPerformed(evt);
            }
        });

        timeGrp.add(rdoMilliSec);
        rdoMilliSec.setText("ms");

        timeGrp.add(rdoSec);
        rdoSec.setSelected(true);
        rdoSec.setText("s");

        txtGABARevP.setText("-50");
        txtGABARevP.setPreferredSize(new java.awt.Dimension(30, 20));
        txtGABARevP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGABARevPActionPerformed(evt);
            }
        });

        jLabel2.setText("mV");

        jLabel3.setText("GABA Rev Potential");

        txtRandProb.setText("40");
        txtRandProb.setPreferredSize(new java.awt.Dimension(30, 20));
        txtRandProb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRandProbActionPerformed(evt);
            }
        });

        jLabel4.setText("Random I prob");

        jLabel5.setText("%");

        jLabel6.setText("Random I amp");

        txtRandAmp.setText("40");
        txtRandAmp.setPreferredSize(new java.awt.Dimension(30, 20));
        txtRandAmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRandAmpActionPerformed(evt);
            }
        });

        jLabel7.setText("pA");

        txtGFactor.setText("0.3");
        txtGFactor.setPreferredSize(new java.awt.Dimension(30, 20));
        txtGFactor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGFactorActionPerformed(evt);
            }
        });

        jLabel8.setText("Weigh factor");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoMilliSec)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoSec))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtGABARevP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRandProb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRandAmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdoMilliSec)
                    .addComponent(rdoSec)
                    .addComponent(txtRandProb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGABARevP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6)
                    .addComponent(txtRandAmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output Parameter"));

        chkFireHis.setSelected(true);
        chkFireHis.setText("Firing History");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFireHis)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFireHis)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Control"));

        btnRun.setText("Run");
        btnRun.setEnabled(false);
        btnRun.setPreferredSize(new java.awt.Dimension(60, 60));
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        btnSkip.setText("Skip");
        btnSkip.setEnabled(false);
        btnSkip.setPreferredSize(new java.awt.Dimension(60, 60));
        btnSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSkipActionPerformed(evt);
            }
        });

        lblFileCount.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblFileCount.setText("0/0");

        btnStop.setText("Stop");
        btnStop.setEnabled(false);
        btnStop.setPreferredSize(new java.awt.Dimension(60, 60));
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRun, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSkip, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnStop, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFileCount, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSkip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnRun, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblFileCount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Log"));

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimeActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        txtLog.setText("");
        runModel();
    }//GEN-LAST:event_btnRunActionPerformed

    private void btnSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSkipActionPerformed
        if (network.isRunning()) {
            network.stopCycle();
        }
    }//GEN-LAST:event_btnSkipActionPerformed

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
        fileList = new ArrayList<>();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileNameExtensionFilter("ser files", "ser"));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String pathToFile = fc.getSelectedFile().getAbsolutePath();
            if (pathToFile.toLowerCase().endsWith(".ser")) {
                fileList.add(pathToFile);
                enableBtnGrpRun();
                log("Open file " + pathToFile);
            } else {
                log("Wrong file Type!");
            }
        }
    }//GEN-LAST:event_btnOpenFileActionPerformed

    private void txtGABARevPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGABARevPActionPerformed
    }//GEN-LAST:event_txtGABARevPActionPerformed

    private void btnDefaultFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultFileActionPerformed
        File f = new File("conn_Net_C_1.0_W_1.0.ser");
        if (f.exists()) {
            fileList = new ArrayList<>();
            fileList.add(f.getAbsolutePath());
            enableBtnGrpRun();
            log("Open file " + f.getAbsolutePath());
        } else {
            log("File not exist!");
        }
    }//GEN-LAST:event_btnDefaultFileActionPerformed

    private void btnOpenFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFolderActionPerformed
        fileList = new ArrayList<>();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File dir = new File(fc.getSelectedFile().getAbsolutePath());
            log("Open folder " + dir.getAbsolutePath());
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".ser");
                }
            });
            for (File f : files) {
                fileList.add(f.getAbsolutePath());
            }
            if (fileList.isEmpty()) {
                log("No ser files found in the folder!");
                return;
            }
            enableBtnGrpRun();
        }
    }//GEN-LAST:event_btnOpenFolderActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        if (network.isRunning()) {
            final List<String> saveList = new ArrayList<>(fileList);
            fileList.clear();
            network.stopCycle();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    progBar.setValue(0);
                    log("Canceled.");
                    fileList = saveList;
                    lblFileCount.setText("0/" + fileList.size());
                }
            }, 2000);
        }

    }//GEN-LAST:event_btnStopActionPerformed

    private void txtRandProbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRandProbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRandProbActionPerformed

    private void txtRandAmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRandAmpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRandAmpActionPerformed

    private void btnDefaultFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultFolderActionPerformed
        fileList = new ArrayList<>();
        File dir = new File("G:\\java\\lifNetwork");
        log("Open folder " + dir.getAbsolutePath());
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".ser");
            }
        });
        for (File f : files) {
            fileList.add(f.getAbsolutePath());
        }
        if (fileList.isEmpty()) {
            log("No ser files found in the folder!");
            return;
        }
        enableBtnGrpRun();
    }//GEN-LAST:event_btnDefaultFolderActionPerformed

    private void txtGFactorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGFactorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGFactorActionPerformed

    private void runModel() {
        new SwingWorker<Void, Integer>() {
            int fileTotal = fileList.size();
            Timer updateProgress;

            @Override
            protected Void doInBackground() {
                int fileCount = 0;
                for (String pathToFile : fileList) {
                    publish(++fileCount);
                    log("Currently processing: " + pathToFile);
                    int timeNominal = Integer.parseInt(txtTime.getText());
                    timeNominal *= rdoSec.isSelected() ? 1000 * 1000 : 1000;
                    int GABARevP = Integer.parseInt(txtGABARevP.getText());
                    int randProb = Integer.parseInt(txtRandProb.getText());
                    int randAmp = Integer.parseInt(txtRandAmp.getText());
                    float gFactor = Float.parseFloat(txtGFactor.getText());
                    network = new NetworkCalc(timeNominal, GABARevP, randProb, randAmp, gFactor, pathToFile);
                    startUpdateProgBar();
                    log("Total fires = " + network.cycle());
                    stopUpdateProgBar();
                }
                return null;
            }

            @Override
            protected void done() {
            }

            @Override
            protected void process(List<Integer> chunks) {
                lblFileCount.setText(chunks.get(chunks.size() - 1).toString()
                        + "/" + fileTotal);
            }

            private void startUpdateProgBar() {
                progBar.setValue(0);
                updateProgress = new Timer();
                updateProgress.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int progress = network.getProgress();
                        progBar.setValue(progress);
                    }
                }, 0, 1000);
            }

            private void stopUpdateProgBar() {
                updateProgress.cancel();
                progBar.setValue(100);
            }
        }.execute();
    }

    private void log(String s) {
        txtLog.append(s + "\n");
    }

    private void enableBtnGrpRun() {
        btnRun.setEnabled(true);
        btnSkip.setEnabled(true);
        btnStop.setEnabled(true);
        lblFileCount.setText("0/" + fileList.size());
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NetCalcUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NetCalcUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDefaultFile;
    private javax.swing.JButton btnDefaultFolder;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnOpenFolder;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSkip;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox chkFireHis;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFileCount;
    private javax.swing.JProgressBar progBar;
    private javax.swing.JRadioButton rdoMilliSec;
    private javax.swing.JRadioButton rdoSec;
    private javax.swing.ButtonGroup timeGrp;
    private javax.swing.JTextField txtGABARevP;
    private javax.swing.JTextField txtGFactor;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtRandAmp;
    private javax.swing.JTextField txtRandProb;
    private javax.swing.JTextField txtTime;
    // End of variables declaration//GEN-END:variables
    private NetworkCalc network;
    private List<String> fileList;
}
