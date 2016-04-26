/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.SOUTHBR.Exception;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FA_Exception extends Exception {

    /**
     * Creates a new instance of <code>FA_Exception</code> without detail message.
     */
    public FA_Exception() {
    }


    /**
     * Constructs an instance of <code>FA_Exception</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FA_Exception(String msg) {
        super(msg);
    }
}
