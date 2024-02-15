/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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
package org.jboss.sbomer.n.cli.deployment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.jboss.jandex.DotName;

public class IgnorePackagePredicate implements Predicate<DotName> {

    private static final List<String> IGNORED_PACKAGES = Arrays
            .asList("jakarta.crypto.", "jakarta.net.", "jakarta.security.auth.");

    @Override
    public boolean test(DotName dotName) {
        String name = dotName.toString();
        for (String containerPackageName : IGNORED_PACKAGES) {
            if (name.startsWith(containerPackageName)) {
                return true;
            }
        }
        return false;
    }

}
