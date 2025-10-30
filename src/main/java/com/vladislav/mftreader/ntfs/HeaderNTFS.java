package com.vladislav.mftreader.ntfs;

import com.vladislav.mftreader.bpb.DosBPB.DosBPB_Base;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрацкия представляющая заголовка NTFS (73 байта)
 * @author Vladislav
 */
public class HeaderNTFS {
    public static final int SIZE_HEADER_NTFS = 50;
    
    private final DosBPB_Base baseBPB;
    // Номер физического диска
    private final byte[] numberLogicalDisk = new byte[1];
    // Флаги (идентичные DOS 3.4 EBPB)
    private final byte[] identityFlags = new byte[1];
    // Расширенная загрузочная запись. аналогии с DOS 3.4 EBPB и NTFS EBPB
    private final byte[] extendedBootRecord = new byte[1];
    
    // Секторов в томе
    private final byte[] numberSectorsTom = new byte[8];
    // Первый кластер MFT
    private final byte[] oneKlasterMFT = new byte[8];
    // Копия кластера MFT
    private final byte[] copyKlasterMFT = new byte[8];
    // Размер записи MFT
    private final byte[] sizeRecordMFT = new byte[4];
    // Размер индексного блока
    private final byte[] sizeIndexBlock = new byte[4];
    // Серийный номер тома
    private final byte[] serialNumberTom = new byte[8];
    // Контрольная сумма
    private final byte[] sumKontrol = new byte[4];
    
    
    public HeaderNTFS ( final DataInputStream data ) throws IOException {
        // Пропускаю запись DOS 3.31 BPB
        // Чтение данной записи до конца не раелизовано.
        baseBPB = new DosBPB_Base( data );
        
        data.read( this.numberLogicalDisk );        
        data.read( this.identityFlags );
        data.read( this.extendedBootRecord );
        data.skipBytes( 1 );    // Зарезервировано
        
        data.read( this.numberSectorsTom );
        data.read( this.oneKlasterMFT );
        data.read( this.copyKlasterMFT );
        data.read( this.sizeRecordMFT );
        data.read( this.sizeIndexBlock );
        data.read( this.serialNumberTom );
        data.read( this.sumKontrol );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.numberSectorsTom );
        ArrayUtils.reverse( this.oneKlasterMFT );
        ArrayUtils.reverse( this.copyKlasterMFT );
        ArrayUtils.reverse( this.sizeRecordMFT );
        ArrayUtils.reverse( this.sizeIndexBlock );
    }

    
    @Override
    public String toString () {
        return """
               /// Базовая запись BPB ///
               %s
               /// Расширенная запись BPB ///
               Номер физического диска: %d
               Секторов в томе: %d
               Первый кластер MFT: %d
               Копия кластера MFT: %d
               Размер записи MFT: %d
               Размер индексного блока: %d
               Серийный номер тома: %s
               Контрольная сумма: %s
               """.formatted( this.baseBPB,
                              this.getNumberLogicalDisk(),
                              this.getNumberSectorsTom(),
                              this.getOneKlasterMFT(),
                              this.getCopyKlasterMFT(),
                              this.getSizeRecordMFT(),
                              this.getSizeIndexBlock(),
                              this.getSerialNumberTom().toString( 16 ),
                              this.getSumKontrol().toString( 16 ) );
    }
    
    
    public DosBPB_Base getDosBase () { return this.baseBPB; }
    
    public BigInteger getNumberLogicalDisk() {
        return new BigInteger( 1, numberLogicalDisk );
    }
    public BigInteger getIdentityFlags() {
        return new BigInteger( 1, identityFlags );
    }
    public BigInteger getExtendedBootRecord() {
        return new BigInteger( 1, extendedBootRecord );
    }
    public BigInteger getNumberSectorsTom() {
        return new BigInteger( 1, numberSectorsTom );
    }
    public BigInteger getOneKlasterMFT() {
        return new BigInteger( 1, oneKlasterMFT );
    }
    public BigInteger getCopyKlasterMFT() {
        return new BigInteger( 1, copyKlasterMFT );
    }
    public BigInteger getSizeRecordMFT() {
        //BigInteger a = new BigInteger( -1, sizeRecordMFT );
        //if ( a.intValue() < 0 ) {
        //    Integer b = (int) Math.pow( 2, a.intValue()*-1 );
        //    return new BigInteger( b.toString() );
        //} else {
        //    return a;
        //}
        return new BigInteger( "1024" );
    }
    public BigInteger getSizeIndexBlock() {
        return new BigInteger( 1, sizeIndexBlock );
    }
    public BigInteger getSerialNumberTom() {
        return new BigInteger( 1, serialNumberTom );
    }
    public BigInteger getSumKontrol() {
        return new BigInteger( 1, sumKontrol );
    }
}