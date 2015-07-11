package crossMapper_V0_1;

/*
 * oscSettings.java
 *
 * Created on 04-Jan-2011, 10:49:17
 */



/**
 *
 * @author lmosulli
 */
public class OscSettingsMenu extends javax.swing.JFrame {

    /** Creates new form oscSettings */
    public OscSettingsMenu() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        oscLocalAddLabel = new javax.swing.JLabel();
        oscListenPortLabel = new javax.swing.JLabel();
        oscRemoteAddLabel = new javax.swing.JLabel();
        oscSendPortLabel = new javax.swing.JLabel();
        oscLocalAddFld = new javax.swing.JTextField();
        oscListenPortFld = new javax.swing.JTextField();
        oscRemoteAddFld = new javax.swing.JTextField();
        oscSendPortFld = new javax.swing.JTextField();
        oscSettingsLabel = new javax.swing.JLabel();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        oscLocalAddLabel.setText("OSC Local Address");

        oscListenPortLabel.setText("Listening Port");

        oscRemoteAddLabel.setText("OSC Remote Address");

        oscSendPortLabel.setText("Sending Port");

        oscLocalAddFld.setText(CrossMapper_V0_1_Start.myMapperV3.localAddrString);
        oscLocalAddFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscLocalAddFldActionPerformed(evt);
            }
        });

        oscListenPortFld.setText(Integer.toString(CrossMapper_V0_1_Start.myMapperV3.listenPort));
        oscListenPortFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscListenPortFldActionPerformed(evt);
            }
        });

        oscRemoteAddFld.setText(CrossMapper_V0_1_Start.myMapperV3.remoteAddrString);
        oscRemoteAddFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscRemoteAddFldActionPerformed(evt);
            }
        });

        oscSendPortFld.setText(Integer.toString(CrossMapper_V0_1_Start.myMapperV3.sendPort));
        oscSendPortFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscSendPortFldActionPerformed(evt);
            }
        });

        oscSettingsLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        oscSettingsLabel.setText("Open Sound Control Settings");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oscSettingsLabel)
                .addContainerGap(131, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(oscSendPortLabel)
                    .addComponent(oscRemoteAddLabel)
                    .addComponent(oscListenPortLabel)
                    .addComponent(oscLocalAddLabel))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(oscRemoteAddFld)
                    .addComponent(oscLocalAddFld)
                    .addComponent(oscListenPortFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oscSendPortFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {oscListenPortFld, oscSendPortFld});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oscSettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oscLocalAddFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oscLocalAddLabel))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oscListenPortLabel)
                    .addComponent(oscListenPortFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oscRemoteAddLabel)
                    .addComponent(oscRemoteAddFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(oscSendPortLabel)
                    .addComponent(oscSendPortFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void oscListenPortFldActionPerformed(java.awt.event.ActionEvent evt) {                                               
        
    	//System.out.println("The event is: "+ evt);
    	System.out.println("The entered port number is: "+ Integer.parseInt(evt.getActionCommand()));
    	CrossMapper_V0_1_Start.myMapperV3.oscListenPortChange(Integer.parseInt(evt.getActionCommand()));
    	
    }  
    
    private void oscLocalAddFldActionPerformed(java.awt.event.ActionEvent evt) {                                               
          	
    	
    }                                              

    private void oscSendPortFldActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
    	//System.out.println("The event is: "+ evt);
    	System.out.println("The entered port number is: "+ Integer.parseInt(evt.getActionCommand()));
    	CrossMapper_V0_1_Start.myMapperV3.oscSendPortChange(Integer.parseInt(evt.getActionCommand()));
    }                                              

    private void oscRemoteAddFldActionPerformed(java.awt.event.ActionEvent evt) {                                                
    	//System.out.println("The remote address field event is: "+ evt);
    	System.out.println("The entered remote address is: "+ evt.getActionCommand());
    	CrossMapper_V0_1_Start.myMapperV3.oscRemoteAddrChange(evt.getActionCommand());
    }                                               

   
    // Variables declaration
    private javax.swing.JTextField oscListenPortFld;
    private javax.swing.JLabel oscListenPortLabel;
    private javax.swing.JTextField oscLocalAddFld;
    private javax.swing.JLabel oscLocalAddLabel;
    private javax.swing.JTextField oscRemoteAddFld;
    private javax.swing.JLabel oscRemoteAddLabel;
    private javax.swing.JTextField oscSendPortFld;
    private javax.swing.JLabel oscSendPortLabel;
    private javax.swing.JLabel oscSettingsLabel;
    // End of variables declaration

}
