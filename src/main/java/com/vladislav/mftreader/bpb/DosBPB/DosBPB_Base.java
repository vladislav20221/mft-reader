package com.vladislav.mftreader.bpb.DosBPB;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Vladislav
 */
public class DosBPB_Base {
    // Идентификатор OEM — «NTFS» указывает на файловую систему.
    private final byte[] oemID = new byte[8];
    // Размер сектора в байтах
    private final byte[] sizeSectorByte = new byte[2];
    // Количество секторов в одном кластере
    private final byte[] numberSectorToKlaster = new byte[1];
    // Тип носителя данных
    private final byte[] typeDisk = new byte[1];
    // Количество секторов на дорожке
    private final byte[] numberSectorTrack = new byte[2];
    // Количество магнитных головок
    private final byte[] numberMagnaticHeader = new byte[2];
    // количеством секторов перед этим разделом. Тоесть...
    // сколько секторов нам нужно добавить, чтобы получить логический сектор 0. 0x20
    private final byte[] numberSumSector = new byte[4];
    
    
    public DosBPB_Base ( final DataInputStream data ) throws IOException {
        data.skipBytes( 3 ); // Команда JMP
        data.read( this.oemID );
        data.read( this.sizeSectorByte );
        data.read( this.numberSectorToKlaster );
        data.skipBytes( 7 );
        data.read( this.typeDisk );
        data.skipBytes( 2 );
        data.read( this.numberSectorTrack );
        data.read( this.numberMagnaticHeader );
        data.read( this.numberSumSector );
        data.skipBytes( 4 );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.sizeSectorByte );
        ArrayUtils.reverse( this.numberSectorTrack );
        ArrayUtils.reverse( this.numberMagnaticHeader );
        ArrayUtils.reverse( this.numberSumSector );
    }

    @Override
    public String toString () {
        return """
               Идентификатор OEM: %s
               Размер сектора: %d (байт)
               Количество секторов в одном кластере: %d
               Тип носителя данных: %s
               Количество секторов на дорожке: %d
               Количество магнитных головок: %d
               Нужно добавить %d секторов для получения логического адреса.
               """.formatted( new String ( this.getOemID().toByteArray() ), 
                         this.getSizeSectorByte(),
                         this.getNumberSectorToKlaster(), this.getTypeDisk(),
                         this.getNumberSectorTrack(), this.getNumberMagnaticHeader(),
                         this.getNumberSumSector() );
    }
    
    public BigInteger getOemID() {
        return new BigInteger( 1, oemID );
    }
    public BigInteger getSizeSectorByte() {
        return new BigInteger( 1, sizeSectorByte );
    }
    public BigInteger getNumberSectorToKlaster() {
        return new BigInteger( 1, numberSectorToKlaster );
    }
    public BigInteger getTypeDisk() {
        return new BigInteger( 1, typeDisk );
    }
    public BigInteger getNumberSectorTrack() {
        return new BigInteger( 1, numberSectorTrack );
    }
    public BigInteger getNumberMagnaticHeader() {
        return new BigInteger( 1, numberMagnaticHeader );
    }
    public BigInteger getNumberSumSector() {
        return new BigInteger( 1, numberSumSector );
    }           
}