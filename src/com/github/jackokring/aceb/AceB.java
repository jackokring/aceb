package com.github.jackokring.aceb;


import java.io.*;

import android.app.Activity;

public class AceB implements Runnable {

    /* THE MACHINE STRUCTURES AND PRIMARY CODE */
    char[] m = new char[65536]; //memory
    private char rp, ip, sp, dp;

    private static String[] prims = {
        //must keep the order like this
        "!", //0
        "0=*", //1
        "@", //2
        "BEEP", //3
        "BYE", //4
        "D+", //5
        "XOR", //6
        "EXIT", //7
        "INKEY", //8
        "INURL", //9 For network access
        "MEDIA", //10 For multimedia
        "NATIVE", //11 For Machine code emulation
        "R>", //12
        "VIDOUT", //13 Video update method
        "U*", //14
        "U/", //15 Supplied as an optimization
        ">R", //16
        "(EDIT)", //17
        "(COMP)", //18
        "DROP", //19
        "PICK", //20
        "(SP)",  //21
        "SWAP",  //22
        "(R+)", //23
        "J",    //24
        "(SKIP)",//25
        "(REP)",//26
        "(ALT)" //27
    };

    private void p(int p) {
        char t = m[sp];
        char s = m[sp+1];
        char r = m[rp++];
        int z;
        long x;
        switch(p) {
            //!
            case 0: m[t]=s; sp+=2; break;
            //0=*
            case 1: m[++sp] = s==0?t:0;
                break;
            //@
            case 2: m[sp]=m[t]; break;
            //BEEP
            case 3: sp+=2;
                machine.beep(s, t); break;
            //BYE
            case 4: machine.onBackPressed(); break;
            //D+
            case 5: x=(t<<16)+s;
                sp+=2;
                t = m[sp];
                s = m[sp+1];
                z = (t<<16)+s;
                x+=z;
                m[sp]=(char)(x>>16);
                m[sp+1]=(char)x; break;
            //XOR
            case 6: t ^= s;
                m[++sp] = t; break;
            //EXIT
            case 7: r = m[rp++];    //does not go back to caller of EXIT
                break;
            //INKEY
            case 8: m[--sp]=(char)inkey(); break;
            //INURL
            case 9: m[sp]=(char)machine.inurl(t);
                break;
            //TODO: provide different actions for next 2
            //MEDIA
            case 10: sp++;
                media(t); break;
            //NATIVE
            case 11: sp++; nativ(t); break;
            //R>
            case 12: m[--sp] = m[rp++];
                break;
            //VIDOUT
            case 13: m[sp]=(char)vidout(t);
                break;
            //U*
            case 14: x = t*s;
                m[sp]=(char)(x>>16);
                m[sp+1]=(char)x; break;
            //U/ - returns a double
            case 15: z=m[sp+2];
                    x = (s<<16)+z;
                    if(t==0) x = 0; //raphson convergence with overflow
                        else x /= t;
                    m[sp+2]=(char)x;
                    m[++sp]=(char)(x>>16);
                break;
            //>R
            case 16: m[--rp] = m[sp++]; break;
            //(EDIT)
            case 17: machine.edit(m[sp++]); break;
            //(COMP)
            case 18: z = m[--sp] = m[rp];//params
                m[rp] += (m[r+1]==(char)(-1))?m[z]+1:m[r+1];//return ip after params
                //the m[z]+1 is for counted strings etc.
                r += m[r];//link RUNS>
                break;
            //DROP
            case 19: sp++; break;
            //PICK
            case 20: m[sp] = m[(char)(sp+t)]; break;
            //(SP)
            case 21: m[--sp] = sp; break;//effects (sp+1) as top
            //SWAP
            case 22: m[sp+1] = t; m[sp] = s; break;
            //(R+)
            case 23: m[rp] += t; sp++; break;
            //J
            case 24: m[--sp] = m[rp+2]; break;
            //(SKIP)
            case 25: r+=(m[sp++]==0)?0:1; break;
            //(REP)
            case 26: r-=(m[sp++]==0)?0:2; break;
            //(ALT)
            case 27: r=(m[sp++]==0)?m[r]:(char)(r+1);
        }
        ip=r;
    }

