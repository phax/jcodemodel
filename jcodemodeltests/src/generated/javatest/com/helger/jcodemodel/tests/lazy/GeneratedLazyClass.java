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
        Integer getSyncInstance = syncInstance;
        if (getSyncInstance == null) {
            synchronized (this)
            {
                if (getSyncInstance == null) {
                    getSyncInstance = JLazyTestGen.inc();
                    syncInstance = getSyncInstance;
                }
            }
        }
        return getSyncInstance;
    }

    private static volatile Integer syncStatic;

    public static Integer getSyncStatic() {
        Integer getSyncStatic = syncStatic;
        if (getSyncStatic == null) {
            synchronized (GeneratedLazyClass.class)
            {
                if (getSyncStatic == null) {
                    getSyncStatic = JLazyTestGen.inc();
                    syncStatic = getSyncStatic;
                }
            }
        }
        return getSyncStatic;
    }

    private volatile Integer aSyncInstance;

    public Integer getASyncInstance() {
        Integer getASyncInstance = aSyncInstance;
        if (getASyncInstance == null) {
            if (getASyncInstance == null) {
                getASyncInstance = JLazyTestGen.inc();
                aSyncInstance = getASyncInstance;
            }
        }
        return getASyncInstance;
    }

    private static volatile Integer aSyncStatic;

    public static Integer getASyncStatic() {
        Integer getASyncStatic = aSyncStatic;
        if (getASyncStatic == null) {
            if (getASyncStatic == null) {
                getASyncStatic = JLazyTestGen.inc();
                aSyncStatic = getASyncStatic;
            }
        }
        return getASyncStatic;
    }
}
