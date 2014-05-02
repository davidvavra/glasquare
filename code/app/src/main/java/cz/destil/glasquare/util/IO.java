package cz.destil.glasquare.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Utils for general input/output handling
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class IO {
    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                DebugLog.e(e);
            }
        }
    }
}
