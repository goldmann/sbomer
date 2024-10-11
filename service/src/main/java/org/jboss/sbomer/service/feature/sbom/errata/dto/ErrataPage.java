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
package org.jboss.sbomer.service.feature.sbom.errata.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrataPage<T> {

    @Data
    public static class Page {
        /**
         * Page number.
         */
        @JsonProperty("current_page")
        private int pageNumber;

        /**
         * Total pages provided.
         */
        @JsonProperty("total_pages")
        private int totalPages;

        /**
         * Number of all hits (not only this page).
         */
        @JsonProperty("result_count")
        private int totalHits;
    }

    private Page page;
    private List<T> data = new ArrayList<T>();

}
