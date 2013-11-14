/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import commonLibs.ModelType;
import commonLibs.RndCell;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *
 * @author Libra
 */
public class CircuitViewUI extends javax.swing.JFrame {

    private ModelType networkType;
    String pathToFile;

    public void setNetworkType(ModelType networkType) {
        this.networkType = networkType;
        lblDesc.setText(networkType.toString());
    }

    /**
     * Creates new form BirdEyeUI
     */
    public CircuitViewUI() {
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

        btnGrpSize = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        lblDesc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        rdoLarge = new javax.swing.JRadioButton();
        rdoSmall = new javax.swing.JRadioButton();
        scrollPane = new javax.swing.JScrollPane();
        canvas = new modelNetGen.CanvasBean();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(77, 300));

        btnSave.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblDesc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDesc.setText("Unknown_Type");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("ModelType:");

        btnOpen.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnOpen.setText("Open");
        btnOpen.setEnabled(false);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        btnGrpSize.add(rdoLarge);
        rdoLarge.setSelected(true);
        rdoLarge.setText("Large");
        rdoLarge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoLargeActionPerformed(evt);
            }
        });

        btnGrpSize.add(rdoSmall);
        rdoSmall.setText("Small");
        rdoSmall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoSmallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnOpen)
                .addGap(18, 18, 18)
                .addComponent(rdoLarge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdoSmall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDesc)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(lblDesc)
                    .addComponent(jLabel1)
                    .addComponent(btnOpen)
                    .addComponent(rdoLarge)
                    .addComponent(rdoSmall))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        canvas.setPreferredSize(new java.awt.Dimension(3000, 3000));

        javax.swing.GroupLayout canvasLayout = new javax.swing.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3000, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3000, Short.MAX_VALUE)
        );

        scrollPane.setViewportView(canvas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        String suffix;
        switch (networkType) {
            case Ctrl:
                suffix = "Ctrl";
                break;
            case Network:
                suffix = "Net";
                break;
            default:
                suffix = "Others";
        }
        BufferedImage bi = ScreenImage.createImage(canvas);
        try {
            pathToFile = Paths.get("").toAbsolutePath().getParent().toString() + "/img_out/CircuitImage_" + suffix + ".png";
//            System.out.println(pathToFile);
            ScreenImage.writeImage(bi, pathToFile);
            btnOpen.setEnabled(true);
        } catch (IOException e) {
            System.err.println(e.toString());
        }        // TODO add your handling code here:


    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(this, "This operationg system is not supported ", "File Operation", JOptionPane.INFORMATION_MESSAGE);
        }
//TODO Proper handle
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            JOptionPane.showMessageDialog(this, "This operationg system is not supported ", "File Operation", JOptionPane.INFORMATION_MESSAGE);
        }

        try {
//            System.out.println("open" + pathToFile);
            File f = new File(pathToFile);
            desktop.open(f);

        } catch (IOException e) {
            System.out.println(e.toString());
            // Log an error
        }

    }//GEN-LAST:event_btnOpenActionPerformed

    private void rdoLargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoLargeActionPerformed
        canvas.setPreferredSize((new Dimension(3000, 3000)));
        canvas.revalidate();
    }//GEN-LAST:event_rdoLargeActionPerformed

    private void rdoSmallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoSmallActionPerformed
        canvas.setPreferredSize((new Dimension(640, 640)));
        canvas.revalidate();
    }//GEN-LAST:event_rdoSmallActionPerformed

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
            java.util.logging.Logger.getLogger(CircuitViewUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CircuitViewUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGrpSize;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnSave;
    private modelNetGen.CanvasBean canvas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JRadioButton rdoLarge;
    private javax.swing.JRadioButton rdoSmall;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    public void setDim(int dim) {
        canvas.setModelDim(dim);
    }

    public void setCellList(ArrayList<RndCell> cellList) {
        canvas.setCellList(cellList);
    }

    public void repaintCanvas() {
        canvas.repaint();
    }

    public void setConnected(Set<Integer> conn) {
        canvas.setConnected(conn);
    }

    public void closeWindow() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
//    public void setMyWorker(SwingWorker worker) {
//        this.myWorker = worker;
//    }
//
//    public SwingWorker getMyWorker() {
//        return myWorker;
//    }
//    SwingWorker myWorker;
}
