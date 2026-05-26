/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 				http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.helger.jcodemodel.tests.textblocks;

public class TextBlocksExample {
    public static final String EMPTY = """
    """;
    public static final String ONE_LINE = """
    a""";
    public static final String TWO_LINES = """
    a
    b""";
    public static final String TWO_DOUBLE_DQUOTES_LINES = """
    ""
    "\"""";
    public static final String TWO_TRIPLE_DQUOTES_LINES = """
    ""\"
    ""\"""";
    public static final String FIVE_DQUOTES = """
    ""\""\"""";
    public static final String TWO_FIVE_DQUOTES_LINES = """
    ""\"""
    ""\""\"""";
    public static final String ONE_LINE_ENDSPACE = """
    a \040 """;
    public static final String ONE_LINE_ENDTAB = """
    a	\011 """;
    public static final String TWO_LINES_ENDTAB = """
    a	\011
    b	\011 """;
}
