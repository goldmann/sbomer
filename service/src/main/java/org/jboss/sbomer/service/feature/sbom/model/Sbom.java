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
package org.jboss.sbomer.service.feature.sbom.model;

import static org.jboss.sbomer.core.features.sbom.utils.SbomUtils.schemaVersion;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.cyclonedx.BomGeneratorFactory;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;
import org.cyclonedx.parsers.JsonParser;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jboss.sbomer.core.features.sbom.config.runtime.Config;
import org.jboss.sbomer.core.features.sbom.utils.SbomUtils;
import org.jboss.sbomer.core.features.sbom.validation.CycloneDxBom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DynamicUpdate
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
@ToString
@Table(
        name = "sbom",
        indexes = { @Index(name = "idx_sbom_buildid", columnList = "build_id"),
                @Index(name = "idx_sbom_rootpurl", columnList = "root_purl") })
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Sbom extends PanacheEntityBase {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "build_id", nullable = false, updatable = false)
    @NotBlank(message = "Build identifier missing")
    private String buildId;

    @Column(name = "root_purl")
    private String rootPurl;

    @Column(name = "creation_time", nullable = false, updatable = false)
    private Instant creationTime;

    @Type(type = JsonTypes.JSON_BIN)
    @Column(name = "sbom", columnDefinition = JsonTypes.JSON_BIN)
    @CycloneDxBom
    @ToString.Exclude
    private JsonNode sbom;

    @Type(type = JsonTypes.JSON_BIN)
    @Column(name = "config", columnDefinition = JsonTypes.JSON_BIN)
    @ToString.Exclude
    private JsonNode config;

    @JsonIgnore
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "status_msg")
    private String statusMessage;

    /**
     * Returns the generated SBOM as the CycloneDX {@link Bom} object.
     *
     * In case the SBOM is not available yet, returns <code>null</code>.
     *
     * @return The {@link Bom} object representing the SBOM.
     */
    @JsonIgnore
    public Bom getCycloneDxBom() {
        if (sbom == null) {
            return null;
        }

        Bom bom = SbomUtils.fromJsonNode(sbom);

        if (bom == null) {
            try {
                BomJsonGenerator generator = BomGeneratorFactory.createJson(schemaVersion(), new Bom());
                bom = new JsonParser().parse(generator.toJsonNode().textValue().getBytes());
            } catch (ParseException e) {
            }
        }

        return bom;

    }

    /**
     * Returns the config {@link Config}.
     *
     * In case the runtime config is not available or parsable, returns <code>null</code>.
     *
     * @return The {@link Config} object
     */
    @JsonIgnore
    public Config getConfig() {
        return SbomUtils.fromJsonConfig(config);
    }

    /**
     * Updates the purl for the object based on the SBOM content, if provided.
     *
     */
    private void setupRootPurl() {
        Bom bom = getCycloneDxBom();

        rootPurl = null;

        if (bom != null && bom.getMetadata() != null && bom.getMetadata().getComponent() != null) {
            rootPurl = bom.getMetadata().getComponent().getPurl();
        }
    }

    @PrePersist
    public void prePersist() {
        creationTime = Instant.now();
        setupRootPurl();
    }

    @PreUpdate
    public void preUpdate() {
        setupRootPurl();
    }

}
