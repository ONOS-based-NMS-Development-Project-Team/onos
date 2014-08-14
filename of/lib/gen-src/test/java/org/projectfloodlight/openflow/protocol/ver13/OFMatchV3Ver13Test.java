// Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior University
// Copyright (c) 2011, 2012 Open Networking Foundation
// Copyright (c) 2012, 2013 Big Switch Networks, Inc.
// This library was generated by the LoxiGen Compiler.
// See the file LICENSE.txt which should have been included in the source distribution

// Automatically generated by LOXI from template unit_test.java
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
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.hamcrest.CoreMatchers;



public class OFMatchV3Ver13Test {
    OFFactory factory;

    final static byte[] MATCH_V3_SERIALIZED =
        new byte[] { 0x0, 0x1, 0x0, 0x3c, (byte) 0x80, 0x0, 0x5, 0x10, (byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98, 0x12, 0x14, 0x12, 0x10, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x12, 0x34, 0x56, 0x78, (byte) 0x80, 0x0, 0x8, 0x6, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, (byte) 0x80, 0x0, 0x20, 0x2, 0x0, 0x35, (byte) 0x80, 0x0, 0x36, 0x10, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x0, 0x0, 0x0, 0x0 };

    @Before
    public void setup() {
        factory = OFFactoryVer13.INSTANCE;
    }

    @Test
    public void testWrite() {
        OFMatchV3.Builder builder = factory.buildMatchV3();
        builder
       .setMasked(MatchField.METADATA, OFMetadata.ofRaw(0xFEDCBA9812141210l), OFMetadata.ofRaw(0xFFFFFFFF12345678l))
       .setExact(MatchField.ETH_SRC, MacAddress.of(new byte[] {1,2,3,4,5,6}))
       .setExact(MatchField.UDP_DST, TransportPort.of(53))
       .setExact(MatchField.IPV6_DST, IPv6Address.of(new byte[] { 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12,
                                                                  0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12 }));
        OFMatchV3 matchV3 = builder.build();
        ChannelBuffer bb = ChannelBuffers.dynamicBuffer();
        matchV3.writeTo(bb);
        byte[] written = new byte[bb.readableBytes()];
        bb.readBytes(written);

        assertThat(written, CoreMatchers.equalTo(MATCH_V3_SERIALIZED));
    }

    @Test
    public void testRead() throws Exception {
        OFMatchV3.Builder builder = factory.buildMatchV3();
        builder
       .setMasked(MatchField.METADATA, OFMetadata.ofRaw(0xFEDCBA9812141210l), OFMetadata.ofRaw(0xFFFFFFFF12345678l))
       .setExact(MatchField.ETH_SRC, MacAddress.of(new byte[] {1,2,3,4,5,6}))
       .setExact(MatchField.UDP_DST, TransportPort.of(53))
       .setExact(MatchField.IPV6_DST, IPv6Address.of(new byte[] { 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12,
                                                                  0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12 }));
        OFMatchV3 matchV3Built = builder.build();

        ChannelBuffer input = ChannelBuffers.copiedBuffer(MATCH_V3_SERIALIZED);

        // FIXME should invoke the overall reader once implemented
        OFMatchV3 matchV3Read = OFMatchV3Ver13.READER.readFrom(input);
        assertEquals(MATCH_V3_SERIALIZED.length, input.readerIndex());

        assertEquals(matchV3Built, matchV3Read);
   }

   @Test
   public void testReadWrite() throws Exception {
       ChannelBuffer input = ChannelBuffers.copiedBuffer(MATCH_V3_SERIALIZED);

       // FIXME should invoke the overall reader once implemented
       OFMatchV3 matchV3 = OFMatchV3Ver13.READER.readFrom(input);
       assertEquals(MATCH_V3_SERIALIZED.length, input.readerIndex());

       // write message again
       ChannelBuffer bb = ChannelBuffers.dynamicBuffer();
       matchV3.writeTo(bb);
       byte[] written = new byte[bb.readableBytes()];
       bb.readBytes(written);

       assertThat(written, CoreMatchers.equalTo(MATCH_V3_SERIALIZED));
   }

}
