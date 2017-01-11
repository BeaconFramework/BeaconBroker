/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MDBInt;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public class FederationAgentInfo {

    //<editor-fold defaultstate="collapsed" desc="Variable definitions"> 
    private String SiteName; 
    private String Ip, Port;
    private String site_proxyip,site_proxyport;
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Setter & Getter"> 
    
    public String getSite_proxyip() {
        return site_proxyip;
    }

    public void setSite_proxyip(String site_proxyip) {
        this.site_proxyip = site_proxyip;
    }

    public String getSite_proxyport() {
        return site_proxyport;
    }

    public void setSite_proxyport(String site_proxyport) {
        this.site_proxyport = site_proxyport;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String SiteName) {
        this.SiteName = SiteName;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String Ip) {
        this.Ip = Ip;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String Port) {
        this.Port = Port;
    }

    //</editor-fold>
    
    
    public FederationAgentInfo(String SiteName, String Ip, String Port,String site_proxyip,String site_proxyport) {
        this.SiteName = SiteName;
        this.Ip = Ip;
        this.Port = Port;
        this.site_proxyip=site_proxyip;
        this.site_proxyport=site_proxyport;
    }
    public FederationAgentInfo(String json) throws JSONException {
        JSONObject j=new JSONObject(json);
        this.SiteName = j.getString("SiteName");
        this.Ip = j.getString("Ip");
        this.Port = j.getString("Port");
        this.site_proxyip=j.getString("site_proxyip");
        this.site_proxyport=j.getString("site_proxyport");
    }
}