    private void next() {
        int addr = m[ip++];
        if(addr == 0) {
            //primitive
            p(m[ip]); //do
            //exit...
        } else {
            //threading
            m[--rp]=ip; //save addr return
            ip=(char)addr;       //jump
        }
    }

    //TODO: persist before!!
    public void load() {
    	
    }
    
    public void save() {
    	
    }

    String asString(int s) {
        String i = "";
        for(int j = 1; j <= m[s]; j++) {
            i+=(char)m[s+j];
        }
        return(i);
    }

    private long mils;

    long vidout(int mode) {
    	//TODO: remove
        long mil2 = System.currentTimeMillis();
        long mil3 = mil2-mils;
        int a = 0;
        int i, j;
        if(mil3>16||mode>0) {
            //screen needs to be indirect for communication with system
            int x = m[m[screen]];
            int y = m[m[screen]+1];
            int b = m[screen]+2;
            for(j=0;j<y;j++)
                a = j*x;
                for(i=0;i<x;i++) {
                    machine.gc.setCell(i, j, m[b+i+a]);
            }
            mils=mil2;
            return(mil3);
        }
        return(0);
    }

    /* DICTIONARY CONSTRUCTION */
    private int link=0, oldlink=0;    //zero default and -1 dict end

    private void head(String s) {
        //makes a dictionary header
        //use ip as construction index
        int len = s.length();
        link=dp;
        m[dp++]=(char)len;
        for(int i=0;i<len;i++)
            m[dp++]=(char)s.charAt(i);
        //thats put the string there!!
        m[dp++]=(char)oldlink;
        oldlink=link;
    }

    private void prims() {
        //builds the primitive dictionary entries
        int len = prims.length;
        for(int i=0;i<len;i++) {
            head(prims[i]);
            m[dp++]=0;//is prim
            m[dp++]=(char)i;//the prim
        }
    }

