package org.dcache.xrootd2.protocol.messages;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteBuffer;

import org.dcache.xrootd2.protocol.XrootdProtocol;
import org.jboss.netty.buffer.ChannelBuffer;

public class WriteRequest extends AbstractRequestMessage
{
    private final int fhandle;
    private final long offset;
    private final int dlen;
    private final ChannelBuffer buffer;

    public WriteRequest(ChannelBuffer buffer)
    {
        super(buffer);

        if (getRequestID() != XrootdProtocol.kXR_write)
            throw new IllegalArgumentException("doesn't seem to be a kXR_write message");

        fhandle = buffer.getInt(4);
        offset = buffer.getLong(8);
        dlen = buffer.getInt(20);

        this.buffer = buffer;
    }

    public int getFileHandle()
    {
        return fhandle;
    }

    public long getWriteOffset()
    {
        return offset;
    }

    public int getDataLength()
    {
        return dlen;
    }

    public void getData(GatheringByteChannel out)
        throws IOException
    {
        int index = 24;
        int len = dlen;
        while (len > 0) {
            int written = buffer.getBytes(index, out, len);
            index += written;
            len -= written;
        }
    }

    /**
     * Converts this requests's payload into an array of NIO
     * buffers. The returned buffers might or might not share the
     * content with this request.
     *
     * @see ChannelBuffers.toByteBuffers
     */
    public ByteBuffer[] toByteBuffers()
    {
        return buffer.toByteBuffers(24, dlen);
    }

    @Override
    public String toString()
    {
        return String.format("write[handle=%d,offset=%d,length=%d]", fhandle, offset, dlen);
    }
}
