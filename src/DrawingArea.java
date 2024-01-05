
import java.awt.Graphics;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
import java.awt.Image;
import java.awt.Toolkit;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 349737163
 */
public class DrawingArea extends javax.swing.JPanel {
    //timers
    Timer subTimer;
    Timer haidaTimer;
    //image for interception
    Image signal = Toolkit.getDefaultToolkit().getImage("foundSignal.png");
    //input from MainFrame
    private static double subA;
    private static double subB;
    private static int subSpeed;
    private static int haidaSpeed;
    private static int searchRadius;
    private static boolean start=false;
    private static boolean inSpiral=false;
    static boolean display=false;
    private static boolean showTrace = false;
    //starting positions
    private static int haidaStartX;
    private static int haidaStartY;
    private static int subStartX;
    private static int subStartY;
    //current distance between the submarine and haida
    private static int curDistance;
    //set the smallest distance to 100 to start
    int minDistance=100;
    //current coordinates for animation
    int currentSubX;
    int currentSubY;
    int haidaX;
    int haidaY;
    static int angle=0;
    int radius = 0;
    int currentIndex = 1;
    int numStart = 0;
    //longs for timer updates
    long lastSubTime=0;
    long lastHaidaTime=0;
    boolean updateSub=false;
    static ArrayList<Integer> subCoordinates = new ArrayList<Integer>();
    static ArrayList<Integer> searchDistance = new ArrayList<Integer>();