    private static String[] words = {
        //only one space works as a seperator
        //needs to be a non (xxx) word first so hide works
        "STOP", "(UP) FIND ?DUP (SKIP) (REDO) EXECUTE (CHKS) LITERAL QUIT LITERAL 0 ! R> DROP STOP",//error checks
        "QUIT", "LITERAL -1 LITERAL 3 .ERR", //user interrupted
        "ABORT", "(UP) DUP (STK) (REP) LITERAL -1 LITERAL 10 .ERR",
        "(STK)", "DROP DEPTH 0>",
        "(UP)", "DEPTH 0< 0= (SKIP) EXIT DEPTH (SKIP) EXIT (UP2) (REP)",//stack underflow fixer
        "(UP2)", "DUP DEPTH 0<",
        "(?TR)", "LITERAL STOP DUP (SCRN) 1- ! I' XOR 0= (SKIP) STOP",//check not value instead of location
        ".TR", "R> DROP R> DUP U. 1- @ (NFA) COUNT 16 MIN TYPE CR LITERAL QUIT DUP (SCRN) 1- ! I XOR (SKIP) (?TR) .TR",
        "(.ERR)", "LITERAL -1 + SWAP (LFA) @ SWAP DUP",
        "(ERR)", "DROP R> DROP",
        ".ERR", "SWAP (SKIP) (ERR) (COMPLETE) @ (SKIP) (CLEAN) LITERAL 32 EMIT LITERAL FORTH LITERAL 4 + @ DUP ROT (.ERR) (REP) DROP COUNT TYPE LITERAL 32 EMIT COUNT TYPE CR .TR", //print error
        "(DP)", "VARABLE <dp>",
        "HERE", "(DP) @",
        "(STATE)", "VARIABLE 0",
        "CONTEXT", "VARIABLE FORTH",
        "CURRENT", "VARIABLE FORTH",
        "BASE", "VARIABLE 10",
        "(PP)", "VARIABLE 0",
        "(TEX2)", "DUP INKEY DUP LITERAL 1 HERE +! HERE DUP @ + ! 2DUP XOR AND",
        "(TEXT)", "LITERAL 0 HERE ! (TEX2) (REP) DROP LITERAL -1 HERE +!",
        "QUERY", "CR LITERAL 0 (TEXT) LITERAL 0 HERE ! (EDIT)",//just clear buffer
        "(WORD)", "(TEXT) HERE @ 0=",
        "WORD", "(WORD) (REP) HERE DUP COUNT 2DUP TYPE + @ DUP EMIT LITERAL 32 = (SKIP) SPACE",
        "(VLIST)", "COUNT 2DUP TYPE LITERAL 32 EMIT + @ DUP",
        "VLIST", "CR CONTEXT @ 1+ @ (VLIST) (REP) DROP",
        "+!", "SWAP OVER @ + SWAP !",
        "UD.", "<# #S #> TYPE",
        "D.", "<# DUP >R DABS #S R> SIGN #> TYPE",
        "DABS", "S>D 0= (SKIP) DNEGATE",
        "(CFA)", "(LFA) 1+",//from NFA
        "(LFA)", "DUP @ + 1+",//from NFA
        "(NFA2)", "1- DUP @ LITERAL 256 MIN + LITERAL 3 PICK -",//max name length
        "(NFA)", "2- DUP (NFA2) (REP) NIP", //loop down till sum equal from a CFA
        "2DUP", "OVER OVER",
        "2DROP", "DROP DROP",
        "2SWAP", ">R ROT ROT R> ROT ROT",
        "2OVER", "LITERAL 4 PICK LITERAL 4 PICK",
        "(CVAL)", "LITERAL -7 + DUP LITERAL -32 + DUP 0< (SKIP) SWAP DROP",
        "CVAL", "1+ DUP >R @ LITERAL -48 + DUP LITERAL 17 < (SKIP) (CVAL) R> SWAP",//quicker base 10
        "(CONV)", "SWAP >R (BASE) @ UD* LITERAL 0 D+ R> CVAL DUP BASE @ DUP (BASE) ! U<",
        "(BASE)", "VARIABLE 1",
        "LATER", "R> R> SWAP >R >R",
        "(-)", "DUP 1+ @ LITERAL 45 - (SKIP) EXIT LATER >R DNEGATE R>",
        "CONVERT", "LITERAL 1 (BASE) ! LITERAL 0 (-) (CONV) (REP)",
        "DUP", "(SP) @",
        "NIP", "SWAP DROP",
        "?DUP", "DUP 0= (SKIP) DUP",
        "ROT", ">R SWAP R> SWAP",
        "OVER", "LITERAL 2 PICK",
        "DEPTH", "(SCRN) LITERAL -257 + (SP) -",
        "ROLL", "DUP 0= LITERAL 7 .ERR DUP DEPTH 1- U< LITERAL 7 .ERR 1- ?DUP (SKIP) EXIT SWAP >R ROLL R> SWAP",
        "COUNT", "DUP 1+ SWAP @",
        "(0LEN)", "2DROP R> DROP",
        "(TYPE)", "1- SWAP DUP @ EMIT 1+ SWAP DUP",
        "TYPE", "DUP (SKIP) (0LEN) (TYPE) (REP) 2DROP",
        "<#", "(DP) @ LITERAL 256 + (PP) ! 0 (PP) @ !", //prepare the picture pointer
        "#>", "2DROP (PP) @ COUNT",
        "SIGN", "LITERAL 0 2DUP D+ NIP 13 U* DROP 32 + HOLD",
        ".", "S>D D.",
        "U.", "LITERAL 0 D.",
        "(#S)", "# 2DUP OR",
        "#S", "(#S) (REP)",
        "#", "BASE @ UD/MOD DIGIT HOLD",
        "DIGIT", "LITERAL 48 + DUP 58 < 1+ LITERAL 6 * +",
        "(SCRN)", "LITERAL -1 @",
        "(POS)", "VARIABLE 0",
        "CLS", "(SCRN) DUP @ SWAP 1+ @ * SPACES LITERAL 0 (POS) !", //emit spaces
        "HOLD", "(PP) @ DUP @ >R ! LITERAL -1 (PP) +! R> LITERAL 1 + (PP) @ !",
        "SPACE", "LITERAL 32 EMIT",
        "(SPC)", "1- SPACE DUP",
        "SPACES", "DUP DUP (SKIP) (0LEN) (SPC) (REP) 2DROP",
        "CR", "(POS) @ (SCRN) MOD (SCRN) SWAP - DUP SPACES", //done by (POS) mod
        "AT", "SWAP (SCRN) @ * + DUP (SCRN) DUP @ SWAP 1+ @ * U< 0= LITERAL 9 .ERR (POS) !",
        "GET", "(POS) LITERAL 0 (SCRN) @ UD/MOD NIP",
        "ABS", "S>D 0= (SKIP) NEGATE",
        "0=", "LITERAL -1 0=*",
        "0<", "S>D NIP",
        "0>", "1- 0< NOT",
        "=", "XOR 0=",
        ">", "- 0>",
        "<", "- 0<",
        "U<", "LITERAL 0 SWAP OVER D- NIP",
        "D<", "D- NIP S>D NIP",
        "S>D", "DUP LITERAL 0 2DUP D+ 0= 0= NIP", // n -> d
        "(*-)", "S>D >R ABS SWAP S>D >R ABS SWAP R> R> XOR",
        "/MOD", "OVER >R / DUP R> SWAP - SWAP",
        "/", "(*-) >R LITERAL 0 SWAP U/ DROP R> 0= (SKIP) NEGATE",
        "MOD", "/MOD DROP",
        "*", "U* DROP",
        "*/", ">R M* R> M/ DROP",
        "*/MOD", ">R 2DUP I */ DUP R> SWAP >R M* D- DROP R>",
        "M*", "(*-) >R U* R> 0= (SKIP) DNEGATE", // n1 n2 -> d=n1*n2
        "M/", ">R S>D >R DABS R> R> SWAP >R S>D R> XOR >R U/ R> 0= (SKIP) DNEGATE",  // d1 n1 -> d=d1/n1
        "UD*", ">R SWAP I U* ROT R> U* DROP +", // ud u -> ud
        "UD/", "?DUP (ALT) U/ DUP 1+ >R DLITERAL 1 0 R> U/ DROP DUP >R UD* >R >R DUP R> UD* I U/ D- R> R> SWAP UD*/ NIP LITERAL 0",
        "UD/MOD", ">R OVER >R I' U/ OVER R> SWAP R> * -", // ud u -> udq ur
        "UD*/", ">R LITERAL 0 SWAP T* DROP R> T/",
        "D*", ">R LITERAL 3 PICK >R UD* LITERAL 0 R> R> U* DROP D+",
        "D/", "S>D LITERAL 4 PICK S>D XOR >R >R DABS R> ABS UD/ R> 0= (SKIP) DNEGATE",
        "TD-", ">R SWAP >R SWAP >R LITERAL 0 SWAP OVER D- DUP R> R> R> LITERAL 0 D- D+",
        "T*", "SWAP >R DUP >R DUP ROT U* >R >R U* LITERAL 0 R> R> D+ LITERAL 0 R> R> U* D+",
        "T/", "DUP >R UD/MOD SWAP DROP >R SWAP R> R> U/ DROP SWAP",
        "(D/)", "ROT DROP UD/",
        "TD/", "?DUP (ALT) T/ LITERAL 3 PICK (ALT) (D/) DUP 1+ DLITERAL 1 0 ROT U/ DROP DUP >R D* >R >R 2DUP DUP LITERAL 0 R> R> 2DUP >R >R UD*/ D- R> I UD*/ TD- R> R> SWAP >R T* R> T/ ROT DROP",
        "NEGATE", "NOT 1+",
        "+", ">R (R+) R>",
        "-", "NEGATE +",
        "1+", "LITERAL 1 +",
        "2+", "LITERAL 2 +",
        "1-", "LITERAL -1 +",
        "2-", "LITERAL -2 +",
        "OR", "NOT SWAP NOT AND NOT",
        "AND", "2DUP XOR >R LITERAL 0 SWAP LITERAL 0 D+ R> LITERAL 0 D- LITERAL 2 U/ DROP",
        "NOT", "LITERAL -1 XOR",
        "D-", "DNEGATE D+",
        "DNEGATE", "NOT SWAP NOT SWAP LITERAL 1 LITERAL 0 D+",
        "MAX", "2DUP > (SKIP) NIP",
        "MIN", "2DUP < (SKIP) NIP",
        "NOT", "LITERAL -1 XOR",
        "DECIMAL", "LITERAL 10 BASE !",
        "CREATE", "LITERAL 32 WORD DUP @ LITERAL 33 - U< 0= LITERAL 6 .ERR DUP CAPS DUP @ 1+ ALLOT CURRENT @ 1+ DUP @ , !",
        ",", "HERE ! LITERAL 1 ALLOT",
        "ALLOT", "(DP) +!",
        "DEFINITIONS", "CONTEXT @ CURRENT !",
        "I", "I'",
        "I'", "J",
        "REDEFINE", "FIND CURRENT @ 1+ @ (CFA) OVER ! LITERAL ; SWAP 1+ !", //a vectored hack
        "FORGET", "FIND (NFA) (CLN)",//vocabs??
        "(MATCH)", "1- >R >R COUNT ROT COUNT ROT - R> OR R> DUP",
        "MATCH", "LITERAL 0 OVER @ (MATCH) (REP) DROP NIP NIP 0=",
        "(NO')", "DUP R> DROP",
        "(FIND)", "DUP (SKIP) (NO') 2DUP MATCH 0= >R (LFA) @ R>",
        "FIND", "LITERAL 32 WORD DUP CAPS CONTEXT @ 1+ @ (FIND) (REP) NIP ?DUP (SKIP) (NUM')",
        "(F')", "2DROP DROP LITERAL 0",//no float support - not found
        "(D')", "DROP R> DROP LITERAL DLITERAL",//a double
        "(LIT')", "2DROP R> DROP LITERAL LITERAL",//a single literal
        "(NUM')", "LITERAL 0 LITERAL 0 HERE CONVERT DUP @ LITERAL 32 XOR (SKIP) (LIT') DUP @ LITERAL 46 XOR OVER 1+ @ LITERAL 32 XOR + (SKIP) (D') (F')",
        "(CLEAN)", "CURRENT @ 1+ @ DUP (DP) ! (LFA) @ CURRENT @ 1+ ! LITERAL 0 (STATE) ! LITERAL 1 (COMPLETE) !",//cleans the last created
        "(CAP)", "DUP LITERAL -97 + LITERAL 27 U< (SKIP) EXIT LITERAL -32 +",
        "(CAPS)", "2DUP + DUP @ (CAP) SWAP ! 1- DUP",
        "CAPS", "COUNT DUP (SKIP) (0LEN) (CAPS) (REP) 2DROP",//string to capitals
        "(SCR)", "1+ (SCRN) @ DUP ROT * SWAP ROT 0 AT TYPE 2DUP -",
        "(SCRL)", "(SCRN) 1+ @ LITERAL 0 (SCR) (REP) 2DROP (POS) @ (SCRN) @ 1- SPACES LITERAL 32 (SCRN) (POS) @ + ! (POS) !",
        "EMIT", "(SCRN) 2+ (POS) + ! LITERAL 1 (POS) +! (POS) @ (SCRN) DUP 1+ @ SWAP @ * U< (SKIP) (SCRL) LITERAL 0 VIDOUT",
        "(REDO)", "DROP R> DROP RETYPE",
        "RETYPE", "LITERAL 1 HERE +! HERE (EDIT)",//not blocking so be careful of evaluate background
        "(0COMP)", "@ LITERAL (COMP) XOR",
        "(NUM)", "DROP R> R> 2DROP",
        "(EXEC)", "DUP DUP >R LITERAL LITERAL XOR SWAP R> LITERAL DLITERAL XOR * (SKIP) (NUM) DUP (0COMP) 0= LITERAL 4 .ERR R> DROP >R",
        "(RUN)", "R> DROP DUP , LITERAL 3 + >R",//where run behaviour is!!
        "EXECUTE", "(STATE) @ (SKIP) (EXEC) DUP (0COMP) (SKIP) (RUN) ,",
        "(CHKS)", "HERE LITERAL 259 + (SP) - 0< LITERAL 1 .ERR DEPTH 0< LITERAL 2 .ERR",//error 1 and 2
        "(;)", "(CHKS)",
        "FAST", "LITERAL EXIT LITERAL (;) !",//override mem checks
        "SLOW", "LITERAL (CHKS) LITERAL (;) !",
        "(IS)", "CURRENT @ 1+ @ (CFA) @ XOR",
        "(COMPLETE)", "VARIABLE 1",
        ":", "(COMPLETE) @ 0= LITERAL 12 .ERR CREATE ] LITERAL 0 (COMPLETE) !",
        ";", "(COMP) 8 0 [ LITERAL 1 (COMPLETE) ! RUNS> DROP R> DROP (;)",//COMPILER link>> len^^ ...
        "CONSTANT", "(DEF) 3 , DOES> R> @",
        "LITERAL", "(COMP) 4 1 , RUNS> @",
        "DLITERAL", "(COMP) 5 2 , , RUNS> DUP 1+ @ SWAP @",
        "VARIABLE", "(DEF) 5 LITERAL 1 ALLOT DOES> R>",
        "(DEF)", "(COMP) 3 1 RUNS> CREATE R> DUP DUP @ + , 1+ >R",//the does bit
        "DEFINER", ": LITERAL (DEF) , HERE DUP ,",//jumps to compile behaviour
        "COMPILER", ": LITERAL (COMP) , HERE DUP , SWAP ,",
        "(<!)", "SWAP >R 0= LITERAL 5 .ERR HERE I - R> !",//resolve
        "(>!)", "SWAP >R 0= LITERAL 5 .ERR R> HERE - ,",
        "DOES>", "(COMP) 10 1 LITERAL (DEF) (IS) (<!) LITERAL R> , RUNS> DROP R> DROP",
        "RUNS>", "(COMP) 7 0 LITERAL (COMP) (IS) (<!) RUNS> DROP R> DROP",//exits compile behaviour
        "VOCABULARY", "(DEF) 7 CURRENT @ 1+ @ , DOES> R> 1- CONTEXT !",//store as per use
        "ASCII", "LITERAL 32 WORD HERE 1+ @",//this word has no compile behaviour!!
        "[", "(COMP) 10 0 LITERAL -1 ALLOT LITERAL 0 (STATE) ! RUNS> LITERAL 0 (STATE) !",
        "]", "LITERAL 1 (STATE) !",
        ".\"", "(COMP) 9 -1 LITERAL 34 WORD HERE @ 1+ ALLOT RUNS> COUNT TYPE",
        "(", "(COMP) 9 -1 LITERAL 41 WORD HERE @ 1+ ALLOT RUNS> DROP",
        "CLASS", "(DEF) 7 CURRENT @ 1+ @ , DOES> R> @ NATIVE",
        "(BRA)", "OVER 1- @ XOR",//get word compiler word branching to not!
        "IF", "(COMP) 6 1 HERE DUP , RUNS> @ 0=* (R+)",
        "ELSE", "(COMP) 10 1 LITERAL IF (BRA) (<!) HERE DUP , RUNS> @ (R+)",
        "THEN", "(COMP) 11 0 LITERAL IF (BRA) LITERAL ELSE (BRA) * (<!) RUNS> DROP",
        "BEGIN", "(COMP) 4 0 HERE RUNS> DROP",
        "WHILE", "(COMP) 8 1 LITERAL BEGIN (BRA) (>!) HERE 1- RUNS> @ 0=* (R+)",//works as AGAIN
        "REPEAT", "(COMP) 12 1 DUP @ LITERAL WHILE (BRA) (>!) LITERAL 0 (<!) RUNS> @ (R+) (;)",//error test,
        "UNTIL", "(COMP) 7 1 LITERAL BEGIN (BRA) (>!) RUNS> @ 0=* (R+) (;)",//error test
        "ON>", "(R+) R> @ >R",
        "DO", "",
        "LOOP", "",//error test
        "+LOOP", "",//error test
        "LEAVE", "",
        "EDIT", "",
        "LIST", "",
        "ALL", "VOCABULARY <all>",
        "FORTH", "VOCABULARY <link>", //must be the last word, as it's an error thing
    };

