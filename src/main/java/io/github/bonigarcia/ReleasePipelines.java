/*
 * (C) Copyright 2017 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia;

import java.util.List;

import org.kurento.client.KurentoClient;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pipelines "releaser".
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 1.0.0
 */
public class ReleasePipelines {

    private static final Logger log = LoggerFactory
            .getLogger(ReleasePipelines.class);

    public static void main(String[] args) {
        String kmsUrl = "ws://kurento.lab.fiware.org:8888/kurento";
        KurentoClient kurentoClient = KurentoClient.create(kmsUrl);
        ServerManager serverManager = kurentoClient.getServerManager();

        List<MediaPipeline> pipelines = serverManager.getPipelines();
        log.info("*** {} pipelines", pipelines.size());

        for (int i = 0; i < pipelines.size(); i++) {
            MediaPipeline pipeline = pipelines.get(i);
            log.info(">>> Pipeline #{} : {}", i, pipeline);

            List<MediaObject> children = pipeline.getChildren();
            for (MediaObject mediaObject : children) {
                log.info("\tMediaObject {}", mediaObject);
            }

            log.info("Releasing pipeline {}", pipeline);
            pipeline.release();
        }

        kurentoClient.destroy();
    }

}
