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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.sbomer.n.cli.runtime.KojiClientProducer;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

public class KojiBuildFinderProcessor {
    private static final String FEATURE = "sbomer-koji-build-finder";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void addDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        indexDependency.produce(new IndexDependencyBuildItem("org.eclipse.packager", "packager-rpm"));
        indexDependency.produce(new IndexDependencyBuildItem("com.google.guava", "guava"));
        indexDependency.produce(new IndexDependencyBuildItem("org.jboss.pnc", "dto"));
        indexDependency.produce(new IndexDependencyBuildItem("org.jboss.pnc", "pnc-api"));
        indexDependency.produce(new IndexDependencyBuildItem("org.jboss.pnc", "rest-api"));
        indexDependency.produce(new IndexDependencyBuildItem("org.jboss.pnc", "constants"));
        indexDependency.produce(new IndexDependencyBuildItem("org.apache.commons", "commons-vfs2"));
        indexDependency.produce(new IndexDependencyBuildItem("org.apache.httpcomponents.client5", "httpclient5"));
        indexDependency.produce(new IndexDependencyBuildItem("org.commonjava.util", "jhttpc"));
        // indexDependency.produce(
        // new IndexDependencyBuildItem(
        // "oorg.eclipse.microprofile.context-propagation",
        // "microprofile-context-propagation-api"));

    }

    @BuildStep
    public void registerReflections(
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            CombinedIndexBuildItem combinedIndexBuildItem) {

        List<String> knownClasses = combinedIndexBuildItem.getIndex()
                .getKnownClasses()
                .stream()
                .map(classInfo -> classInfo.name().toString())
                .toList();

        List<String> reflectionClassNames = List.of(
                "org.jboss.pnc.build.finder.core.BuildConfig",
                "org.jboss.pnc.build.finder.koji.ClientSession",
                "org.jboss.pnc.client.RemoteCollectionConfig",
                "org.jboss.pnc.client.Configuration");

        List<String> reflectionCassPrefixes = List.of(
                "org.jboss.pnc.enums",
                "org.jboss.pnc.api.enums",
                "org.jboss.pnc.dto",
                "org.eclipse.packager.rpm",
                "com.google.common.io");

        List<String> toAdd = new ArrayList<>();

        toAdd.addAll(knownClasses.stream().filter(className -> {
            if (reflectionClassNames.contains(className)) {
                return true;
            }

            for (String prefix : reflectionCassPrefixes) {
                if (className.startsWith(prefix)) {
                    return true;
                }
            }

            return false;
        }).collect(Collectors.toList()));

        // .filter(classInfo -> {
        // String className = classInfo.name().toString();

        // // || className.startsWith("org.jboss.pnc")

        // if (className.startsWith("org.jboss.pnc.dto")) {
        // return true;
        // }

        // // if (className.startsWith("org.eclipse.packager.rpm")) {
        // // return true;
        // // }

        // // if (className.startsWith("com.google.common.io")) {
        // // return true;
        // // }

        // // if (className.startsWith("org.apache.hc.client5.http.impl.classic.HttpClientBuilder")) {
        // // return true;
        // // }

        // // if (className.startsWith("org.commonjava.util.jhttpc")) {
        // // return true;
        // // }

        // // if (className.startsWith("org.apache.hc.client5.http.entity.BrotliInputStreamFactory")) {
        // // return true;
        // // }

        // return false;
        // }).map(Object::toString).toArray(String[]::new);

        System.out.println(toAdd);

        reflectiveClass.produce(
                ReflectiveClassBuildItem.builder(toAdd.toArray(String[]::new)).fields(true).methods(true).build());
    }

    @BuildStep
    List<RuntimeInitializedClassBuildItem> staticRuntimes() {
        final List<RuntimeInitializedClassBuildItem> buildItems = new ArrayList<>();

        String[] classNames = new String[] { "org.apache.commons.vfs2.impl.DefaultFileReplicator",
                "org.apache.hc.client5.http.impl.auth.NTLMEngineImpl",
                "org.jboss.pnc.dto.DeliverableAnalyzerLabelEntry$Builder" };

        for (String className : classNames) {
            buildItems.add(new RuntimeInitializedClassBuildItem(className));
        }

        return buildItems;
    }

    @BuildStep
    public void registerBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        additionalBeanBuildItemProducer.produce(AdditionalBeanBuildItem.unremovableOf(KojiClientProducer.class));
    }

    @BuildStep
    public void ignoreWarnings(BuildProducer<ReflectiveHierarchyIgnoreWarningBuildItem> ignoreWarningProducer) {
        // ignoreWarningProducer.produce(new ReflectiveHierarchyIgnoreWarningBuildItem(new IgnorePackagePredicate()));

    }
}
