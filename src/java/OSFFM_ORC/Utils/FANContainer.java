/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OSFFM_ORC.Utils;

import API.SOUTHBR.FA_client4Network;
import MDBInt.FederationAgentInfo;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FANContainer {
    private FA_client4Network fan;
    private String body;
    String fahIP,fahPORT;
    String tenantid;

    public FA_client4Network getFan() {
        return fan;
    }

    public void setFan(FA_client4Network fan) {
        this.fan = fan;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFahIP() {
        return fahIP;
    }

    public void setFahIP(String fahIP) {
        this.fahIP = fahIP;
    }

    public String getFahPORT() {
        return fahPORT;
    }

    public void setFahPORT(String fahPORT) {
        this.fahPORT = fahPORT;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public FANContainer(FA_client4Network fan, String body, String fahIP, String fahPORT, String tenantid) {
        this.fan = fan;
        this.body = body;
        this.fahIP = fahIP;
        this.fahPORT = fahPORT;
        this.tenantid = tenantid;
    }
}
