/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vladislav.mftreader.bpb.DosBPB;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Абстрацкия для представления структуры 
 * @author Vladislav
 */
public class DosBPB_2_0 {
    // Байт на логическом секторе
    private final byte[] numberByteLogicalSector = new byte[1];
    // Логических секторов в кластере
    private final byte[] numberLogicalSector = new byte[1];
    // Зарезервированых секторов
    private final byte[] reservedSector = new byte[2];
    // Количество таблиц FAT
    private final byte[] numberTableFAT = new byte[1];
    // Количество элементов корневого каталога
    private final byte[] numberElementRootDirictory = new byte[2];
    // Всего логических секторов на диске
    private final byte[] numberLogicalSectorMax = new byte[2];
    // Тип носителя
    private final byte[] typeDisk = new byte[1];
    // Логических секторов в FAT
    private final byte[] numberLogicalSectorFAT = new byte[2];
    
    public DosBPB_2_0 ( final DataInputStream data ) throws IOException {
        data.read( this.numberByteLogicalSector );
        data.read( this.numberLogicalSector );
        data.read( this.reservedSector );
        data.read( this.numberTableFAT );
        data.read( this.numberElementRootDirictory );
        data.read( this.numberLogicalSectorMax );
        data.read( this.typeDisk );
        data.read( this.numberLogicalSectorFAT );
        
        // Инвертирование байтов в масивах данных
        ArrayUtils.reverse( this.reservedSector );
        ArrayUtils.reverse( this.numberElementRootDirictory );
        ArrayUtils.reverse( this.numberLogicalSectorMax );
        ArrayUtils.reverse( this.numberLogicalSectorFAT );
    }

    @Override
    public String toString () {
        return """
               Байт на логическом секторе:
               Логических секторов в кластере:
               Зарезервированых секторов;
               Количество таблиц FAT:
               Количество элементов корневого каталога:
               Всего логических секторов на диске:
               Тип носителя:
               Логических секторов в FAT:
               """;
    }
    
    public BigInteger getNumberByteLogicalSector() {
        return new BigInteger( 1, numberByteLogicalSector );
    }
    public BigInteger getNumberLogicalSector() {
        return new BigInteger( 1, numberLogicalSector );
    }
    public BigInteger getReservedSector() {
        return new BigInteger( 1, reservedSector );
    }
    public BigInteger getNumberTableFAT() {
        return new BigInteger( 1, numberTableFAT );
    }
    public BigInteger getNumberElementRootDirictory() {
        return new BigInteger( 1, numberElementRootDirictory );
    }
    public BigInteger getNumberLogicalSectorMax() {
        return new BigInteger( 1, numberLogicalSectorMax );
    }
    public BigInteger getTypeDisk() {
        return new BigInteger( 1, typeDisk );
    }
    public BigInteger getNumberLogicalSectorFAT() {
        return new BigInteger( 1, numberLogicalSectorFAT );
    }
}