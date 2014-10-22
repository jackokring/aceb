package com.github.jackokring.aceb;

public class Tester implements Machine {

    /* THE MACHINE STRUCTURES AND PRIMARY CODE */
    char[] m = new char[65536]; //memory

    protected void next() {
        //TODO: all the tests
    }
    
    public synchronized void load(char[] f) {
    	end();
    	m = f.clone();
    	reset(false);
    }
    
    public synchronized char[] save() {
    	end();
    	char[] f = m.clone();
    	reset(false);
    	return f;
    }

    public String asString(int s) {
        String i = "";
        for(int j = 1; j <= m[s]; j++) {
            i+=(char)m[s+j];
        }
        return(i);
    }

    public String[] split(String s) {
        int i = 0, j = 0;
        String old = "\n\t";//replace newline and tab
        for(i = 0;i < old.length();i++)
            s = s.replace((char)old.charAt(i), (char)32);
        i = 0;
        while((j = s.indexOf(32, j)+1)!=-1)
            i++;
        String[] rtn;
        if(i == 0) {
            rtn = new String[1];
            rtn[0] = s;
        } else {
            rtn = new String[i+1];
            j = 0; i = 0; int k = 0;
            while((j = s.indexOf(32, j))!=-1) {
                if(j==-1) j=s.length();
                rtn[k] = s.substring(i, j);
                k++;
                i=(j+=1);
            }
        }
        return rtn;
    }

    /* MAIN BUILDER */

    protected void dict() {
        
    }

    protected void alloc() { 
        
    }

    /* APPLICATION INTERFACE */

    OSAdapter machine;
    Thread ref;

    public Tester(OSAdapter mach) {
        machine = mach;
    }

    boolean destroy = false;//botch for file initialisation
    boolean safe = true;

    public void run() {
        while(!destroy) {
        	try {
        		next();
        	} catch(Exception e) {
        		//error in system (not in language)
        		alloc();//warm start
        	}
        }
        safe = true;
    }
    
    public synchronized void reset(boolean build) {
    	end();
    	safe = false;
    	if(build) dict();//cold start
    	(ref = new Thread(this)).start();
    }

	@Override
	public synchronized void end() {
		if(destroy == true) return;
		destroy = true;
		while(!safe) Thread.yield();//lock
	}

	@Override
	public SearchItem[] search() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resX(char x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resY(char y) {
		// TODO Auto-generated method stub
		
	}
}