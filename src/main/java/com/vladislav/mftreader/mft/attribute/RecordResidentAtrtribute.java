package com.vladislav.mftreader.mft.attribute;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Vladislav
 */
public class RecordResidentAtrtribute {
    // Длина тела атрибута, без заголовка
    private final byte[] lengthBody = new byte[4];
    // Смещение тела атрибута
    private final byte[] offsetBody = new byte[2];
    // Индексный флаг
    private final byte[] flagIndex = new byte[1];
    // Имя атрибута
    private final byte[] attribyteName;
    
    
    public RecordResidentAtrtribute ( final DataInputStream data, int lengthName ) throws IOException {
        data.read( this.lengthBody );
        data.read( this.offsetBody );
        data.read( this.flagIndex );
        data.skipBytes( 1 );        // Выравнивание.
        
        if ( lengthName == 0 ) {
            this.attribyteName = null;
        } else {
            this.attribyteName = new byte[ lengthName*2 ];
            System.out.println( "\nДлина имени атрибута: "+lengthName*2+"\n" );
            data.read( this.attribyteName );
        }
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.lengthBody );
        ArrayUtils.reverse( this.offsetBody );
    }
    
    
    @Override
    public String toString () {
        return """
               /// ========= Резидентный заголовок ========= ///
               Длина тела атрибута, без заголовка: %s
               Смещение тела атрибута: %s
               Индексный флаг: %s
               Имя атрибута: %s
               """.formatted( this.getLengthBody(), this.getOffsetBody(),
                              this.getFlagIndex(), this.getAttribyteName() );
    }
    
    
    
    public BigInteger getLengthBody() {
        return new BigInteger( 1, lengthBody );
    }
    public BigInteger getOffsetBody() {
        return new BigInteger( 1, offsetBody );
    }
    public BigInteger getFlagIndex() {
        return new BigInteger( 1, flagIndex );
    }
    public BigInteger getAttribyteName() {
        if ( attribyteName == null ) return null;
        return new BigInteger( 1, attribyteName );
    }
}