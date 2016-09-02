/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OSFFM_ELA;

/**
 *
 * @author Giuseppe Tricomi
 */
public class ElasticityPolicyException extends Exception {

    /**
     * Creates a new instance of <code>ElasticityPolicyException</code> without detail message.
     */
    public ElasticityPolicyException() {
    }


    /**
     * Constructs an instance of <code>ElasticityPolicyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ElasticityPolicyException(String msg) {
        super(msg);
    }
}
