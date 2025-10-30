package com.vladislav.mftreader.mft.attribute.head;

import com.vladislav.mftreader.mbr.DescriptSection;
import com.vladislav.mftreader.mft.attribute.head.packed.PackingFlags;
import com.vladislav.mftreader.mft.attribute.head.packed.PackingFlagsAttribute;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
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
 * Абстрация для представления заголовка атрибута в таблице MFT
 * Атрибуты можно разделить на две группы: резидентные и нерезидентные.
 * @author Vladislav
 */
public class AttributeHeadMFT {
    private static final Path TABLE = Paths.get( "files\\Типы атрибутов.csv" );
    private static final HashMap<Integer,String> kodeTypeAttributeMap = new HashMap( 30 );
    
    // Размер заголовка атрибута. Всегда фиксированное значение.
    // Любой заголовок атрибута начинается с фиксированной последовательности в 16 байт.
    // Далее формат записи зависит от группы атрибута (2 группы).
    public static final int SIZE_HEAD_ATTRIBUTE_MFT_BASE = 16;
    public static final String INDEFINED = "[Не определенно]";
    
    // Тип атрибута
    private final byte[] type = new byte[4];
    // Размер области памяти, занимаемой атрибутом
    private final byte[] size = new byte[4];
    // Флаг нерезидентного атрибута
    private final byte[] flagResident = new byte[1];
    // Длина имени атрибута
    private final byte[] lengthName = new byte[1];
    // Смещение имени атрибута
    private final byte[] offset = new byte[2];
    // Флаг упакованного атрибута
    private final PackingFlags flagPacked;
    // Идентификатор атрибута
    private final byte[] ID = new byte[2];
        
    
    // Статический инициализирующий блок.
    // Как только загрузчик классов загружает данный класс блок запускается и заполняет колекцию.
    // Данные беруться из файла для удобства. Так можно добавлять новые записи по мере необходимости.
    static {
        try {
            List<String> lines = Files.readAllLines( TABLE, Charset.forName( "UTF-8" ) );
            for ( int i = 1; i < lines.size(); i++ ) {
                String[] value = lines.get( i ).split( ";" );
                kodeTypeAttributeMap.put( Integer.parseInt( value[0].replaceAll( "h", "" ), 16 ), value[1] );                
            }
        } catch ( IOException ex ) {
            Logger.getLogger(DescriptSection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public AttributeHeadMFT ( final DataInputStream data ) throws IOException {
        Class cl = this.getClass();        
        InputStream stream = cl.getResourceAsStream( "files\\Типы атрибутов.csv" );
        
        
        URL rul = cl.getResource( "files\\Типы атрибутов.csv" );
        // Первые 7 полей идентичны для двух типов атрибутов. В дальнейшем форма записи отличается. 
        data.read( this.type );
        data.read( this.size );
        data.read( this.flagResident );
        data.read( this.lengthName );
        data.read( this.offset );        
        this.flagPacked = new PackingFlagsAttribute ( data );                
        data.read( this.ID );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.type );
        ArrayUtils.reverse( this.size );
        ArrayUtils.reverse( this.offset );
        ArrayUtils.reverse( this.ID );
    }
    
    
    @Override
    public String toString () {       
        String baseHead = 
        """
               /------------------ Базовый заголовок ---------------------------/
               Тип атрибута: %s
               Длина атрибута вместе с заголовком: %s
               Флаг нерезидентного атрибута: %s
               Длина имени атрибута: %s
               Смещение имени атрибута: %s
               Флаг упакованного атрибута: %s
               Идентификатор атрибута: %s
               """.formatted( this.getAttributeType(  ),
                              this.getSize(), 
                              this.getFlagResident(),
                              this.getLengthName(), this.getOffset(),
                              this.getPacked(), this.getID() );                
        return baseHead;
    }

    /**
     * Возвращает истину, если указанная сигнатура для атрибута не определена.
     * @return 
     */
    public boolean isIndefined () {
        return AttributeHeadMFT.kodeTypeAttributeMap.get( this.getType().intValue() ) == null;
    }
    /**
     * Возвращает истену, если заголовок является резидентным. 
     * @return 
     */
    public final boolean isRezident () {
        return getFlagResident().intValue() != 0;
    }
    
    public String getAttributeType ( final int value ) {
        return AttributeHeadMFT.kodeTypeAttributeMap.getOrDefault( value, AttributeHeadMFT.INDEFINED );
    }    
    public String getAttributeType () {
        return AttributeHeadMFT.kodeTypeAttributeMap.getOrDefault( this.getType().intValue(), AttributeHeadMFT.INDEFINED );
    }
    
    public final BigInteger getType() {
        return new BigInteger( 1, type );
    }
    public final BigInteger getSize() {
        return new BigInteger( 1, size );
    }
    public final BigInteger getSizeBody () {
        return this.getSize().subtract( new BigInteger( "16" ) );
    }
    public final BigInteger getFlagResident() {
        return new BigInteger( 1, flagResident );
    }
    public final BigInteger getLengthName() {
        return new BigInteger( 1, lengthName );
    }
    public final BigInteger getOffset() {
        return new BigInteger( 1, offset );
    }
    public final BigInteger getID() {
        return new BigInteger( 1, ID );
    }   
    public PackingFlags getPacked () {
        return this.flagPacked;
    }     
}