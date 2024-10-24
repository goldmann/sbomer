/**
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
package org.redhat.sbomer.service.generator;

import org.redhat.sbomer.errors.ApplicationException;

/**
 * High-level interaction with the SBOM generator.
 *
 */
public interface SBOMGenerator {

    /**
     * Generates the SBOM in CycloneDX format for a PNC build identified by the buildId
     *
     * @param buildId PNC build id
     */
    public void generate(String buildId) throws ApplicationException;

}
