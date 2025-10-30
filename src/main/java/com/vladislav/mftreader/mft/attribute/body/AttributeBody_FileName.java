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
 *
 * @author Vladislav
 */
public class AttributeBody_FileName {
    // Ссылка на материнский каталог
    private final byte[] linkRootDiriktory = new byte[8];    
    // Временные метки
    private final Timestamp timeStamp;
    
    // Выделенный размер файла
    private final byte[] allocatedSize = new byte[8];
    // Реальный размер файла
    private final byte[] realSize = new byte[8];
    // Флаги. Права доступа MS-DOS.
    private final AccessRights permissions;
    // Используется HPFS
    private final byte[] npfs = new byte[4];
    // Длина имени в символах
    private final byte[] lengthName = new byte[1];
    // Пространство имен файла
    private final byte[] fileNameSpace = new byte[1];
    // Длина определяется полем длина имени. 
    // Имя файла
    private final byte[] fileName;
    
    
    public AttributeBody_FileName ( final DataInputStream data ) throws IOException {
        data.read( this.linkRootDiriktory );
        this.timeStamp = new RecordTimestamp( data );
        data.read( this.allocatedSize );
        data.read( this.realSize );
        this.permissions = new AccessRights_MS_DOS( data );
        data.read( this.npfs );
        data.read( this.lengthName );
        data.read( this.fileNameSpace );
        this.fileName = new byte[ this.getLengthName().intValue()*2 ];
        data.read( this.fileName );

        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.linkRootDiriktory );
        ArrayUtils.reverse( this.allocatedSize );
        ArrayUtils.reverse( this.realSize );        
        ArrayUtils.reverse( this.npfs );
    }
    
    @Override
    public String toString () {
        return """
               // Верменные метки //
               %s
               Размер файла: %d кб
               Название файла: %s
               флаги доступа MS-DOS: %s
               """.formatted( this.getTimeStamp(),
                              (double)this.getRealSize().longValue()/1024d, 
                         this.getFileName(), this.getPermissions() );
    }

    
    public final Timestamp getTimeStamp () { return this.timeStamp; }
    
    public final BigInteger getLinkRootDiriktory() {
        return new BigInteger( 1, linkRootDiriktory );
    }
    public final BigInteger getAllocatedSize() {
        return new BigInteger( 1, allocatedSize );
    }
    public final BigInteger getRealSize() {
        return new BigInteger( 1, realSize );
    }
    public final AccessRights getPermissions() {
        return this.permissions;
    }
    public final BigInteger getNpfs() {
        return new BigInteger( 1, npfs );
    }
    public final BigInteger getLengthName() {
        return new BigInteger( 1, lengthName );
    }
    public final BigInteger getFileNameSpace() {
        return new BigInteger( 1, fileNameSpace );
    }
    public final String getFileName() {
        return new String ( fileName );
    }
}