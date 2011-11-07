/**
 *  Copyright 2011 Rapleaf
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.rapleaf.hank.coordinator;

import java.io.IOException;

public abstract class AbstractRing implements Ring {
  private final int ringNumber;
  private final RingGroup ringGroup;

  protected AbstractRing(int ringNumber, RingGroup ringGroup) {
    this.ringNumber = ringNumber;
    this.ringGroup = ringGroup;
  }

  @Override
  public final RingGroup getRingGroup() {
    return ringGroup;
  }

  @Override
  public final int getRingNumber() {
    return ringNumber;
  }

  @Override
  public DomainGroupVersion getCurrentVersion() throws IOException {
    Integer currentVersionNumber = getCurrentVersionNumber();
    if (currentVersionNumber != null) {
      return getRingGroup().getDomainGroup().getVersionByNumber(currentVersionNumber);
    } else {
      return null;
    }
  }

  @Override
  public DomainGroupVersion getUpdatingToVersion() throws IOException {
    Integer updatingToVersionNumber = getUpdatingToVersionNumber();
    if (updatingToVersionNumber != null) {
      return getRingGroup().getDomainGroup().getVersionByNumber(updatingToVersionNumber);
    } else {
      return null;
    }
  }

  @Override
  public void markUpdateComplete() throws IOException {
    setCurrentVersion(getUpdatingToVersionNumber());
    setUpdatingToVersion(null);
  }

  @Override
  public int compareTo(Ring other) {
    return Integer.valueOf(ringNumber).compareTo(other.getRingNumber());
  }

  @Override
  public String toString() {
    return String.format("AbstractRing [ringGroup=%s, ring=%d, version=%d, updatingToVersion=%d]",
        (getRingGroup() != null ? getRingGroup().getName() : "null"), this.getRingNumber(), this.getCurrentVersionNumber(), this.getUpdatingToVersionNumber());
  }
}
