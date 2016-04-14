/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API.EASTAPI.utils_containers;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giuseppe Tricomi
 */
public class LinkInfoContainers {
    private String type;
    private String token;
    private String command;
    private JSONArray fa_endpoints;
    private JSONArray network_tables;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public JSONArray getFa_endpoints() {
        return fa_endpoints;
    }

    public void setFa_endpoints(JSONArray fa_endpoints) {
        this.fa_endpoints = fa_endpoints;
    }

    public JSONArray getNetwork_tables() {
        return network_tables;
    }

    public void setNetwork_tables(JSONArray network_tables) {
        this.network_tables = network_tables;
    }
    
    
}
