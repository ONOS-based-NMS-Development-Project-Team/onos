/*
 * Copyright 2018-present Open Networking Foundation
 *
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
package univ.bupt.soon.mlshow.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.felix.scr.annotations.*;
import org.onosproject.soon.MLAppRegistry;
import org.onosproject.soon.ModelControlService;
import org.onosproject.ui.UiExtension;
import org.onosproject.ui.UiExtensionService;
import org.onosproject.ui.UiMessageHandlerFactory;
import org.onosproject.ui.UiView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * SOON的UI展示。
 */
@Component(immediate = true)
@Service
public class SoonUiComponent implements MLAppRegistry {

    private static final String VIEW_ID = "soon";
    private static final String VIEW_TEXT = "Self Oprimizing Optical Network";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, ModelControlService> modelServices = Maps.newConcurrentMap();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected UiExtensionService uiExtensionService;


    // List of application views
    private final List<UiView> uiViews = ImmutableList.of(
            new UiView(UiView.Category.OTHER, VIEW_ID, VIEW_TEXT)
    );

    // Factory for UI message handlers
    private final UiMessageHandlerFactory messageHandlerFactory =
            () -> ImmutableList.of(
                    // 添加handler
            );

    // Application UI extension
    protected UiExtension extension =
            new UiExtension.Builder(getClass().getClassLoader(), uiViews)
                    .resourcePath(VIEW_ID)
                    .messageHandlerFactory(messageHandlerFactory)
                    .build();

    @Activate
    protected void activate() {
        uiExtensionService.register(extension);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        uiExtensionService.unregister(extension);
        log.info("Stopped");
    }

    @Override
    public boolean register(ModelControlService modelControlService, String s) {
        if (modelServices.containsKey(s)) {
            return false;
        } else {
            modelServices.put(s, modelControlService);
            return true;
        }
    }

    @Override
    public boolean unregister(String s) {
        modelServices.remove(s);
        return true;
    }
}
