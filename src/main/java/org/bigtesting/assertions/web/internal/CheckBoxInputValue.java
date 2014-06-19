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
package org.bigtesting.assertions.web.internal;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

/**
 * 
 * @author Luis Antunes
 */
public class CheckBoxInputValue extends FormInputValue {
    
    private final boolean checked;
    
    public CheckBoxInputValue(String inputName, boolean checked) {
        super(inputName);
        this.checked = checked;
    }
    
    public void handleInput(HtmlInput input) {
        ((HtmlCheckBoxInput)input).setChecked(checked);
    }
}