// Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior University
// Copyright (c) 2011, 2012 Open Networking Foundation
// Copyright (c) 2012, 2013 Big Switch Networks, Inc.
// This library was generated by the LoxiGen Compiler.
// See the file LICENSE.txt which should have been included in the source distribution

// Automatically generated by LOXI from template of_class.java
// Do not modify

package org.projectfloodlight.openflow.protocol.ver13;

import org.projectfloodlight.openflow.protocol.*;
import org.projectfloodlight.openflow.protocol.action.*;
import org.projectfloodlight.openflow.protocol.actionid.*;
import org.projectfloodlight.openflow.protocol.bsntlv.*;
import org.projectfloodlight.openflow.protocol.errormsg.*;
import org.projectfloodlight.openflow.protocol.meterband.*;
import org.projectfloodlight.openflow.protocol.instruction.*;
import org.projectfloodlight.openflow.protocol.instructionid.*;
import org.projectfloodlight.openflow.protocol.match.*;
import org.projectfloodlight.openflow.protocol.oxm.*;
import org.projectfloodlight.openflow.protocol.queueprop.*;
import org.projectfloodlight.openflow.types.*;
import org.projectfloodlight.openflow.util.*;
import org.projectfloodlight.openflow.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import org.jboss.netty.buffer.ChannelBuffer;
import com.google.common.hash.PrimitiveSink;
import com.google.common.hash.Funnel;

class OFQueueStatsEntryVer13 implements OFQueueStatsEntry {
    private static final Logger logger = LoggerFactory.getLogger(OFQueueStatsEntryVer13.class);
    // version: 1.3
    final static byte WIRE_VERSION = 4;
    final static int LENGTH = 40;

        private final static OFPort DEFAULT_PORT_NO = OFPort.ANY;
        private final static long DEFAULT_QUEUE_ID = 0x0L;
        private final static U64 DEFAULT_TX_BYTES = U64.ZERO;
        private final static U64 DEFAULT_TX_PACKETS = U64.ZERO;
        private final static U64 DEFAULT_TX_ERRORS = U64.ZERO;
        private final static long DEFAULT_DURATION_SEC = 0x0L;
        private final static long DEFAULT_DURATION_NSEC = 0x0L;

    // OF message fields
    private final OFPort portNo;
    private final long queueId;
    private final U64 txBytes;
    private final U64 txPackets;
    private final U64 txErrors;
    private final long durationSec;
    private final long durationNsec;
//
    // Immutable default instance
    final static OFQueueStatsEntryVer13 DEFAULT = new OFQueueStatsEntryVer13(
        DEFAULT_PORT_NO, DEFAULT_QUEUE_ID, DEFAULT_TX_BYTES, DEFAULT_TX_PACKETS, DEFAULT_TX_ERRORS, DEFAULT_DURATION_SEC, DEFAULT_DURATION_NSEC
    );

    // package private constructor - used by readers, builders, and factory
    OFQueueStatsEntryVer13(OFPort portNo, long queueId, U64 txBytes, U64 txPackets, U64 txErrors, long durationSec, long durationNsec) {
        this.portNo = portNo;
        this.queueId = queueId;
        this.txBytes = txBytes;
        this.txPackets = txPackets;
        this.txErrors = txErrors;
        this.durationSec = durationSec;
        this.durationNsec = durationNsec;
    }

    // Accessors for OF message fields
    @Override
    public OFPort getPortNo() {
        return portNo;
    }

    @Override
    public long getQueueId() {
        return queueId;
    }

    @Override
    public U64 getTxBytes() {
        return txBytes;
    }

    @Override
    public U64 getTxPackets() {
        return txPackets;
    }

    @Override
    public U64 getTxErrors() {
        return txErrors;
    }

    @Override
    public long getDurationSec() {
        return durationSec;
    }

    @Override
    public long getDurationNsec() {
        return durationNsec;
    }

    @Override
    public OFVersion getVersion() {
        return OFVersion.OF_13;
    }



