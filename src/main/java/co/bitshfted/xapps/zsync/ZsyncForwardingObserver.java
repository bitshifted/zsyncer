/**
 * Copyright (c) 2015, Salesforce.com, Inc. All rights reserved.
 * Copyright (c) 2020, Bitshift (bitshifted.co), Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 * 
 * Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.bitshfted.xapps.zsync;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.bitshfted.xapps.zsync.http.ContentRange;


/**
 * A {@link ZsyncObserver} that forwards observed events to a configurable list of additional zsync
 * observers.
 *
 * @author bstclair
 */
public class ZsyncForwardingObserver extends ZsyncObserver {

  private List<ZsyncObserver> observers;

  public ZsyncForwardingObserver(ZsyncObserver... targets) {
    this(targets == null ? Collections.emptyList() : List.of(targets));
  }

  public ZsyncForwardingObserver(Iterable<? extends ZsyncObserver> observers) {
    if(observers == null) {
      throw new NullPointerException("Observers list can not be null");
    }
    this.observers = new ArrayList<>();
    observers.forEach(o -> this.observers.add(o));
  }

  @Override
  public void zsyncStarted(URI requestedZsyncUri, Zsync.Options options) {
    for (ZsyncObserver observer : this.observers) {
      observer.zsyncStarted(requestedZsyncUri, options);
    }
  }

  @Override
  public void controlFileDownloadingInitiated(URI uri) {
    for (ZsyncObserver observer : this.observers) {
      observer.controlFileDownloadingInitiated(uri);
    }
  }

  @Override
  public void controlFileDownloadingStarted(URI uri, long length) {
    for (ZsyncObserver observer : this.observers) {
      observer.controlFileDownloadingStarted(uri, length);
    }
  }

  @Override
  public void controlFileDownloadingComplete() {
    for (ZsyncObserver observer : this.observers) {
      observer.controlFileDownloadingComplete();
    }
  }

  @Override
  public void controlFileReadingStarted(Path path, long length) {
    for (ZsyncObserver observer : this.observers) {
      observer.controlFileReadingStarted(path, length);
    }
  }

  @Override
  public void controlFileReadingComplete() {
    for (ZsyncObserver observer : this.observers) {
      observer.controlFileReadingComplete();
    }
  }

  @Override
  public void inputFileReadingStarted(Path inputFile, long length) {
    for (ZsyncObserver observer : this.observers) {
      observer.inputFileReadingStarted(inputFile, length);
    }
  }

  @Override
  public void inputFileReadingComplete() {
    for (ZsyncObserver observer : this.observers) {
      observer.inputFileReadingComplete();
    }
  }

  @Override
  public void remoteFileDownloadingInitiated(URI uri, List<ContentRange> ranges) {
    for (ZsyncObserver observer : this.observers) {
      observer.remoteFileDownloadingInitiated(uri, ranges);
    }
  }

  @Override
  public void remoteFileDownloadingStarted(URI uri, long length) {
    for (ZsyncObserver observer : this.observers) {
      observer.remoteFileDownloadingStarted(uri, length);
    }
  }

  @Override
  public void remoteFileRangeReceived(ContentRange range) {
    for (ZsyncObserver observer : this.observers) {
      observer.remoteFileRangeReceived(range);
    }
  }

  @Override
  public void remoteFileDownloadingComplete() {
    for (ZsyncObserver observer : this.observers) {
      observer.remoteFileDownloadingComplete();
    }
  }

  @Override
  public void outputFileWritingStarted(Path outputFile, long length) {
    for (ZsyncObserver observer : this.observers) {
      observer.outputFileWritingStarted(outputFile, length);
    }
  }

  @Override
  public void outputFileWritingCompleted() {
    for (ZsyncObserver observer : this.observers) {
      observer.outputFileWritingCompleted();
    }
  }

  @Override
  public void bytesRead(long bytes) {
    for (ZsyncObserver observer : this.observers) {
      observer.bytesRead(bytes);
    }
  }

  @Override
  public void bytesWritten(long bytes) {
    for (ZsyncObserver observer : this.observers) {
      observer.bytesWritten(bytes);
    }
  }

  @Override
  public void bytesDownloaded(long bytes) {
    for (ZsyncObserver observer : this.observers) {
      observer.bytesDownloaded(bytes);
    }
  }

  @Override
  public void zsyncFailed(Exception exception) {
    for (ZsyncObserver observer : this.observers) {
      observer.zsyncFailed(exception);
    }
  }

  @Override
  public void zsyncComplete() {
    for (ZsyncObserver observer : this.observers) {
      observer.zsyncComplete();
    }
  }
}
