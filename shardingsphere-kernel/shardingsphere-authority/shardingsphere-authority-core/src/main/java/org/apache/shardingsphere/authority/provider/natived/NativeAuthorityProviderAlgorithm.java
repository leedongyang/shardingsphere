/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.authority.provider.natived;

import lombok.Getter;
import org.apache.shardingsphere.authority.model.ShardingSpherePrivileges;
import org.apache.shardingsphere.authority.provider.natived.builder.StoragePrivilegeBuilder;
import org.apache.shardingsphere.authority.spi.AuthorityProviderAlgorithm;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.metadata.user.ShardingSphereUser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Native authority provide algorithm.
 * 
 * @deprecated This algorithm will be removed in a future version.
*/
@Deprecated
public final class NativeAuthorityProviderAlgorithm implements AuthorityProviderAlgorithm {
    
    @Getter
    private Properties props;
    
    private final Map<ShardingSphereUser, ShardingSpherePrivileges> userPrivilegeMap = new ConcurrentHashMap<>();
    
    @Override
    public void init(final Properties props) {
        this.props = props;
    }
    
    @Override
    public void init(final Map<String, ShardingSphereMetaData> metaDataMap, final Collection<ShardingSphereUser> users) {
        userPrivilegeMap.putAll(StoragePrivilegeBuilder.build(new LinkedList<>(metaDataMap.values()), users));
    }
    
    @Override
    public void refresh(final Map<String, ShardingSphereMetaData> metaDataMap, final Collection<ShardingSphereUser> users) {
        userPrivilegeMap.putAll(StoragePrivilegeBuilder.build(new LinkedList<>(metaDataMap.values()), users));
    }
    
    @Override
    public Optional<ShardingSpherePrivileges> findPrivileges(final Grantee grantee) {
        return userPrivilegeMap.keySet().stream().filter(each -> each.getGrantee().equals(grantee)).findFirst().map(userPrivilegeMap::get);
    }
    
    @Override
    public String getType() {
        return "NATIVE";
    }
}
