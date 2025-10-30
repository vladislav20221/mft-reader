package com.vladislav.mftreader.mft.attribute.access;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Vladislav
 */
public class AccessRights_MS_DOS implements AccessRights {
    // Флаги доступа MS-DOS.
    private final byte[] flags = new byte[4];
    
    public AccessRights_MS_DOS ( final DataInputStream data ) throws IOException {
        data.read( flags );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.flags );
    }
    
    @Override
    public String toString () {
        return this.getPermissions();
    }
    
    @Override
    public String getPermissions() {
        final int flaag = new BigInteger( 1, flags ).intValue();
        final StringBuilder build = new StringBuilder();
        switch ( flaag&0xF ) {
            case 1 -> { build.append( "Только для чтения" ); }
            case 2 -> { build.append( "Скрытый файл" ); }
            case 4 -> { build.append( "Системынй файл" ); }
        }
        switch ( flaag&0xF0 ) {
            case 20 -> { build.append( " Архивный" ); }
            case 40 -> { build.append( " Устройство" ); }
            case 80 -> { build.append( " Обычный" ); }
        }
        switch ( flaag&0xF00 ) {
            case 100 -> { build.append( " Временный" ); }
            case 200 -> { build.append( " Разряженный" ); }
            case 400 -> { build.append( " Replace point" ); }
            case 800 -> { build.append( " Сжатый" ); }
        }
        switch ( flaag&0xF000 ) {
            case 1000 -> { build.append( " Оффлайновый" ); }
            case 2000 -> { build.append( " Не индексируемый" ); }
            case 4000 -> { build.append( " Зашифрованный" ); }
        }
        return build.toString();
    }
}