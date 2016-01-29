package network.thunder.core.mesh;

import io.netty.channel.ChannelHandlerContext;
import network.thunder.core.communication.processor.ChannelIntent;
import network.thunder.core.etc.crypto.ECDHKeySet;
import org.bitcoinj.core.ECKey;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Node {
    public boolean justFetchNewIpAddresses = false;

    public ECKey pubKeyClient;
    public ECKey pubKeyServer;

    public ECKey ephemeralKeyServer;
    public ECKey ephemeralKeyClient;

    public boolean isServer;

    public ECDHKeySet ecdhKeySet;

    public ChannelIntent intent = ChannelIntent.MISC;

    public String host;
    public int port;

    public String hostServer;
    public int portServer;

    private boolean connected = false;
    private ChannelHandlerContext nettyContext;
    private byte[] pubkey;

    private boolean isReady;
    private boolean hasOpenChannel;
    private ArrayList<byte[]> inventoryList = new ArrayList<>();
    private OnConnectionCloseListener onConnectionCloseListener;

    public String name;

    public Node (String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    public Node (ResultSet set) throws SQLException {
        this.host = set.getString("host");
        this.port = set.getInt("port");
        init();
    }

    public Node (byte[] pubkey) {
        this.pubkey = pubkey;
        init();
    }

    public Node (Node node) {
        init();
        this.port = node.port;
        this.portServer = node.portServer;
        this.host = node.host;
        this.hostServer = node.hostServer;
        this.pubKeyServer = node.pubKeyServer;
        this.pubKeyClient = node.pubKeyClient;
        this.isServer = node.isServer;
        this.intent = node.intent;
        this.name = node.name;
    }

    public Node () {
        init();
    }

    public void init () {
        ephemeralKeyServer = new ECKey();
        pubKeyServer = new ECKey();
    }

    public void closeConnection () {
        if (onConnectionCloseListener != null) {
            onConnectionCloseListener.onClose();
        }
        try {
            this.nettyContext.close();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        return Arrays.equals(pubkey, node.pubkey);

    }

    public String getHost () {
        return host;
    }

    public void setHost (String host) {
        this.host = host;
    }

    public ChannelHandlerContext getNettyContext () {
        return nettyContext;
    }

    public void setNettyContext (ChannelHandlerContext nettyContext) {
        this.nettyContext = nettyContext;
    }

    public int getPort () {
        return port;
    }

    public void setPort (int port) {
        this.port = port;
    }

    @Override
    public int hashCode () {
        return pubkey != null ? Arrays.hashCode(pubkey) : 0;
    }

    public boolean isConnected () {
        return connected;
    }

    public void setConnected (boolean connected) {
        this.connected = connected;
    }

    public void setOnConnectionCloseListener (OnConnectionCloseListener onConnectionCloseListener) {
        this.onConnectionCloseListener = onConnectionCloseListener;
    }

    public interface OnConnectionCloseListener {
        public void onClose ();
    }
}
