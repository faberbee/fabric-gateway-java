/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.gateway.impl;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.TestUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GatewayTest {
    private static final TestUtils testUtils = TestUtils.getInstance();

    private Gateway.Builder builder = null;

    @BeforeEach
    public void beforeEach() throws Exception {
        builder = testUtils.newGatewayBuilder();
    }

    @Test
    public void testGetNetworkFromConfig() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            assertThat(network.getChannel().getName()).isEqualTo("mychannel");
        }
    }

    @Test
    public void testGetAssumedNetwork() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("assumed");
            assertThat(network.getChannel().getName()).isEqualTo("assumed");
        }
    }

    @Test
    public void testGetCachedNetwork() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("assumed");
            Network network2 = gateway.getNetwork("assumed");
            assertThat(network).isSameAs(network2);
        }
    }

    @Test
    public void testGetNetworkEmptyString() {
        try (Gateway gateway = builder.connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetNetworkNullString() {
        try (Gateway gateway = builder.connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testCloseGatewayClosesNetworks() {
        Gateway gateway = builder.connect();
        Channel channel = gateway.getNetwork("assumed").getChannel();

        gateway.close();

        assertThat(channel.isShutdown()).isTrue();
    }

    // -- Filtered

    @Test
    public void testGetFullBlockGetNetworkFromConfig_1() {
        try (Gateway gateway = builder.deliverFilter(false).connect()) {
            Network network = gateway.getNetwork("mychannel");
            assertThat(network.getChannel().getName()).isEqualTo("mychannel");

            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });
        }
    }

    @Test
    public void testGetFullBlockGetNetworkFromConfig_2() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            assertThat(network.getChannel().getName()).isEqualTo("mychannel");

            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });
        }
    }

    @Test
    public void testGetFilterBlockGetAssumedNetwork() {
        try (Gateway gateway = builder.deliverFilter(true).connect()) {
            Network network = gateway.getNetwork("assumed");
            assertThat(network.getChannel().getName()).isEqualTo("assumed");

            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertTrue(peerOptions.isRegisterEventsForFilteredBlocks());
            });
        }
    }

    @Test
    public void testGetFullBlockGetAssumedNetwork_1() {
        try (Gateway gateway = builder.deliverFilter(false).connect()) {
            Network network = gateway.getNetwork("assumed");
            assertThat(network.getChannel().getName()).isEqualTo("assumed");

            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });
        }
    }

    @Test
    public void testGetFullBlockGetAssumedNetwork_2() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("assumed");
            assertThat(network.getChannel().getName()).isEqualTo("assumed");

            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });
        }
    }

    @Test
    public void testGetFilterBlockGetCachedNetwork() {
        try (Gateway gateway = builder.deliverFilter(true).connect()) {
            Network network = gateway.getNetwork("assumed");
            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertTrue(peerOptions.isRegisterEventsForFilteredBlocks());
            });

            Network network2 = gateway.getNetwork("assumed");
            Channel channel2 = network2.getChannel();

            Collection<Peer> peers2 = channel2.getPeers();
            peers2.forEach(peer2 -> {
                Channel.PeerOptions peerOptions2 = channel2.getPeersOptions(peer2);
                Assertions.assertTrue(peerOptions2.isRegisterEventsForFilteredBlocks());
            });

            assertThat(network).isSameAs(network2);
        }
    }

    @Test
    public void testGetFullBlockGetCachedNetwork_1() {
        try (Gateway gateway = builder.deliverFilter(false).connect()) {
            Network network = gateway.getNetwork("assumed");
            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });

            Network network2 = gateway.getNetwork("assumed");
            Channel channel2 = network2.getChannel();

            Collection<Peer> peers2 = channel2.getPeers();
            peers2.forEach(peer2 -> {
                Channel.PeerOptions peerOptions2 = channel2.getPeersOptions(peer2);
                Assertions.assertFalse(peerOptions2.isRegisterEventsForFilteredBlocks());
            });

            assertThat(network).isSameAs(network2);
        }
    }

    @Test
    public void testGetFullBlockGetCachedNetwork_2() {
        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("assumed");
            Channel channel = network.getChannel();

            Collection<Peer> peers = channel.getPeers();
            peers.forEach(peer -> {
                Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
                Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
            });

            Network network2 = gateway.getNetwork("assumed");
            Channel channel2 = network2.getChannel();

            Collection<Peer> peers2 = channel2.getPeers();
            peers2.forEach(peer2 -> {
                Channel.PeerOptions peerOptions2 = channel2.getPeersOptions(peer2);
                Assertions.assertFalse(peerOptions2.isRegisterEventsForFilteredBlocks());
            });

            assertThat(network).isSameAs(network2);
        }
    }

    @Test
    public void testGetFilterBlockGetNetworkEmptyString() {
        try (Gateway gateway = builder.deliverFilter(true).connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetFullBlockGetNetworkEmptyString_1() {
        try (Gateway gateway = builder.deliverFilter(false).connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetFullBlockGetNetworkEmptyString_2() {
        try (Gateway gateway = builder.connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }


    @Test
    public void testGetFilterBlockGetNetworkNullString() {
        try (Gateway gateway = builder.deliverFilter(true).connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetFullBlockGetNetworkNullString_1() {
        try (Gateway gateway = builder.connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetFullBlockGetNetworkNullString_2() {
        try (Gateway gateway = builder.deliverFilter(false).connect()) {
            assertThatThrownBy(() -> gateway.getNetwork(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Channel name must be a non-empty string");
        }
    }

    @Test
    public void testGetFilterBlockCloseGatewayClosesNetworks() {
        Gateway gateway = builder.deliverFilter(true).connect();
        Channel channel = gateway.getNetwork("assumed").getChannel();

        Collection<Peer> peers = channel.getPeers();
        peers.forEach(peer -> {
            Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
            Assertions.assertTrue(peerOptions.isRegisterEventsForFilteredBlocks());
        });

        gateway.close();

        assertThat(channel.isShutdown()).isTrue();
    }

    @Test
    public void testGetFullBlockCloseGatewayClosesNetworks_1() {
        Gateway gateway = builder.deliverFilter(false).connect();
        Channel channel = gateway.getNetwork("assumed").getChannel();

        Collection<Peer> peers = channel.getPeers();
        peers.forEach(peer -> {
            Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
            Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
        });

        gateway.close();

        assertThat(channel.isShutdown()).isTrue();
    }

    @Test
    public void testGetFullBlockCloseGatewayClosesNetworks_2() {
        Gateway gateway = builder.connect();
        Channel channel = gateway.getNetwork("assumed").getChannel();

        Collection<Peer> peers = channel.getPeers();
        peers.forEach(peer -> {
            Channel.PeerOptions peerOptions = channel.getPeersOptions(peer);
            Assertions.assertFalse(peerOptions.isRegisterEventsForFilteredBlocks());
        });

        gateway.close();

        assertThat(channel.isShutdown()).isTrue();
    }
}
