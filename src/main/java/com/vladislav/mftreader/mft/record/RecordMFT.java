package com.vladislav.mftreader.mft.record;

import com.vladislav.mftreader.exeption.InvalidSignatureException;
import com.vladislav.mftreader.mft.attribute.AttributeMFT;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Абстрактное представлине записи MFT.
 * Любая запись MFT начинается с заголовка MFT.
 * После загловка располагаются атрибуты файлов и дерикторий.
 * @author Vladislav
 */
public class RecordMFT {
    private static final Logger LOG = Logger.getLogger( RecordMFT.class.getName() );
    // Размер записи для MFT по умолчанию в большинстве случаев.
    public static final int SIZE_DEFAULT_RECORD_MFT = 1024;
    
    private final RecordHeaderMFT headMFT;
    private final List<AttributeMFT> listAttribute = new ArrayList( 6 );
    
    
    public RecordHeaderMFT getHeaderMFT () {
        return this.headMFT;
    }
    public List<AttributeMFT> getAttributeList () {
        return this.listAttribute;
    }
    
    public RecordMFT ( final byte[] buffer ) throws IOException, InvalidSignatureException {
        final ByteArrayInputStream byteStream = new ByteArrayInputStream( buffer );
        final DataInputStream data = new DataInputStream( byteStream );
        
        this.headMFT = new RecordHeaderMFT( data );
        //System.out.println( "Рассматривается запись: "+this.headMFT.getIndexRecordMFT() );
        // Расчёт размера прочитанного заголовка
        // Прочитано 48+массив 8 байт
        final long sizeAll = this.headMFT.getRealSize().subtract( this.headMFT.getRecordSize() ).longValue()-8;
        //System.out.printf( "Заголовок занимает место: %d.\n", this.headMFT.getRecordSize() );
        //System.out.printf( "Атрибуты занимают %d байт.\n", sizeAll );
        long N = 0;
        while ( N < sizeAll ) {
            if ( sizeAll >= buffer.length ) break;
            final long finish = sizeAll-N;
            try {
                AttributeMFT attribute = new AttributeMFT ( data, finish, this.headMFT );
                this.listAttribute.add( attribute );
                //System.out.println( attribute );
                N += attribute.getHead().getSize().longValue();
                // Осталось прочитать байт.
                //System.out.printf( "Прочитано байт %d из %d логика %b -> осталось %d.\n", N, sizeAll, ( N < sizeAll ), (finish + attribute.getHead().getSize().longValue() ) );
            } catch ( EOFException es ) {
                LOG.warning( "Привышен лимит буфура." );
                break;
            }
        }
        final byte[] controlEnd = new byte[8];
        data.read( controlEnd );
        //System.out.printf( "Атрибуты были завершены последовательностью: %s длиной 8 байт.\n", new BigInteger( 1, controlEnd ).toString( 16 ) );
        final long offset = this.headMFT.getSizeAllocatedMFT().subtract( this.headMFT.getRecordSize() ).longValue()-N-8;
        //long skipSize = data.skip( offset );
        //System.out.printf( "По факту было пропущено: %d \n.", skipSize );
    }
    
    @Override
    public String toString () {
        if ( this.listAttribute.isEmpty() ) return "Нет распознаных атрибутов.";
        StringBuilder build = new StringBuilder();
        this.listAttribute.forEach( build::append );
        return build.toString();
    }
}