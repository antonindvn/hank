package com.rapleaf.hank.partition_server;

import com.rapleaf.hank.BaseTestCase;
import com.rapleaf.hank.coordinator.MockHostDomainPartition;
import com.rapleaf.hank.partitioner.MapPartitioner;
import com.rapleaf.hank.storage.mock.MockReader;

import java.nio.ByteBuffer;

public class TestDomainAccessor extends BaseTestCase {
  public void testSetUpAndServe() throws Exception {
    PartitionAccessor partitionAccessors[] = new PartitionAccessor[1];

    ByteBuffer key = ByteBuffer.wrap("key".getBytes());
    ByteBuffer nullKey = ByteBuffer.wrap("nullKey".getBytes());

    // setup DomainAccessor
    partitionAccessors[0] = new PartitionAccessor(new MockHostDomainPartition(0, 1, 2),
        new MockReader(null, 1, "v".getBytes(), null));
    // MapPartitioner maps both 'key' and 'nullkey' to partitionAccessors[0]
    DomainAccessor drs = new DomainAccessor("domainReaderSet", partitionAccessors,
        new MapPartitioner(key, 0, nullKey, 0), 2000);

    drs.get(key);
    drs.get(nullKey);

    assertEquals(partitionAccessors[0].getRequests(), 2l);
    assertEquals(partitionAccessors[0].getHits(), 1l);

    assertEquals(partitionAccessors[0].getHostDomainPartition().getCount("Requests in last minute").intValue(), 0);
    assertEquals(partitionAccessors[0].getHostDomainPartition().getCount("Hits in last minute").intValue(), 0);

    Thread.sleep(3000);

    assertEquals(partitionAccessors[0].getRequests(), 0l);
    assertEquals(partitionAccessors[0].getHits(), 0l);

    assertEquals(partitionAccessors[0].getHostDomainPartition().getCount("Requests in last minute").intValue(), 2);
    assertEquals(partitionAccessors[0].getHostDomainPartition().getCount("Hits in last minute").intValue(), 1);
  }
}