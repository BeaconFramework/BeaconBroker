/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OSFFM_ORC.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import org.jclouds.openstack.neutron.v2.domain.Port;

/**
 *
 * @author Giuseppe Tricomi
 */
public class ElasticitysuppContainer {
private ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> info;

    public ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> getInfo() {
        return info;
    }

    public ElasticitysuppContainer(ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> info, String firstCloudId) {
        this.info = info;
        this.firstCloudId = firstCloudId;
    }

    public void setInfo(ArrayList<ArrayList<HashMap<String, ArrayList<Port>>>> info) {
        this.info = info;
    }

    public String getFirstCloudId() {
        return firstCloudId;
    }

    public void setFirstCloudId(String firstCloudId) {
        this.firstCloudId = firstCloudId;
    }
private String firstCloudId;
}
