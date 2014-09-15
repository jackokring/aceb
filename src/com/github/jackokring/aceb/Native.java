package com.github.jackokring.aceb;

import java.io.*;

abstract class Native implements Runnable, Keys {
    //use static for instance control.

    /* THE MAIN CODE INTERFACE */
    public int code(int sp) {
        new Thread(this).start();//maybe a thread.
        return sp;
    }

    //sp is the stack index into m going positive for down stack.
    //return a new stack pointer if any items are consumed or made.
    //This routine is synchronous and blocking.

    /* THE FOLLOWING ARE THE AceB INTERFACE
        String AceB.alloc(String s); //returns and sets execution context
        AceB.addCommand(Command c, CommandListener this); //for adding command menu option
        Display AceB.display; //application Display
        boolean AceB.exited; //main application exited flag
        char AceB.m[]; //the memory
        Graphics AceB.scrn; //Graphics of the Display's background
        String AceB.asString(int s); //forth string to Java String
        AceB.beep(int f, int d); //beep frequency MIDI note and miliseconds duration
        AceB.bye(); //exit the application propely
        AceB.destroy; //take action to destroy application
        AceB.evaluate(String s); //evaluate String of AceB forth
        AceB.media(String s); //open media at URL String
        AceB.nativ(String s); //open a Native Java class by name String
        boolean AceB.pause; //pause the application or take thread yield action
        InputStream AceB.urlStream; //the current stream for INURL
        AceB.vidout(int mode); //refresh application Display
    */

    /* THE THREADING INTERFACE */
    public abstract void run();
    public abstract void exited();
    public abstract void actions(Action ac, Keys s);

    /* UNICODE SUBSET HANDLING */
    // Unicode UTF-8/16 with no surragates and folded above 3 bytes.
    // A char never has a code above 16 bits.
    // Technically codes 0x10000 to 0x1ffff could be folded as 3 bytes
    // with 0xf_ lead byte. When does character separate from font?
    // Well, who uses UTF-32 anyhow?
    static byte[] asBytes(char[] str) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	try
	{
            int strlen = str.length;
            for( int i = 0 ; i < strlen ; i++ ) {
                char c = str[i];
                if(c >= 0 && c < 0x80) {
                        bos.write((byte)( c & 0xff ));
                } else if(c > 0x7f && c < 0x800) {
                        bos.write( (byte) ((( c >>> 6 ) & 0x1f ) | 0xc0 ));
                        bos.write( (byte) ((( c >>> 0 ) & 0x3f ) | 0x80 ));
                } else {
                        bos.write( (byte) ((( c >>> 12 ) & 0x0f ) | 0xe0 ));
                        bos.write( (byte) ((( c >>> 6 ) & 0x3f ) | 0x80 ));
                        bos.write( (byte) ((( c >>> 0 ) & 0x3f ) | 0x80 ));
                }
            }
            bos.flush();
	} catch(Exception e) {

	}
	return bos.toByteArray();
    }

    static char fromUTF(InputStream in) throws IOException {
        char c = 0;
        if(((c = (char)in.read()) & 0x80) != 0) {
            c &= 0x7f;
            c <<= 6;
            if( ( (c |= ((char)(in.read() & 0x3f))) & 0x1000 ) != 0) {
                c <<= 6;
                c |=  (char)(in.read() & 0x3f);
            }
        }
        return c;
    }

    // This will load all 7, 11 and 16 bit codes.
    // 21, 26 and 31/32 bit codes will be mangled.
    // Excessive byte count for a code will be ignored.
    static char[] fromBytes(byte[] in) {
        StringBuffer buff = new StringBuffer();
        ByteArrayInputStream bin = new ByteArrayInputStream(in);
        try {
            while(bin.available()!=0) buff.append(fromUTF(bin));
        } catch(Exception e) {
            
        }
	return buff.toString().toCharArray();
    }
}