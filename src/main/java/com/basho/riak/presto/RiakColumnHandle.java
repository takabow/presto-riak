/*
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
package com.basho.riak.presto;

import com.facebook.presto.spi.ColumnMetadata;
import com.facebook.presto.spi.ConnectorColumnHandle;
import com.facebook.presto.spi.type.Type;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.airlift.log.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public final class RiakColumnHandle
        implements ConnectorColumnHandle
{
    public static final String PKEY_COLUMN_NAME = "__pkey";
    private final String connectorId;
    private final String columnName;
    private final Type type;
    private boolean index;
    private final int ordinalPosition;

    private static final Logger log = Logger.get(RiakRecordSetProvider.class);

    public RiakColumnHandle(String connectorId, ColumnMetadata columnMetadata)
    {
        this(connectorId, columnMetadata.getName(),
                columnMetadata.getType(),
                // NOTE: this default 'false' can be implicit performance lose
                //       if there be a bug that indexedColumns lost somewhere
                false,
                columnMetadata.getOrdinalPosition());
    }

    @JsonCreator
    public RiakColumnHandle(
            @JsonProperty("connectorId") String connectorId,
            @JsonProperty("columnName") String columnName,
            @JsonProperty("type") Type type,
            @JsonProperty("index") boolean index,
            @JsonProperty("ordinalPosition") int ordinalPosition)
    {
        this.connectorId = checkNotNull(connectorId, "connectorId is null");
        this.columnName = checkNotNull(columnName, "columnName is null");
        this.type = checkNotNull(type, "type is null");
        this.index = checkNotNull(index);
        this.ordinalPosition = ordinalPosition;
    }

    @JsonProperty
    public String getConnectorId()
    {
        return connectorId;
    }

    @JsonProperty
    public String getColumnName()
    {
        return columnName;
    }

    @JsonProperty
    public Type getType()
    {
        return type;
    }

    @JsonProperty
    public boolean getIndex()
    {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }


    @JsonProperty
    public int getOrdinalPosition()
    {
        return ordinalPosition;
    }

    public ColumnMetadata getColumnMetadata()
    {
        //boolean isPartitionKey = columnName.equals(PKEY_COLUMN_NAME);
        return new ColumnMetadata(columnName, type, ordinalPosition, false);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(connectorId, columnName);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        com.basho.riak.presto.RiakColumnHandle other = (com.basho.riak.presto.RiakColumnHandle) obj;
        return Objects.equal(this.connectorId, other.connectorId) &&
                (this.index == other.index) &&
                Objects.equal(this.columnName, other.columnName);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("columnName", columnName)
                .add("type", type)
                .add("index", index)
                .add("ordinalPosition", ordinalPosition)
                .toString();
    }
}
