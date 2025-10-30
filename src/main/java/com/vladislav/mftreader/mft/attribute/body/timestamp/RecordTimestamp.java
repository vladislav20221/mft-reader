package com.vladislav.mftreader.mft.attribute.body.timestamp;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.attribute.FileTime;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Vladislav
 */
public class RecordTimestamp implements Timestamp {
    // время создания (creation) файла
    private final byte[] timeCreation = new byte[8];
    // время изменения (altered) файла
    private final byte[] timeAltered = new byte[8];
    // время изменения файловой записи (MFT changed)
    private final byte[] timeChangedMFT = new byte[8];
    // время последнего чтения (read) файла
    private final byte[] timeLastRead = new byte[8];
    
    public RecordTimestamp ( final DataInputStream data ) throws IOException {
        data.read( this.timeCreation );
        data.read( this.timeAltered );
        data.read( this.timeChangedMFT );
        data.read( this.timeLastRead );
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.timeCreation );
        ArrayUtils.reverse( this.timeAltered );
        ArrayUtils.reverse( this.timeChangedMFT );
        ArrayUtils.reverse( this.timeLastRead );        
    }
    
    @Override
    public String toString () {
        return """
               Время создания: %s
               Время изменения: %s
               Время изменения файловой записи: %s
               Время последнего чтения: %s
               """.formatted( this.getTimeCreation(),
                              this.getTimeAltered(),
                              this.getTimeChangedMFT(),
                              this.getTimeLastRead() );
    }
    
    @Override
    public FileTime getTimeCreation() {
        BigInteger value = new BigInteger ( 1, timeCreation );
        long unix = convertFILETIMEtoUNIX( value.longValue() );        
        return FileTime.fromMillis( unix );
    }
    @Override
    public FileTime getTimeAltered() {
        BigInteger value = new BigInteger ( 1, timeAltered );
        long unix = convertFILETIMEtoUNIX( value.longValue() );        
        return FileTime.fromMillis( unix );
    }
    @Override
    public FileTime getTimeLastRead() {
        BigInteger value = new BigInteger ( 1, timeChangedMFT );
        long unix = convertFILETIMEtoUNIX( value.longValue() );        
        return FileTime.fromMillis( unix );
    }
    @Override
    public FileTime getTimeChangedMFT() {
        BigInteger value = new BigInteger ( 1, timeLastRead );
        long unix = convertFILETIMEtoUNIX( value.longValue() );        
        return FileTime.fromMillis( unix );
    }
}