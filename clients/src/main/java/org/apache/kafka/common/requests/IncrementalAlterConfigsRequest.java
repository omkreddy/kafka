/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.common.requests;

import org.apache.kafka.common.message.IncrementalAlterConfigsRequestData;
import org.apache.kafka.common.message.IncrementalAlterConfigsRequestData.AlterConfigsResource;
import org.apache.kafka.common.message.IncrementalAlterConfigsResponseData;
import org.apache.kafka.common.message.IncrementalAlterConfigsResponseData.AlterConfigsResourceResult;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.types.Struct;

public class IncrementalAlterConfigsRequest extends AbstractRequest {

    private final IncrementalAlterConfigsRequestData data;
    private final short version;

    public IncrementalAlterConfigsRequest(final Struct struct, final short version) {
        super(ApiKeys.ELECT_PREFERRED_LEADERS, version);
        this.data = new IncrementalAlterConfigsRequestData(struct, version);
        this.version = version;
    }

    @Override
    protected Struct toStruct() {
        return data.toStruct(version);
    }

    @Override
    public AbstractResponse getErrorResponse(final int throttleTimeMs, final Throwable e) {
        IncrementalAlterConfigsResponseData response = new IncrementalAlterConfigsResponseData();
        ApiError apiError = ApiError.fromThrowable(e);
        for (AlterConfigsResource resource : data.resources()) {
            response.responses().add(new AlterConfigsResourceResult()
                    .setResourceName(resource.resourceName())
                    .setResourceType(resource.resourceType())
                    .setErrorCode(apiError.error().code())
                    .setErrorMessage(apiError.message()));
        }
        return new IncrementalAlterConfigsResponse(response);
    }
}
