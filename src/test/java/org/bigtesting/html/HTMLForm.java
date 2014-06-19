/*
 * Copyright (C) 2014 BigTesting.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bigtesting.html;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Luis Antunes
 */
public class HTMLForm implements Renderable {
    
    private String action;
    private boolean multipart;
    private final List<Input> inputs = new ArrayList<Input>();
    
    public HTMLForm withAction(String action) {
        this.action = action;
        return this;
    }
    
    public HTMLForm withMultiPart() {
        this.multipart = true;
        return this;
    }
    
    public HTMLForm withFileInput() {
        this.inputs.add(new FileInput());
        withMultiPart();
        return this;
    }
    
    public HTMLForm withCheckboxInput(String name, boolean value, String label) {
        this.inputs.add(new CheckboxInput(name, value, label));
        return this;
    }
    
    public HTMLForm withTextInput(String id, String name, String value) {
        this.inputs.add(new TextInput(id, name, value));
        return this;
    }
    
    public HTMLForm withTextInput(String id, String name) {
        this.inputs.add(new TextInput(id, name, null));
        return this;
    }
    
    public HTMLForm withTextInput(String idAndName) {
        this.inputs.add(new TextInput(idAndName, idAndName, null));
        return this;
    }
    
    public HTMLForm withPasswordInput(String id, String name, String value) {
        this.inputs.add(new PasswordInput(id, name, value));
        return this;
    }
    
    public HTMLForm withPasswordInput(String id, String name) {
        this.inputs.add(new PasswordInput(id, name, null));
        return this;
    }
    
    public HTMLForm withPasswordInput(String idAndName) {
        this.inputs.add(new PasswordInput(idAndName, idAndName, null));
        return this;
    }
    
    public void render(PrintWriter out) {

        try {
            out.println("<html>");
            out.println("<body>");
            
            out.print("<form name=\"test-form\" " +
                    "action=\"" + action + "\" method=\"post\"");
            if (multipart) {
                out.println(" enctype=\"multipart/form-data\">");
            } else {
                out.println(">");
            }
    
            inputs.add(new SubmitInput());
            for (Input input : inputs) {
                input.print(out);
            }
            
            out.println("</form>");
            
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private abstract class Input {
        
        String id;
        String name;
        String type;
        String value;
        String label;
        
        void print(PrintWriter out) {
            out.print("<input ");
            if (id != null) {
                out.print("id=\"" + id + "\"");
            }
            out.print(" name=\"" + name + "\"");
            out.print(" type=\"" + type + "\"");
            if (value != null) {
                
                out.print(" value=\"" + value + "\"");
            }
            out.print(" />");
            if (label != null) {
                out.print(label);
            }
            out.println("<br/>");
        }
    }
    
    private class SubmitInput extends Input {
        
        public SubmitInput() {
            this.id = "submit";
            this.value = "submit";
            this.type = "submit";
        }
    }
    
    private class FileInput extends Input {
        
        public FileInput() {
            this.id = "file";
            this.name = "file";
            this.type = "file";
        }
    }
    
    private class CheckboxInput extends Input {
        
        public CheckboxInput(String name, boolean value, String label) {
            this.name = name;
            this.type = "checkbox";
            this.value = String.valueOf(value);
            this.label = label;
        }
    }
    
    private class TextInput extends Input {
        
        public TextInput(String id, String name, String value) {
            this.id = id;
            this.name = name;
            this.type = "text";
            this.value = value;
        }
    }
    
    private class PasswordInput extends Input {
        
        public PasswordInput(String id, String name, String value) {
            this.id = id;
            this.name = name;
            this.type = "password";
            this.value = value;
        }
    }
}
