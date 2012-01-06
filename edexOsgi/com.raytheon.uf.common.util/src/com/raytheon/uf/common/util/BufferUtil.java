/**
 * This software was developed and / or modified by Raytheon Company,
 * pursuant to Contract DG133W-05-CQ-1067 with the US Government.
 * 
 * U.S. EXPORT CONTROLLED TECHNICAL DATA
 * This software product contains export-restricted data whose
 * export/transfer/disclosure is restricted by U.S. law. Dissemination
 * to non-U.S. persons whether in the United States or abroad requires
 * an export license or other authorization.
 * 
 * Contractor Name:        Raytheon Company
 * Contractor Address:     6825 Pine Street, Suite 340
 *                         Mail Stop B8
 *                         Omaha, NE 68106
 *                         402.291.0100
 * 
 * See the AWIPS II Master Rights File ("Master Rights File.pdf") for
 * further licensing information.
 **/
package com.raytheon.uf.common.util;

import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.Library;

/**
 * Utility for creating and managing nio Buffers
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Mar 1, 2010            mschenke     Initial creation
 * 
 * </pre>
 * 
 * @author mschenke
 * @version 1.0
 */

public class BufferUtil {

    private static boolean mallocing = false;

    static {
        if (mallocing) {
            try {
                Library.initialize("tcnative-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int wordAlignedByteWidth(Rectangle datasetBounds) {
        int paddedWidth = datasetBounds.width;

        int width = paddedWidth % 4;
        if (width != 0) {
            paddedWidth += 4 - width;
        }
        return paddedWidth;
    }

    public static int wordAlignedShortWidth(Rectangle datasetBounds) {
        int paddedWidth = datasetBounds.width;

        if ((paddedWidth & 1) == 1) {
            paddedWidth++;
        }
        return paddedWidth;
    }

    public static ByteBuffer wrapDirect(byte[] byteData,
            Rectangle datasetBounds, int bytesPerPixel) {
        int paddedWidth = wordAlignedByteWidth(new Rectangle(datasetBounds.x,
                datasetBounds.y, datasetBounds.width * bytesPerPixel,
                datasetBounds.height * bytesPerPixel));
        Rectangle actualBounds = new Rectangle(datasetBounds.x,
                datasetBounds.y, bytesPerPixel * datasetBounds.width,
                datasetBounds.height);

        ByteBuffer bb = createByteBuffer(actualBounds.height * paddedWidth,
                true);
        int diff = paddedWidth - actualBounds.width;

        int szX = actualBounds.x + actualBounds.width;
        int szY = actualBounds.y + actualBounds.height;
        int k = 0;
        for (int j = actualBounds.y; j < szY; j++) {
            for (int i = actualBounds.x; i < szX; i++) {
                bb.put(byteData[k++]);
            }

            for (int i = 0; i < diff; i++) {
                bb.put((byte) 0);
            }
        }

        return bb;
    }

    public static ByteBuffer wrapDirect(byte[] byteData, Rectangle datasetBounds) {
        return wrapDirect(byteData, datasetBounds, 1);
    }

    public static ShortBuffer wrapDirect(short[] shortBuffer,
            Rectangle datasetBounds) {

        int paddedWidth = wordAlignedShortWidth(datasetBounds);

        ByteBuffer bb = ByteBuffer.allocateDirect(paddedWidth
                * datasetBounds.height * 2);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();

        int szX = datasetBounds.x + datasetBounds.width;
        int szY = datasetBounds.y + datasetBounds.height;
        int k = 0;
        for (int j = datasetBounds.y; j < szY; j++) {
            for (int i = datasetBounds.x; i < szX; i++) {
                sb.put(shortBuffer[k++]);
            }

            if (paddedWidth != datasetBounds.width) {
                sb.put((short) 0);
            }
        }

        return sb;
    }

    public static ShortBuffer directBuffer(ShortBuffer sb) {
        if (sb.isDirect()) {
            return sb;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(sb.capacity() * 2);
        bb.rewind();
        ShortBuffer directSb = bb.asShortBuffer();
        directSb.rewind();
        sb.rewind();
        directSb.put(sb);

        directSb.rewind();
        return directSb;
    }

    public static IntBuffer directBuffer(IntBuffer ib) {
        if (ib.isDirect()) {
            return ib;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(ib.capacity() * 4);
        bb.order(ByteOrder.nativeOrder());
        bb.rewind();
        IntBuffer directIb = bb.asIntBuffer();
        directIb.rewind();
        ib.rewind();
        directIb.put(ib);
        directIb.rewind();
        return directIb;
    }

    public static ByteBuffer directBuffer(ByteBuffer ib) {
        if (ib.isDirect()) {
            return ib;
        }

        ByteBuffer bbDirect = createByteBuffer(ib.capacity(), true);
        bbDirect.rewind();
        ib.rewind();
        bbDirect.put(ib);

        bbDirect.rewind();
        return bbDirect;
    }

    public static synchronized ByteBuffer createByteBuffer(int capacity,
            boolean direct) {
        if (direct) {
            if (mallocing) {
                return Buffer.malloc(capacity);
            } else {
                return ByteBuffer.allocateDirect(capacity);
            }
        } else {
            return ByteBuffer.allocate(capacity);
        }
    }

    public static synchronized void freeByteBuffer(ByteBuffer buf) {
        if (buf.isDirect() && mallocing) {
            Buffer.free(buf);
        }
    }

}
