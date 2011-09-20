/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.redhat.ceylon.compiler.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JavacFileManager;
import com.sun.tools.javac.util.ListBuffer;
import com.redhat.ceylon.compiler.codegen.CeylonFileObject;

public class CeyloncFileManager extends JavacFileManager implements StandardJavaFileManager {
    protected String[] sourcePath = new String[0];
    private JarOutputStream jarFile;
    private String currentPackage;

    public void setSourcePath(String sourcePath) {
        StringTokenizer st = new StringTokenizer(sourcePath, File.pathSeparator);
        int size = st.countTokens();
        this.sourcePath = new String[size];
        for (int i = 0; i < size; i++) {
            this.sourcePath[i] = st.nextToken();
        }
    }

    public String[] getSourcePath() {
        return sourcePath;
    }

    public CeyloncFileManager(Context context, boolean register, Charset charset) {
        super(context, register, charset);
    }

    protected JavaFileObject.Kind getKind(String extension) {
        if (extension.equals(JavaFileObject.Kind.CLASS.extension))
            return JavaFileObject.Kind.CLASS;
        else if (extension.equals(JavaFileObject.Kind.SOURCE.extension) || extension.equals(".ceylon"))
            return JavaFileObject.Kind.SOURCE;
        else if (extension.equals(JavaFileObject.Kind.HTML.extension))
            return JavaFileObject.Kind.HTML;
        else
            return JavaFileObject.Kind.OTHER;
    }

    /**
     * Register a Context.Factory to create a JavacFileManager.
     */
    public static void preRegister(final Context context) {
        context.put(JavaFileManager.class, new Context.Factory<JavaFileManager>() {
            public JavaFileManager make() {
                return new CeyloncFileManager(context, true, null);
            }
        });
    }

    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files) {

        Iterable<? extends JavaFileObject> theCollection = super.getJavaFileObjectsFromFiles(files);
        ArrayList<JavaFileObject> result = new ArrayList<JavaFileObject>();
        for (JavaFileObject file : theCollection) {
            if (file.getName().endsWith(".ceylon")) {
                result.add(new CeylonFileObject(file, sourcePath));
            } else {
                result.add(file);
            }
        }
        return result;
    }

    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
        ListBuffer<JavaFileObject> buf = new ListBuffer<JavaFileObject>();
        for (JavaFileObject f : result) {
            if (f.getName().endsWith(".ceylon")) {
                buf.add(new CeylonFileObject(f));
            } else {
                buf.add(f);
            }
        }
        return buf.toList();
    }

    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CeylonFileObject) {
            CeylonFileObject fo = (CeylonFileObject) file;
            return super.inferBinaryName(location, fo.getFile());
        }
        return super.inferBinaryName(location, file);
    }

    protected JavaFileObject getFileForOutput(Location location, final String fileName, FileObject sibling) throws IOException {
        if (sibling instanceof CeylonFileObject) {
            sibling = ((CeylonFileObject) sibling).getFile();
        }
        if(location == StandardLocation.CLASS_OUTPUT){
            openJar();
            return new JavaFileObject(){

                @Override
                public URI toUri() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String getName() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public InputStream openInputStream() throws IOException {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public OutputStream openOutputStream() throws IOException {
                    jarFile.putNextEntry(new ZipEntry(fileName));
                    return new FilterOutputStream(jarFile){
                        @Override
                        public void close() throws IOException {
                            jarFile.closeEntry();
                        }
                    };
                }

                @Override
                public Reader openReader(boolean ignoreEncodingErrors)
                        throws IOException {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors)
                        throws IOException {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public Writer openWriter() throws IOException {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public long getLastModified() {
                    // TODO Auto-generated method stub
                    return 0;
                }

                @Override
                public boolean delete() {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public Kind getKind() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public boolean isNameCompatible(String simpleName, Kind kind) {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public NestingKind getNestingKind() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public Modifier getAccessLevel() {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        }else
            return super.getFileForOutput(location, fileName, sibling);
    }

    @Override
    public void flush() {
        super.flush();
        if(jarFile != null){
            try {
                jarFile.flush();
                jarFile.close();
                jarFile = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void openJar() throws IOException {
        if(jarFile == null){
            jarFile = new JarOutputStream(new FileOutputStream(currentPackage+".jar"));
        }
    }

    public void setPackage(String string) {
        currentPackage = string;
    }
}