    public OFQueueStatsEntry.Builder createBuilder() {
        return new BuilderWithParent(this);
    }

    static class BuilderWithParent implements OFQueueStatsEntry.Builder {
        final OFQueueStatsEntryVer13 parentMessage;

        // OF message fields
        private boolean portNoSet;
        private OFPort portNo;
        private boolean queueIdSet;
        private long queueId;
        private boolean txBytesSet;
        private U64 txBytes;
        private boolean txPacketsSet;
        private U64 txPackets;
        private boolean txErrorsSet;
        private U64 txErrors;
        private boolean durationSecSet;
        private long durationSec;
        private boolean durationNsecSet;
        private long durationNsec;

        BuilderWithParent(OFQueueStatsEntryVer13 parentMessage) {
            this.parentMessage = parentMessage;
        }

    @Override
    public OFPort getPortNo() {
        return portNo;
    }

    @Override
    public OFQueueStatsEntry.Builder setPortNo(OFPort portNo) {
        this.portNo = portNo;
        this.portNoSet = true;
        return this;
    }
    @Override
    public long getQueueId() {
        return queueId;
    }

    @Override
    public OFQueueStatsEntry.Builder setQueueId(long queueId) {
        this.queueId = queueId;
        this.queueIdSet = true;
        return this;
    }
    @Override
    public U64 getTxBytes() {
        return txBytes;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxBytes(U64 txBytes) {
        this.txBytes = txBytes;
        this.txBytesSet = true;
        return this;
    }
    @Override
    public U64 getTxPackets() {
        return txPackets;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxPackets(U64 txPackets) {
        this.txPackets = txPackets;
        this.txPacketsSet = true;
        return this;
    }
    @Override
    public U64 getTxErrors() {
        return txErrors;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxErrors(U64 txErrors) {
        this.txErrors = txErrors;
        this.txErrorsSet = true;
        return this;
    }
    @Override
    public long getDurationSec() {
        return durationSec;
    }

    @Override
    public OFQueueStatsEntry.Builder setDurationSec(long durationSec) {
        this.durationSec = durationSec;
        this.durationSecSet = true;
        return this;
    }
    @Override
    public long getDurationNsec() {
        return durationNsec;
    }

    @Override
    public OFQueueStatsEntry.Builder setDurationNsec(long durationNsec) {
        this.durationNsec = durationNsec;
        this.durationNsecSet = true;
        return this;
    }
    @Override
    public OFVersion getVersion() {
        return OFVersion.OF_13;
    }



        @Override
        public OFQueueStatsEntry build() {
                OFPort portNo = this.portNoSet ? this.portNo : parentMessage.portNo;
                if(portNo == null)
                    throw new NullPointerException("Property portNo must not be null");
                long queueId = this.queueIdSet ? this.queueId : parentMessage.queueId;
                U64 txBytes = this.txBytesSet ? this.txBytes : parentMessage.txBytes;
                if(txBytes == null)
                    throw new NullPointerException("Property txBytes must not be null");
                U64 txPackets = this.txPacketsSet ? this.txPackets : parentMessage.txPackets;
                if(txPackets == null)
                    throw new NullPointerException("Property txPackets must not be null");
                U64 txErrors = this.txErrorsSet ? this.txErrors : parentMessage.txErrors;
                if(txErrors == null)
                    throw new NullPointerException("Property txErrors must not be null");
                long durationSec = this.durationSecSet ? this.durationSec : parentMessage.durationSec;
                long durationNsec = this.durationNsecSet ? this.durationNsec : parentMessage.durationNsec;

                //
                return new OFQueueStatsEntryVer13(
                    portNo,
                    queueId,
                    txBytes,
                    txPackets,
                    txErrors,
                    durationSec,
                    durationNsec
                );
        }

    }

    static class Builder implements OFQueueStatsEntry.Builder {
        // OF message fields
        private boolean portNoSet;
        private OFPort portNo;
        private boolean queueIdSet;
        private long queueId;
        private boolean txBytesSet;
        private U64 txBytes;
        private boolean txPacketsSet;
        private U64 txPackets;
        private boolean txErrorsSet;
        private U64 txErrors;
        private boolean durationSecSet;
        private long durationSec;
        private boolean durationNsecSet;
        private long durationNsec;

    @Override
    public OFPort getPortNo() {
        return portNo;
    }

    @Override
    public OFQueueStatsEntry.Builder setPortNo(OFPort portNo) {
        this.portNo = portNo;
        this.portNoSet = true;
        return this;
    }
    @Override
    public long getQueueId() {
        return queueId;
    }

    @Override
    public OFQueueStatsEntry.Builder setQueueId(long queueId) {
        this.queueId = queueId;
        this.queueIdSet = true;
        return this;
    }
    @Override
    public U64 getTxBytes() {
        return txBytes;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxBytes(U64 txBytes) {
        this.txBytes = txBytes;
        this.txBytesSet = true;
        return this;
    }
    @Override
    public U64 getTxPackets() {
        return txPackets;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxPackets(U64 txPackets) {
        this.txPackets = txPackets;
        this.txPacketsSet = true;
        return this;
    }
    @Override
    public U64 getTxErrors() {
        return txErrors;
    }

    @Override
    public OFQueueStatsEntry.Builder setTxErrors(U64 txErrors) {
        this.txErrors = txErrors;
        this.txErrorsSet = true;
        return this;
    }
    @Override
    public long getDurationSec() {
        return durationSec;
    }

    @Override
    public OFQueueStatsEntry.Builder setDurationSec(long durationSec) {
        this.durationSec = durationSec;
        this.durationSecSet = true;
        return this;
    }
    @Override
    public long getDurationNsec() {
        return durationNsec;
    }

    @Override
    public OFQueueStatsEntry.Builder setDurationNsec(long durationNsec) {
        this.durationNsec = durationNsec;
        this.durationNsecSet = true;
        return this;
    }
    @Override
    public OFVersion getVersion() {
        return OFVersion.OF_13;
    }

//
        @Override
        public OFQueueStatsEntry build() {
            OFPort portNo = this.portNoSet ? this.portNo : DEFAULT_PORT_NO;
            if(portNo == null)
                throw new NullPointerException("Property portNo must not be null");
            long queueId = this.queueIdSet ? this.queueId : DEFAULT_QUEUE_ID;
            U64 txBytes = this.txBytesSet ? this.txBytes : DEFAULT_TX_BYTES;
            if(txBytes == null)
                throw new NullPointerException("Property txBytes must not be null");
            U64 txPackets = this.txPacketsSet ? this.txPackets : DEFAULT_TX_PACKETS;
            if(txPackets == null)
                throw new NullPointerException("Property txPackets must not be null");
            U64 txErrors = this.txErrorsSet ? this.txErrors : DEFAULT_TX_ERRORS;
            if(txErrors == null)
                throw new NullPointerException("Property txErrors must not be null");
            long durationSec = this.durationSecSet ? this.durationSec : DEFAULT_DURATION_SEC;
            long durationNsec = this.durationNsecSet ? this.durationNsec : DEFAULT_DURATION_NSEC;


            return new OFQueueStatsEntryVer13(
                    portNo,
                    queueId,
                    txBytes,
                    txPackets,
                    txErrors,
                    durationSec,
                    durationNsec
                );
        }

    }


    final static Reader READER = new Reader();
    static class Reader implements OFMessageReader<OFQueueStatsEntry> {
        @Override
        public OFQueueStatsEntry readFrom(ChannelBuffer bb) throws OFParseError {
            OFPort portNo = OFPort.read4Bytes(bb);
            long queueId = U32.f(bb.readInt());
            U64 txBytes = U64.ofRaw(bb.readLong());
            U64 txPackets = U64.ofRaw(bb.readLong());
            U64 txErrors = U64.ofRaw(bb.readLong());
            long durationSec = U32.f(bb.readInt());
            long durationNsec = U32.f(bb.readInt());

            OFQueueStatsEntryVer13 queueStatsEntryVer13 = new OFQueueStatsEntryVer13(
                    portNo,
                      queueId,
                      txBytes,
                      txPackets,
                      txErrors,
                      durationSec,
                      durationNsec
                    );
            if(logger.isTraceEnabled())
                logger.trace("readFrom - read={}", queueStatsEntryVer13);
            return queueStatsEntryVer13;
        }
    }

    public void putTo(PrimitiveSink sink) {
        FUNNEL.funnel(this, sink);
    }

    final static OFQueueStatsEntryVer13Funnel FUNNEL = new OFQueueStatsEntryVer13Funnel();
    static class OFQueueStatsEntryVer13Funnel implements Funnel<OFQueueStatsEntryVer13> {
        private static final long serialVersionUID = 1L;
        @Override
        public void funnel(OFQueueStatsEntryVer13 message, PrimitiveSink sink) {
            message.portNo.putTo(sink);
            sink.putLong(message.queueId);
            message.txBytes.putTo(sink);
            message.txPackets.putTo(sink);
            message.txErrors.putTo(sink);
            sink.putLong(message.durationSec);
            sink.putLong(message.durationNsec);
        }
    }


    public void writeTo(ChannelBuffer bb) {
        WRITER.write(bb, this);
    }

    final static Writer WRITER = new Writer();
    static class Writer implements OFMessageWriter<OFQueueStatsEntryVer13> {
        @Override
        public void write(ChannelBuffer bb, OFQueueStatsEntryVer13 message) {
            message.portNo.write4Bytes(bb);
            bb.writeInt(U32.t(message.queueId));
            bb.writeLong(message.txBytes.getValue());
            bb.writeLong(message.txPackets.getValue());
            bb.writeLong(message.txErrors.getValue());
            bb.writeInt(U32.t(message.durationSec));
            bb.writeInt(U32.t(message.durationNsec));


        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("OFQueueStatsEntryVer13(");
        b.append("portNo=").append(portNo);
        b.append(", ");
        b.append("queueId=").append(queueId);
        b.append(", ");
        b.append("txBytes=").append(txBytes);
        b.append(", ");
        b.append("txPackets=").append(txPackets);
        b.append(", ");
        b.append("txErrors=").append(txErrors);
        b.append(", ");
        b.append("durationSec=").append(durationSec);
        b.append(", ");
        b.append("durationNsec=").append(durationNsec);
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OFQueueStatsEntryVer13 other = (OFQueueStatsEntryVer13) obj;

        if (portNo == null) {
            if (other.portNo != null)
                return false;
        } else if (!portNo.equals(other.portNo))
            return false;
        if( queueId != other.queueId)
            return false;
        if (txBytes == null) {
            if (other.txBytes != null)
                return false;
        } else if (!txBytes.equals(other.txBytes))
            return false;
        if (txPackets == null) {
            if (other.txPackets != null)
                return false;
        } else if (!txPackets.equals(other.txPackets))
            return false;
        if (txErrors == null) {
            if (other.txErrors != null)
                return false;
        } else if (!txErrors.equals(other.txErrors))
            return false;
        if( durationSec != other.durationSec)
            return false;
        if( durationNsec != other.durationNsec)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((portNo == null) ? 0 : portNo.hashCode());
        result = prime *  (int) (queueId ^ (queueId >>> 32));
        result = prime * result + ((txBytes == null) ? 0 : txBytes.hashCode());
        result = prime * result + ((txPackets == null) ? 0 : txPackets.hashCode());
        result = prime * result + ((txErrors == null) ? 0 : txErrors.hashCode());
        result = prime *  (int) (durationSec ^ (durationSec >>> 32));
        result = prime *  (int) (durationNsec ^ (durationNsec >>> 32));
        return result;
    }

}
