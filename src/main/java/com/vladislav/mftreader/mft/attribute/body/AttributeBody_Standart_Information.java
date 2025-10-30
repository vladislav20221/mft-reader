package com.vladislav.mftreader.mft.attribute.body;

import com.vladislav.mftreader.mft.attribute.access.AccessRights;
import com.vladislav.mftreader.mft.attribute.access.AccessRights_MS_DOS;
import com.vladislav.mftreader.mft.attribute.body.timestamp.RecordTimestamp;
import com.vladislav.mftreader.mft.attribute.body.timestamp.Timestamp;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрактное представлине тела атрибута -Standart Information-
 * @author Vladislav
 */
public class AttributeBody_Standart_Information {
    // Размер данного атрибута фиксированный.
    public static final int SIZE_BODY = 72;
    
    // Временные метки
    private final Timestamp timeStamp;
    // Права доступа MS-DOS
    private final AccessRights accessRight;
    
    // номера версии 
    private final byte[] versionBumber = new byte[8];
    // Идентификатор класса
    private final byte[] classID = new byte[4];
    // Идентификатор владельца
    private final byte[] ownerID = new byte[4];
    // Идентификатор безопасности
    private final byte[] securityID = new byte[4];
    // Количество квотируемых байт
    private final byte[] numberQuotaCharged = new byte[8];
    // Номер последней последовательности обновления USN
    private final byte[] numberSequenceUpdate = new byte[8];
    
    
    public AttributeBody_Standart_Information ( final DataInputStream data ) throws IOException {
        this.timeStamp = new RecordTimestamp ( data );
        this.accessRight = new AccessRights_MS_DOS( data );
                
        data.read( this.versionBumber );
        data.read( this.classID );
        data.read( this.ownerID );
        data.read( this.securityID );
        data.read( this.numberQuotaCharged );
        data.read( this.numberSequenceUpdate );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.versionBumber );
        ArrayUtils.reverse( this.classID );
        ArrayUtils.reverse( this.ownerID );
        ArrayUtils.reverse( this.securityID );
        ArrayUtils.reverse( this.numberQuotaCharged );
        ArrayUtils.reverse( this.numberSequenceUpdate );
    }
    
    
    @Override
    public String toString () {
        return this.timeStamp.toString()+"\n"+this.accessRight.toString();
    }
    
    public BigInteger getVersionBumber() {
        return new BigInteger( 1, versionBumber );
    }
    public BigInteger getClassID() {
        return new BigInteger( 1, classID );
    }
    public BigInteger getOwnerID() {
        return new BigInteger( 1, ownerID );
    }
    public BigInteger getSecurityID() {
        return new BigInteger( 1, securityID );
    }
    public BigInteger getNumberQuotaCharged() {
        return new BigInteger( 1, numberQuotaCharged );
    }
    public BigInteger getNumberSequenceUpdate() {
        return new BigInteger( 1, numberSequenceUpdate );
    }
}