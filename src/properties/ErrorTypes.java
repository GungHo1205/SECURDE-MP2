/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

/**
 *
 * @author J
 */
public enum ErrorTypes {
    ERROR404("ERROR 404: Hello"),
    ERROR500("ERROR 500: Henlo");

    private String label;
    
    private ErrorTypes(String label) {
        this.label = label;
    }

    /**
     * @return the label
     */

public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
