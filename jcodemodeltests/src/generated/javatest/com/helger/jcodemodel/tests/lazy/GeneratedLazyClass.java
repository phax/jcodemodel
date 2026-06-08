/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.helger.jcodemodel.tests.lazy;

import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class GeneratedLazyClass {

    public int sum() {
        return (((getSyncInstance()+ getSyncStatic())+ getASyncInstance())+ getASyncStatic());
    }

    private volatile Integer syncInstance;

    public Integer getSyncInstance() {
        Integer ret = this.syncInstance;
        if (ret == null) {
            synchronized (this)
            {
                ret = this.syncInstance;
                if (ret == null) {
                    ret = JLazyTestGen.inc();
                    this.syncInstance = ret;
                }
            }
        }
        return ret;
    }

    private static volatile Integer syncStatic;

    public static Integer getSyncStatic() {
        Integer ret = GeneratedLazyClass.syncStatic;
        if (ret == null) {
            synchronized (GeneratedLazyClass.class)
            {
                ret = GeneratedLazyClass.syncStatic;
                if (ret == null) {
                    ret = JLazyTestGen.inc();
                    GeneratedLazyClass.syncStatic = ret;
                }
            }
        }
        return ret;
    }

    private volatile Integer aSyncInstance;

    public Integer getASyncInstance() {
        Integer ret = this.aSyncInstance;
        if (ret == null) {
            ret = this.aSyncInstance;
            if (ret == null) {
                ret = JLazyTestGen.inc();
                this.aSyncInstance = ret;
            }
        }
        return ret;
    }

    private static volatile Integer aSyncStatic;

    public static Integer getASyncStatic() {
        Integer ret = GeneratedLazyClass.aSyncStatic;
        if (ret == null) {
            ret = GeneratedLazyClass.aSyncStatic;
            if (ret == null) {
                ret = JLazyTestGen.inc();
                GeneratedLazyClass.aSyncStatic = ret;
            }
        }
        return ret;
    }
}
