package com.vladislav.mftreader.mbr;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрактное представлине записи описывающий раздел диска.
 * @author Vladislav
 */
public class DescriptSection {
    private static final Path TABLE = Paths.get( "files\\Коды типов разделов.csv" );
    private static final HashMap<Integer,String> kodePartitionMap = new HashMap( 30 );
    
    // Номер описательного блока.
    private final int number;
    
    // Признак активности раздела
    public final String activity;
    // Номер поверхности (головки) диска, с которой начинается раздел
    public final ByteBuffer surfaceNumberStart = ByteBuffer.allocate( 1 );
    // Номер поверхности (головки) диска, на которой заканчивается раздел
    public final ByteBuffer surfaceNumberEnd = ByteBuffer.allocate( 1 );
    
    // Номер цилиндра (дорожки) с которого начинается раздел.
    public final ByteBuffer cylinderNumberStart = ByteBuffer.allocate( 2 );
    // Номер цилиндра (дорожки) на котором заканчивается раздел
    public final ByteBuffer cylinderNumberEnd = ByteBuffer.allocate( 2 );
    
    // Номер сектора с которого начинается раздел.
    public final ByteBuffer sectorNumberStart = ByteBuffer.allocate( 1 );
    // Номер сектора на которых заканчивается раздел
    public final ByteBuffer sectorNumberEnd = ByteBuffer.allocate( 1 );
    
    // Код раздела
    public final String kodePartition;
        
    // Абсолютный номер начального раздела LBA
    public final ByteBuffer initialNumberSector = ByteBuffer.allocate( 4 );
    // Размер раздела (число секторов)
    public final ByteBuffer partitionSize = ByteBuffer.allocate( 4 );
    
    
    // Статический инициализирующий блок.
    // Как только загрузчик классов загружает данный класс блок запускается и заполняет колекцию.
    // Данные беруться из файла для удобства. Так можно добавлять новые записи по мере необходимости.
    static {
        try {
            List<String> lines = Files.readAllLines( TABLE, Charset.forName( "UTF-8" ) );
            for ( int i = 1; i < lines.size(); i++ ) {
                String[] value = lines.get( i ).split( ";" );
                kodePartitionMap.put( Integer.parseInt( value[0].replaceAll( "h", "" ), 16 ), value[1] );                
            }
        } catch ( IOException ex ) {
            Logger.getLogger(DescriptSection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DescriptSection ( final DataInputStream data, int number )
            throws IOException {
        this.number = number;
        
        if ( (data.readByte()&0xFF) == 0x80 ) 
            this.activity = "активен";
        else
            this.activity = "не активен";        
        data.read( this.surfaceNumberStart.array() );
        short cylinder_sector_start = Short.reverseBytes( data.readShort() );
        this.sectorNumberStart.put( (byte) (cylinder_sector_start&0x6) );
        this.cylinderNumberStart.putShort( (short) ((cylinder_sector_start>>6)&0x10) );
        this.kodePartition = DescriptSection.kodePartitionMap.get( data.readByte()&0xFF );
        data.read( this.surfaceNumberEnd.array() );
        short cylinder_sector_end = Short.reverseBytes( data.readShort() );
        this.sectorNumberEnd.put( (byte) (cylinder_sector_end&0x6) );
        this.cylinderNumberEnd.putShort( (short) ((cylinder_sector_end>>6)&0x10) );        
        data.read( this.initialNumberSector.array() );
        data.read( this.partitionSize.array() );
        
        // Переварачивание байтов.
        ArrayUtils.reverse( this.initialNumberSector.array() );
        ArrayUtils.reverse( this.partitionSize.array() );
    }
    
    @Override
    public String toString () {
        // Координаты CHS -> номер цилиндра / номер поверхности / номер сектора
        return """
               /---------------- Описание раздела [%d] ----------------/
               Признак активности раздела: %s Код раздела: %s
               CHS координаты начала раздела: %5d/%5d/%5d
               CHS координаты конца раздела:  %5d/%5d/%5d               
               Абсолютный номер начального раздела (LBA-1): %d
               Размер раздела (число секторов): %d
               """.formatted(this.number, this.activity, this.kodePartition,
                            
                              new BigInteger( 1, this.cylinderNumberStart.array() ),
                              new BigInteger( 1, this.surfaceNumberStart.array() ),
                              new BigInteger( 1, this.sectorNumberStart.array() ),
                                                            
                              new BigInteger( 1, this.cylinderNumberEnd.array() ),
                              new BigInteger( 1, this.surfaceNumberEnd.array() ),
                              new BigInteger( 1, this.sectorNumberEnd.array() ),
                              
                              new BigInteger( 1, this.initialNumberSector.array() ), 
                              new BigInteger( 1, this.partitionSize.array() ) );
    }
}