package com.vladislav.mftreader.mft.attribute.head.packed;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Флаги упаковки атрибута.
 * @author Vladislav
 */
public class PackingFlagsAttribute implements PackingFlags {
    // Флаги.
    private final byte[] flag = new byte[2];
    
    public PackingFlagsAttribute ( final DataInputStream data ) throws IOException {
        data.read( flag );
    }

    @Override
    public String toString () {
        return this.getPackingType();
    }
    
    @Override
    public String getPackingType() {
        int value = new BigInteger( 1, this.flag ).intValue();
        switch ( value&0xF ) {
            case 1: return "[Сжатый атрибут]";
        }
        switch ( value&0xF000 ) {
            case 4000: return "[Зашифрованный атрибут]";
            case 8000: return "[Разряженный атрибут]";
        }
        return "[Не известно]";
    }
}