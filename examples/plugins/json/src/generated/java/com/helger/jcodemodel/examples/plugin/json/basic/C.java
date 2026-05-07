/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 * 						http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 */
package com.helger.jcodemodel.examples.plugin.json.basic;

public class C {
    private B redirect;

    /**
     * set the {@link #redirect}
     */
    public void setRedirect(B redirect) {
        this.redirect = redirect;
    }

    /**
     * @return the {@link #redirect}
     */
    public B getRedirect() {
        return redirect;
    }

    public void setB(int b) {
        redirect.setB(b);
    }

    public int getB() {
        return redirect.getB();
    }
}
