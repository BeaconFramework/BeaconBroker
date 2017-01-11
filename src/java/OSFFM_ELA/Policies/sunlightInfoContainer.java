/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OSFFM_ELA.Policies;

/**
 *
 * @author Giuseppe Tricomi
 */
public class sunlightInfoContainer {
private String shapeName;
private String servGroupName;
private String minimumgap;

    public sunlightInfoContainer(String shapeName, String servGroupName, String minimumgap) {
        this.shapeName = shapeName;
        this.servGroupName = servGroupName;
        this.minimumgap = minimumgap;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getServGroupName() {
        return servGroupName;
    }

    public void setServGroupName(String servGroupName) {
        this.servGroupName = servGroupName;
    }

    public String getMinimumgap() {
        return minimumgap;
    }

    public void setMinimumgap(String minimumgap) {
        this.minimumgap = minimumgap;
    }

}
