package com.vladislav.mftreader.mft.record;


import com.vladislav.mftreader.exeption.InvalidSignatureException;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрактное представлине запси MFT.
 * @author Vladislav
 */
public class RecordHeaderMFT {
    private static final Logger LOG = Logger.getLogger( RecordHeaderMFT.class.getName() );
    
    public static final int SIZE_BASE_MFT_HEAD = 48;
    public static final String SIGNATUR = "FILE";
    
    // Сигнатура -FILE- признак записи MFT
    private final byte[] signature = new byte[4];
    // Смещение массива корректировки записи Update Sequence
    private final byte[] offsetSequence = new byte[2];
    // Размер массива корректировки записей в словах <S> Update Sequence
    private final byte[] sizeSequence = new byte[2];
    // Номер последовательности файла транзакций LSN
    private final byte[] LSN = new byte[8];
    // Номер последоватлеьности (Sequence)
    private final byte[] numberSequence = new byte[2];
    // Счётчик жёских ссылок 
    private final byte[] numberHardLink = new byte[2];
    // Смещение начала списка атрибутов
    private final byte[] offsetStarАttribute = new byte[2];
    // Флажки, отмечающее состояние записи MFT
    private final byte[] stateEntry = new byte[2];
    // Реальный размер записи MFT
    private final byte[] realSizeMFT = new byte[4];
    // Выделенный размер файловой записи
    private final byte[] sizeAllocatedMFT = new byte[4];
    // Сылка на (File Reference) на базовую запись MFT. 0 если данная файловая запись базовая. Индекс данной файловой записи указан в поле ниже.
    private final byte[] linkFileReferenceBase = new byte[8];
    // Идентификатор следующего атрубута
    private final byte[] nextAttributeID = new byte[2];
    // Индекс Данной файловой записи. Нужен коагда файл не может быть описан одной MFT записью. 
    private final byte[] indexRecordMFT = new byte[4];
    // Номер последоватлеьности обновлений (update sequence number)
    private final byte[] numberUpdateSequence = new byte[2];
    // Массив последовательности обновлений (update sequance array)
    private final byte[] arrayUpdateSequance;
    
    // Массив атрибутов
    public RecordHeaderMFT ( final DataInputStream data ) throws IOException, InvalidSignatureException {
        data.read( this.signature );
        data.read( this.offsetSequence );
        data.read( this.sizeSequence );
        data.read( this.LSN );
        data.read( this.numberSequence );
        data.read( this.numberHardLink );
        data.read( this.offsetStarАttribute );
        data.read( this.stateEntry );
        data.read( this.realSizeMFT );
        data.read( this.sizeAllocatedMFT );
        data.read( this.linkFileReferenceBase );
        data.read( this.nextAttributeID );
        data.skipNBytes( 2 );
        data.read( this.indexRecordMFT );
        data.read( this.numberUpdateSequence );
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.offsetSequence );
        ArrayUtils.reverse( this.sizeSequence );
        ArrayUtils.reverse( this.LSN );
        ArrayUtils.reverse( this.numberSequence );
        ArrayUtils.reverse( this.numberHardLink );
        ArrayUtils.reverse( this.offsetStarАttribute );
        ArrayUtils.reverse( this.stateEntry );
        ArrayUtils.reverse( this.realSizeMFT );
        ArrayUtils.reverse( this.sizeAllocatedMFT );
        ArrayUtils.reverse( this.linkFileReferenceBase );
        ArrayUtils.reverse( this.nextAttributeID );
        ArrayUtils.reverse( this.indexRecordMFT );
        //ArrayUtils.reverse( this.numberUpdateSequence );
        
        // Считывание массива корретировки.
        // Разме массива коректировки
        final int S = (getSizeSequence().intValue()*2);
        this.arrayUpdateSequance = new byte[ S ];
        data.read( this.arrayUpdateSequance );
        if ( !this.getSignature().equals( RecordHeaderMFT.SIGNATUR ) ) {
            // Если сигнатура не верная значит была считана не запись MFT 
            // Нужно сбросить поток до точки маркировки.
            throw new InvalidSignatureException( "Запись имеет неверную сигнатуру: <%s>".formatted( this.getSignature() ) );            
        }
    }
    
    @Override
    public String toString () {
        return """
               Сигнатура: %s
               Смещение Sequence: %s
               Размер Sequence: %s word 
               Номер Sequence в $LogFile LSN: %s               
               Номер Sequence: %s
               Счётчик жеских сылок: %s
               Смещение первого атрибута: %s                                             
               Cостояние записи MFT: %s               
               Реальный размер записи MFT: %s
               Выделенный размер записью MFT: %s               
               Ссылка на базовую файловую запись: %s               
               Идентификатор следующего атрибута: %s
               Индекс файловой записи: %s
               Номер последоватльености обновлений Sequence: %s              
               Массив корректировки записи MFT (update sequence array): %s
               """.formatted( this.getSignature(), this.getOffsetSequence(), 
                              this.getSizeSequence(), this.getLSN(), 
                              this.getNumberSequence(), this.getNumberHardLink(),
                              this.getOffsetStarАttribute(), this.getStateMFT(),
                              this.getRealSize(), this.getSizeAllocatedMFT(), 
                              this.getLinkFileReferenceBase(),
                              this.getNextAttributeID(), this.getIndexRecordMFT(), 
                              this.getNumberUpdateSequence().toString( 16 ),
                              this.getArrayUpdateSequance().toString( 16 ) );
    }
    
    public final BigInteger getRecordSize () {
        return this.getOffsetStarАttribute();
    }
    
    public BigInteger getSkipByte () {
        return this.getSizeAllocatedMFT().subtract( this.getOffsetStarАttribute() );
    }
    
    public final BigInteger getRealSize () {
        return new BigInteger( 1, this.realSizeMFT );
    }
    public final BigInteger getStateMFT () {
        return new BigInteger( 1, this.stateEntry );
    }
    public final String getSignature() {
        return new String( this.signature );
    }   
    public final BigInteger getOffsetSequence() {
        return new BigInteger( 1, offsetSequence );
    }
    public final BigInteger getSizeSequence() {
        return new BigInteger( 1, sizeSequence );
    }
    public final BigInteger getLSN() {
        return new BigInteger( 1, LSN );
    }
    public final BigInteger getNumberSequence() {
        return new BigInteger( 1, numberSequence );
    }
    public final BigInteger getNumberHardLink() {
        return new BigInteger( 1, numberHardLink );
    }
    public final BigInteger getOffsetStarАttribute() {
        return new BigInteger( 1, offsetStarАttribute );
    }
    public final BigInteger getSizeAllocatedMFT() {
        return new BigInteger( 1, sizeAllocatedMFT );
    }
    public final BigInteger getLinkFileReferenceBase() {
        return new BigInteger( 1, linkFileReferenceBase );
    }
    public final BigInteger getNextAttributeID() {
        return new BigInteger( 1, nextAttributeID );
    }
    public final BigInteger getIndexRecordMFT() {
        return new BigInteger( 1, indexRecordMFT );
    }
    public final BigInteger getNumberUpdateSequence() {
        return new BigInteger( 1, numberUpdateSequence );
    }
    public final BigInteger getArrayUpdateSequance() {
        return new BigInteger( 1, arrayUpdateSequance );
    }
}