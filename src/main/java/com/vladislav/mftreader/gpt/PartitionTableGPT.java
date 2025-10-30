package com.vladislav.mftreader.gpt;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрактное представление структуры -Таблица разделов диска-
 * @author Vladislav
 */
public class PartitionTableGPT {
    // Номер записи 
    private final int number;
    
    // GUID типа раздела
    private final byte[] GUID_TYPE = new byte[16];
    // GUID раздела
    private final byte[] GUID_PARTITION = new byte[16];
    // Начальный LBA-адрес раздела
    private final byte[] addressStartLBA = new byte[8];
    // Конечный LBA-адрес раздела
    private final byte[] addressEndLBA = new byte[8];
    // Атрибуты раздела
    private final byte[] attributes = new byte[8];
    // Название раздела
    private final byte[] title = new byte[72];
    
    public PartitionTableGPT ( final DataInputStream data, int n ) throws IOException {
        this.number = n;
        
        data.read( this.GUID_TYPE );
        data.read( this.GUID_PARTITION );
        data.read( this.addressStartLBA );
        data.read( this.addressEndLBA );
        data.read( this.attributes );
        data.read( this.title );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.addressEndLBA );
        ArrayUtils.reverse( this.addressStartLBA );
    }
    
    @Override
    public String toString () {
        return """
               /---------------- Запись таблицы разделов %d ----------------/
               GUID типа раздела: %s
               GUID раздела: %s
               Начальный LBA-адрес раздела: %s
               Конечный LBA-адрес раздела: %s
               Атрибуты раздела: %s
               Название раздела: %s
               """.formatted( this.getNumber( ),
                              this.getGUID_TYPE().toString( 16 ), 
                              this.getGUID_PARTITION().toString(16),
                              this.getAddressStartLBA(),
                              this.getAddressEndLBA(),
                              this.getAttributes().toString( 16 ),
                                   new String ( this.getTitle().toByteArray() ) );
    }
    
    
    
    public int getNumber() {return this.number;}    
    public BigInteger getGUID_TYPE() {
        return new BigInteger( 1, GUID_TYPE );
    }
    public BigInteger getGUID_PARTITION() {
        return new BigInteger( 1, GUID_PARTITION );
    }
    public BigInteger getAddressStartLBA() {
        return new BigInteger( 1, addressStartLBA );
    }
    public BigInteger getAddressEndLBA() {
        return new BigInteger( 1, addressEndLBA );
    }
    public BigInteger getAttributes() {
        return new BigInteger( 1, attributes );
    }
    public BigInteger getTitle() {
        return new BigInteger( 1, title );
    }            
}