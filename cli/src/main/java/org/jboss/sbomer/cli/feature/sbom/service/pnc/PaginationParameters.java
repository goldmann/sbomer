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
package org.jboss.sbomer.cli.feature.sbom.service.pnc;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.pnc.rest.configuration.Constants;
import org.jboss.pnc.rest.configuration.SwaggerConstants;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class PaginationParameters {

    /**
     * {@value SwaggerConstants#PAGE_INDEX_DESCRIPTION}
     */
    @Parameter(description = SwaggerConstants.PAGE_INDEX_DESCRIPTION)
    @QueryParam(value = SwaggerConstants.PAGE_INDEX_QUERY_PARAM)
    @DefaultValue(value = SwaggerConstants.PAGE_INDEX_DEFAULT_VALUE)
    @PositiveOrZero
    protected int pageIndex;

    /**
     * {@value SwaggerConstants#PAGE_SIZE_DESCRIPTION}
     */
    @Parameter(description = SwaggerConstants.PAGE_SIZE_DESCRIPTION)
    @QueryParam(value = SwaggerConstants.PAGE_SIZE_QUERY_PARAM)
    @DefaultValue(value = SwaggerConstants.PAGE_SIZE_DEFAULT_VALUE)
    @Positive
    @Max(value = Constants.MAX_PAGE_SIZE)
    protected int pageSize;

}
