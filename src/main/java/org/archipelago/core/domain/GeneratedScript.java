package org.archipelago.core.domain;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class GeneratedScript {

    private String content;
    private String scriptName;

    public GeneratedScript() {
        super();
    }

    public GeneratedScript(String scriptName, String content) {
        super();
        this.scriptName = scriptName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public String toString() {
        return String.format("File : %s \n \n%s", scriptName, content);
    }

}
