/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted
 * by the author tags.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.ceylon.cmr.api;

import com.redhat.ceylon.common.ModuleUtil;

/**
 * Module info.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class ModuleDependencyInfo implements Comparable<ModuleDependencyInfo> {
    private String namespace;
    private String name;
    private String version;
    private boolean optional;
    private boolean shared;

    public ModuleDependencyInfo(String namespace, String name, String version, boolean optional, boolean shared) {
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        this.optional = optional;
        this.shared = shared;
        assert(ModuleUtil.validNamespace(namespace));
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isExport() {
        return shared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleDependencyInfo that = (ModuleDependencyInfo) o;
        return that.name.equals(name)
                && that.version.equals(version)
                && that.shared == shared
                && that.optional == optional;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(ModuleDependencyInfo that) {
        int res = name.compareTo(that.name);
        if (res == 0) {
            res = version.compareTo(that.version);
            if (res == 0) {
                res = Boolean.compare(shared, that.shared);
                if (res == 0) {
                    res = Boolean.compare(optional, that.optional);
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return ((shared) ? "shared " : "") +
                ((optional) ? "optional " : "") +
                ((namespace != null) ? namespace + ":" : "") +
                getModuleName();
    }

    public String getModuleName() {
        return name + "/" + version;
    }
}