    /**
     * Creates new form DrawingArea
     */
    public DrawingArea() {
        initComponents();
        //create new timers with the corresponding speeds
        subTimer = new Timer(subSpeed/2*100, new DrawingArea.TimerListener());
        subTimer.start();
        haidaTimer = new Timer(haidaSpeed*9, new DrawingArea.TimerListener());
        haidaTimer.start();
    }
    //set starting information from MainFrame
    public static void setStart(boolean validInfo){
        start=validInfo;
        //if the user inputs valid information and clicks the search button, start the program
    }
    public static void setB(double b){
        subB=b; //set b value
    }
    public static void setA(double a){
        subA=a; //set a value
    }
    public static void setSubSpeed(int speed){
        subSpeed = speed; //set the submarine's speed
    }
    public static void setHaida(int speed){
        haidaSpeed = speed; //set the Haida's speed
    }
    public static void setSearchRadius(int radius){
        searchRadius = radius; //set Haida's search radius
    }
    public static void setTrace(boolean trace){
        showTrace = trace; //set showTrace
    }
    public static void setIn(boolean in){
        //if the user wants an inward spiral
        inSpiral = in;
        if(inSpiral){
            //set the angle to 360 to start
            angle=360;
        }
    }
    private class TimerListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae){
            //if enough time has passed for the sub timer's refresh
            if(System.currentTimeMillis()-lastSubTime > subSpeed/2*100){
                //reset the last update time
                lastSubTime= System.currentTimeMillis();
                //repaint the sub's position
                updateSub= true;
                repaint();
            }
            //if enough time has passed for the Haida's timer refresh
            if(System.currentTimeMillis()-lastHaidaTime > haidaSpeed/2*10){
                //reset the last update time
                lastHaidaTime = System.currentTimeMillis();
                //repaint the Haida's position
                updateSub= false;
                repaint();
            }
        }
    }
    
    /**
     * Create an array list of the submarine's coordinates
     * @param subCoordinates
     * @param a
     * @param b
     * @param x
     * @return 
     */
    public static ArrayList subExp (ArrayList<Integer>subCoordinates, double a, double b,int x){
        int indexLast = subCoordinates.size()-1;
        int currentX = subCoordinates.get(indexLast-1);
        int currentY = subCoordinates.get(indexLast);
        //while the x value of the submarine is within the panel
        while(currentX>=0 && currentX<=600){
        if(currentY>500||currentY<0){
            //if the y value of the submarine is outside of the panel, return subCoordinates
            return subCoordinates;
        }else{ //if the y-coordinates is still inside the panel
            if(b==1){ //if b = 1, only update the x-value
                subCoordinates.add(currentX+30);
                subCoordinates.add(currentY);
                //call the method again
                subExp(subCoordinates,a,b,x+1);
                return subCoordinates;
            }else{
            //calculate the change from the current Y position
            double change = a*Math.pow(b,x);
            int newY;
            if(Math.abs(b)>1){
                //if b is greater than 1, subtract the change
                newY = (int)(currentY-change);
            }else{
                //else if b is less than 1, add the change
                newY = (int)(currentY+change);
            }
            if(change>10||change<10){
                //if the magnitude of change in y is greater than 10, call the distanceBetween method to fill in gaps
                distanceBetween(currentX,currentY,newY);
            }
            //add the new coordinates to the array list
            subCoordinates.add(currentX+30);
            subCoordinates.add(newY);
            //call the method again
            subExp(subCoordinates,a,b,x+1);
            return subCoordinates;
            }
        }
        }return subCoordinates;
    }
    
    /**
     * Method to add linear interpolations to the submarine's path
     * @param x
     * @param start
     * @param target 
     */
    public static void distanceBetween(int x, int start, int target){
        //calculate the distance between the new and old y-coordinates
        int difference = Math.abs(target-start);
        int newY;
        //if the difference is less than 30, add a point midway
        if(difference<30){
            subCoordinates.add(x+15);
            newY=start+(15*(target-start)/(30));
            subCoordinates.add(newY);
        }else if (difference<70){
            //if the difference is between 30 and 70, add 2 points midway
            subCoordinates.add(x+10);
            newY=start+(10*(target-start)/(30));
            subCoordinates.add(newY);
            subCoordinates.add(x+20);
            newY=start+(20*(target-start)/(30));
            subCoordinates.add(newY);
        }else{
            //if the difference is over 70, add 4 points midway
            subCoordinates.add(x+6);
            newY=start+(6*(target-start)/(30));
            subCoordinates.add(newY);
            subCoordinates.add(x+12);
            newY=start+(12*(target-start)/(30));
            subCoordinates.add(newY);
            subCoordinates.add(x+18);
            newY=start+(18*(target-start)/(30));
            subCoordinates.add(newY);
            subCoordinates.add(x+24);
            newY=start+(24*(target-start)/(30));
            subCoordinates.add(newY);
        }
        }
        
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // when the panel is pressed
        if(start){ 
        //if the search button has been pressed and there are enough valid inputs
        numStart++;
        //get the mouse coordinates
        PointerInfo a = MouseInfo.getPointerInfo();
        Point vehicleStart = a.getLocation();
        int x = (int)(vehicleStart.getX());
        int y = (int)(vehicleStart.getY());
        if(numStart==1){
            //if it is the first click, set the coordinates to the submarine's start position
            subStartX = x;
            subStartY=y;
            currentSubX = x;
            currentSubY = y;
            subCoordinates.add(x);
            subCoordinates.add(y);
        }else if(numStart==2){
            //if it is the second click, set the coordinates to the Haida's start position
            haidaStartX=x;
            haidaX = x;
            haidaStartY=y;
            haidaY=y;
            //calculate the submarine's path and print it in the console
            subExp(subCoordinates,subA,subB,0);
            for(int i = 0;i<subCoordinates.size()-2;i++){
                if(i%2==0){
                System.out.print("("+subCoordinates.get(i)+", "+subCoordinates.get(i+1)+"); ");
                }
            }
            //start the animation
            lastSubTime = System.currentTimeMillis();
            lastHaidaTime = System.currentTimeMillis();
            display=true;
        }
        }
          
    }//GEN-LAST:event_formMousePressed
   
    /**
     * Method to find the real-time distance between the submarine and the Haida
     * @param haidaX
     * @param haidaY
     * @param subCoordinates
     * @param index
     * @return 
     */
    public ArrayList<Integer> findDistance(int haidaX, int haidaY, ArrayList<Integer>subCoordinates, int index){
        //if the x and y coordinates of the sub are within the panel and there has been no interception
        if(subCoordinates.get(index+1)>500 || subCoordinates.get(index+1)<0 ||minDistance<=50||subCoordinates.get(index)>600||subCoordinates.get(index)<0){
            return searchDistance;//return searchDistance
        }else{
            //calculate the distance between the two vehicles
            double xDistance = Math.pow(haidaX-currentSubX, 2);
            double yDistance = Math.pow(haidaY-currentSubY,2);
            int distance = (int)(Math.sqrt(xDistance+yDistance));
            curDistance = distance;
            //add the distance to the array
            searchDistance.add(distance);
            if(distance<=50){
                //if there is an interception
                display=false;//stop the animation
            }
            if(distance<minDistance){
                //if the distance is smaller than the current minimum distance
                //replace the current minimum distance
               minDistance=distance;
            }
            //call the method again with the next pair of coordinates
            findDistance(haidaX, haidaY, subCoordinates,index+2);
            return searchDistance;
        }
       
    }
    
    
   @Override
    public void paintComponent(Graphics g){
        Color rect = new Color(214, 213, 227);
        if (!showTrace){
            //if the user does not want to show the traced path, refresh the background every update
            super.paintComponent(g);
        }
        if(numStart>=1){
            //display the submarine's starting position
            g.drawString("Sub Starts at: ("+subStartX+", "+subStartY+")", 20,200);
        }
        if (numStart>=2){
            //display the Haida's starting position
            g.drawString("Haida Starts at: ("+haidaStartX+", "+haidaStartY+")", 20,250);
            //display the exponential equation
            g.drawString("y= "+subA+"*"+subB+"^x",470,200);
        }

        //for the animation
        if(display){
            //if enough time has passed for the submarine update
            if(updateSub){
                //draw an oval for the submarine at the current coordinates
                g.setColor(Color.red);
                g.fillOval(currentSubX, currentSubY,10,20);
                //if the end of the array is not reached
                if(currentIndex+2<subCoordinates.size()-1){
                currentSubX =subCoordinates.get(currentIndex-1);
                currentSubY = subCoordinates.get(currentIndex+2);
                currentIndex+=2;
                }else{
                    display=false;
                }
            }else{//if enough time has passed for the Haida update
            //draw a rectangle for the Haida     
            g.setColor(Color.blue);
            g.fillRect(haidaX, haidaY, 15,25);
            //calculate the new coordinate for the Haida
            double radian = java.lang.Math.toRadians(angle);
            haidaX = haidaStartX+(int)(radius*java.lang.Math.cos(radian));
            haidaY = haidaStartY+(int)(radius*java.lang.Math.sin(radian));
            //find the current distance with the submarine
            findDistance(haidaX, haidaY,subCoordinates,0);
            //display the current distance
            g.setColor(rect);
            g.fillRect(550,330,40,30);
            if(radius==0){ 
             //if it is the beginning of the spiral, just put one rectangle in the center
                radius+=30;
            }
            else if(radius<searchRadius){
                //if the current radius is less than the desired radius
                if(inSpiral){
                    //if the user wants an inwards spiral
                    if(angle>0){
                        //go counter clockwise
                        angle-=20;
                    }else{
                        angle=360;
                        radius+=30;
                    }
                }else{
                    //if the user wants an outwards spiral
                    if(angle<360){
                        //go clockwise
                        angle+=20;
                    }else{
                        angle=0;
                        radius+=30;
                    }
                }
            }
        }
            //display the current distance
           g.setColor(Color.black);
           g.drawString("Current Distance: "+curDistance, 470,350);
        }
        //if there is an interception
        if(minDistance<=50){
            //display a signal image
            g.drawImage(signal, haidaX, haidaY, 100,100,this);
            //display the interception point
            g.drawString("Submarine Intercepted at ("+haidaX+", "+haidaY+")", 400,400);
            //g.drawString("Current Distance: "+curDistance, 470,350);
        }else if(numStart>=2&&!display && minDistance>50){
            //if there is no interception, but the submarine is outside the panel
            //display that there was no interception
            g.drawString("Submarine was not intercepted", 400,400);
        }  
                    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
}