    String[] split(String s) {
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

    private void words(boolean b) {
        //builds the dictionary entries
        int len = words.length;
        for(int i=0;i<len;i+=2) {
            head(words[i]);
            body(words[i+1],b);
        }
    }

    private void body(String s, boolean addr) {
        //if addr is true the do search and fill with addresses
        //else just one cell per word
        //compile a body from the pseudo forth
        String[] cells = split(s+" EXIT ;");//the default end of word
        //such that no error checking done
        if(addr) {
            for(int i=0;i<cells.length;i++) {
                //compile the cell
                if(!findCompile(cells[i])) doSpecial(cells[i]);
                //numbers are specials too.
            }
        } else dp+=cells.length; //jump over compilation cells
    }

    private int masterLink=0;//the compilation entry pointer

    private boolean findCompile(String s) {
        int j = masterLink;
        String i;
        while(!((i=asString(j)).equals(s))&&j!=0) {
            j+=i.length()+1; //LFA
            j=m[j];//link
        }
        if(j==0) return false;
        else {
            j+=i.length()+2; //CFA
            m[dp++]=(char)j;
            return true;
        }
    }

    private char all;

    private void doSpecial(String s) {
        //compile special
        try {
            m[dp]=(char)Integer.parseInt(s);//number
        } catch(Exception e) {
            //not number
            if(s.equals("<link>")) {
                m[dp] = (char)masterLink;//set dictionary entry point
            } else if(s.equals("<dp>")) {
                m[dp] = maxDp;
            } else if(s.equals("<all>")) {
                all = dp;//set for later
            } else ((Video)machine).setCurrent(new MyDialog("DEBUG: "+s));
        }
        dp++;
    }

    /* SANITY */
    private void hide() {
        int j = masterLink;
        int k = 0;//botch for no prvious
        String i;
        m[all] = (char)masterLink;//chain to top of forth
        while(j!=0) {
            i = asString(j);
            while(i.charAt(0)!='(' || i.length()==1) {
                j+=i.length()+1; //LFA
                k = j; //previous LFA
                j=m[j];//link is NFA
            }
            m[k] = m[j+i.length()+1];//set previous to..skip
            m[j+i.length()+1] = m[all];//build chain
            m[all] = (char)j;//add to vocab
        }
    }

    private static String[] errors = {
        //Errors are in reverse order to be found by decreasing loop
        "No Listing", //14 - LIST EDIT
        "Word Not Known", //13 - FORGET LIST EDIT REDEFINE
        "Incomplete Word", //12
        "Word Instabilty", //11 - FORGET REDEFINE
        "Aborted", //10 - ABORT
        "Off Screen", //9 - AT
        "Number Overflow", //8 - floating
        "Stack Index", //7 - ROLL
        "Name Too Long", //6 - CREATE
        "Control Structure", //5
        "Compiler Use Only", //4
        "AceB FORTH System Online NO", //3 - BRK
        "Stack Empty", //2
        "Out of Memory", //1
        "ERROR" //0 - may skip over
    };

    private void errors() {
        int z = dp++;//a blank cell for string list
        int len = errors.length;
        for(int i=0;i<len;i++) {
            head(errors[i]);
        }
        m[z]=(char)link;//access via cell following ERROR pfa
    }

    /* MAIN BUILDER */
    private char maxDp = 0;

    private void dict() {
        //make the dictionary
        dp = 1;
        prims();
        words(false);
        masterLink=link;//has the main search entry point
        errors();
        link = 0;
        oldlink = 0;
        maxDp = dp;
        dp = 1;
        prims();
        words(true);//second pass
        //have main dictionary intact
        errors();//do error strings just after ERROR variable
        //note that errors are chained in reverse order
        //ip is HERE
        hide();//hide minor words
        dp = 0;
        findCompile("QUIT");//entry vector
        alloc();//allocate and fill specials etc
    }

    final char screen = (char)-1;//DisplayTerminal structure pointer

    void alloc() {
        //default
        //build screen structure
        m[screen] = (char)(-1024 - 3);
        m[m[screen]] = 32;
        m[m[screen]+1] = 32;
        //set return stack
        rp = (char)(m[screen]);//just under screen as pre decrement
        //set data stack
        sp = (char)(m[screen]-256);//under a 256 depth return stack
        //set entry point
        ip = 0;
        //return execution context string
        //screen,rp,sp,dp
    }

    /* APPLICATION INTERFACE */

    public Desktop machine;
    Thread ref;

    public AceB(Desktop mach) {
        machine = mach;
        (ref = new Thread(this)).start();
    }

    public boolean destroy;
    public boolean p;//internal application pause
    public boolean pause;
    public boolean exited;

    public void run() {
    	p = true;
        pause = false;
        exited = false;
        dict();
        destroy = false;
        while(!destroy) {
            if(p || pause) Thread.yield();
            next();
        }
        exited = true;
    }

}