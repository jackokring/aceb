package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author jacko
 */


 public class DisplayTerminal extends Screen {

        public void setCell(int x, int y, int c) {
            
        }
        LayerManager layers;
        TiledLayer tl;
        Image paper;    // Holds the default paper yellow
        public DisplayTerminal() {
            super(false);
            Image font;
            scrn = (paper = Image.createImage(getWidth(), getHeight())).getGraphics();
            scrn.setColor(255, 255, 0);
            scrn.fillRect(0, 0, getWidth(), getHeight());
            disp = getGraphics();
            try {
                font = Image.createImage("/Font.png");
            } catch(Exception e) {
                font = null;
            }
            tl = new TiledLayer(getWidth()/8,getHeight()/12,font,8,12);
            layers = new LayerManager();
            tl.setPosition(0,0);
            tl.setVisible(true);
            layers.append(tl);
        }

             public void stringToCells(String s) {
        int x;
        for(x = 0; x < s.length(); x++)
            ac.setCell(x%getWidth(), x/getWidth(), s.charAt(x));
        //simple for moment ????
     }

     public void clear() {
        int x, y;
        for(x = 0; x < getWidth(); x++)
            for(y = 0; y < getHeight(); y++)
                ac.setCell(x, y, 33);//a space with the +1 offset
     }

        //something about an obvious character unicode
        public void keyPressed(int code) {
            String j = getKeyName(getKeyCode(getGameAction(code)));
            if(j.length() != 0) {
                key = j.charAt(0);
            }
        }

        public void keyRepeated(int code) {
            keyPressed(code);
            //handle key repeats easy
            //changed method of telpad
        }

        public void update() {
            disp.drawImage(paper, 0, 0, Graphics.TOP | Graphics.LEFT);
            layers.paint(disp,0,0);
            flushGraphics();
        }
    }
