/*
 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
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
package com.ethercis.vehr.parser;

import com.ethercis.servicemanager.cluster.RunTimeSingleton;
import com.ethercis.servicemanager.common.I_SessionClientProperties;
import com.ethercis.servicemanager.exceptions.ServiceManagerException;
import com.ethercis.servicemanager.service.ServiceInfo;

/**
 * ETHERCIS Project VirtualEhr
 * Created by Christian Chevalley on 7/7/2015.
 */
public class DefaultURIParser extends URIParser {
    /**
     * create a new parser for path, check format
     *
     * @param global
     * @throws com.ethercis.servicemanager.exceptions.ServiceManagerException
     */
    public DefaultURIParser(RunTimeSingleton global)  {
        super(global);
    }

    @Override
    protected void doInit(RunTimeSingleton global, ServiceInfo serviceInfo) throws ServiceManagerException {

    }

    @Override
    public I_SessionClientProperties identifyParametersAsProperties() {
        return null;
    }
}
