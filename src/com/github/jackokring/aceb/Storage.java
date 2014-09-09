package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */

import java.io.*;
public interface Storage {
    public int openURL(String s) throws IOException;
    public void play(String s) throws IOException;
}
