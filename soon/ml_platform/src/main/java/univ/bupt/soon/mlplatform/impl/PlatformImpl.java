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
package univ.bupt.soon.mlplatform.impl;

import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.soon.MLPlatformService;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.mlmodel.MLModelDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class PlatformImpl implements MLPlatformService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }

    @Override
    public void sendMLConfig(MLModelDetail mlModelDetail) {

    }

    @Override
    public int sendTrainData(SegmentForDataset segmentForDataset) {
        return 0;
    }

    @Override
    public int sendTestData(SegmentForDataset segmentForDataset) {
        return 0;
    }

    @Override
    public void startTrain() {

    }

    @Override
    public void stopTrain() {

    }

    @Override
    public List<Double> applyModel(List<List<Double>> list) {
        return null;
    }

    @Override
    public void deleteModel() {

    }

    @Override
    public void deleteTrainDataset(int i) {

    }

    @Override
    public void deleteTestDataset(int i) {

    }

    @Override
    public URL getResultURL() {
        return null;
    }
}
