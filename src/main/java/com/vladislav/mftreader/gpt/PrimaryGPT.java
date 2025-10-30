package com.vladislav.mftreader.gpt;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрактное представление первичного заголовка GPT-заголовка.
 * @author Vladislav
 */
public class PrimaryGPT {        
    // Размер первичной записи GPT в байтвх всегда фиксировано.
    public static final int SIZE_PRIMARY_GPT = 512;
    
    // Сигнатура заголовка. Должно быть "EFI PART".
    private final byte[] signature = new byte[8];
    // Версия формата заголовка. На данный момент 1.0.
    private final byte[] versionHeader = new byte[4];
    // Размер заголовка GPT в байтах. Должно быть 92 байта.
    private final byte[] sizeGPT = new byte[4];
    // Контрольная сумма GPT-заголовка по алгоритму CRC32. 
    private final byte[] sumKontrolGPT = new byte[4];
    // Адрес сектора, содержащего первичный GPT-заголовок. Адрес LBA-1
    private final byte[] addressSectorPrimaryGPT = new byte[8];
    // Адрес сектора, содержащего копию GPT-заголовка. Всегда имеет значение адреса последнего сектора на диске.
    private final byte[] addressSectorCopyPrimaryGPT = new byte[8];
    // Адрес первого раздела диска
    private final byte[] addressSectorStart = new byte[8];
    // Адрес последнего сектора диска, отведённого под разделы.
    private final byte[] addressSectorEnd = new byte[8];
    // Уникальный идентификатор
    private final byte[] GUID = new byte[16];
    // Адрес начала таблицы разделов
    private final byte[] addresPartitionTableStart = new byte[8];
    // Максимальное число разделов, которое может содержать таблица
    private final byte[] maxPartition = new byte[4];
    // Размер записи для раздела
    private final byte[] sizePartitionRecord = new byte[4];
    // Контрольная сумма таблицы разделов. Алгоритм контрольной суммы — CRC32
    private final byte[] sumKontrolPartitionTable = new byte[4];
    
    
    public PrimaryGPT ( final DataInputStream data ) throws IOException {        
        data.read( this.signature );
        data.read( this.versionHeader );                
        data.read( this.sizeGPT );        
        data.read( this.sumKontrolGPT );
        data.skip( 4 ); // зарезервировано. Заполнено нулями.
        data.read( this.addressSectorPrimaryGPT );
        data.read( this.addressSectorCopyPrimaryGPT );
        data.read( this.addressSectorStart );
        data.read( this.addressSectorEnd );
        data.read( this.GUID );
        data.read( this.addresPartitionTableStart );
        data.read( this.maxPartition );
        data.read( this.sizePartitionRecord );        
        data.read( this.sumKontrolPartitionTable );
        data.skip( 420 );   // зарезервировано. Заполнено нулями.
        // Всего запись составляет 512 байт
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.sizeGPT );
        ArrayUtils.reverse( this.addresPartitionTableStart );
        ArrayUtils.reverse( this.addressSectorCopyPrimaryGPT );
        ArrayUtils.reverse( this.addressSectorEnd );
        ArrayUtils.reverse( this.addressSectorPrimaryGPT );
        ArrayUtils.reverse( this.addressSectorStart );
        ArrayUtils.reverse( this.maxPartition );
        ArrayUtils.reverse( this.sizePartitionRecord );
    }
    
    @Override
    public String toString () {        
        return """
               /---------------- GPT-заголовок ----------------/
               Сигнатура заголовка: [%s] Версия заголовка: %s
               Размер заголовка GPT: %d (байт) 
               Контрольная сумма GPT-заголовка (CRC32): %s
               Адрес первичного GPT-заголовка (LBA-1): %d
               Адрес копии GPT-заголовка: %d
               Адрес первого сектора разделов: %d
               Адрес последнего сектора разделов: %d
               GUID диска: %s
               Адрес Таблици разделов: %d
               Максимальное число разделов в таблице: %d
               Размер записи раздела: %d
               Контрольная сумма таблицы разделов (CRC32): %s
               """
                .formatted( new String( this.getSignature().toByteArray() ), 
                       this.getVersionHeader().toString( 16 ),
                       this.getSizeGPT(),
                       this.getSumKontrolGPT().toString( 16 ),
                       this.getAddressSectorPrimaryGPT(),
                       this.getAddressSectorCopyPrimaryGPT(),
                       this.getAddressSectorStart(),
                       this.getAddressSectorEnd(),                        
                       this.getGUID().toString( 16 ),
                       this.getAddresPartitionTableStart(),
                       this.getMaxPartition(),
                       this.getSizePartitionRecord(),
                       this.getSumKontrolPartitionTable().toString( 16 ) );
    }
    
    
    public final BigInteger getSignature() {
        return new BigInteger( 1, signature );
    }
    public final BigInteger getVersionHeader() {
        return new BigInteger( 1, versionHeader );
    }
    public final BigInteger getSizeGPT() {
        return new BigInteger( 1, sizeGPT );
    }
    public final BigInteger getSumKontrolGPT() {
        return new BigInteger( 1, sumKontrolGPT );
    }
    public final BigInteger getAddressSectorPrimaryGPT() {
        return new BigInteger( 1, addressSectorPrimaryGPT );
    }
    public final BigInteger getAddressSectorCopyPrimaryGPT() {
        return new BigInteger( 1, addressSectorCopyPrimaryGPT );
    }
    public final BigInteger getAddressSectorStart() {
        return new BigInteger( 1, addressSectorStart );
    }
    public final BigInteger getAddressSectorEnd() {
        return new BigInteger( 1, addressSectorEnd );
    }
    public final BigInteger getGUID() {
        return new BigInteger( 1, GUID );
    }
    public final BigInteger getAddresPartitionTableStart() {
        return new BigInteger( 1, addresPartitionTableStart );
    }
    public final BigInteger getMaxPartition() {
        return new BigInteger( 1, maxPartition );
    }
    public final BigInteger getSizePartitionRecord() {
        return new BigInteger( 1, sizePartitionRecord );
    }
    public final BigInteger getSumKontrolPartitionTable() {
        return new BigInteger( 1, sumKontrolPartitionTable );
    }
}