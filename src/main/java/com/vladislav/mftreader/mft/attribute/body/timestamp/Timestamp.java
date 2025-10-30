package com.vladislav.mftreader.mft.attribute.body.timestamp;

import java.nio.file.attribute.FileTime;

/**
 *  
 * @author Vladislav
 */
public interface Timestamp {
    // Нужны для преобразования времеи из FileTime в Unix
    public static final long WINDOWS_TICK = 10_000_000L;
    public static final long SEC_TO_UNIX_EPOCH = 11644473600L;
    
    
    public FileTime getTimeCreation();
    public FileTime getTimeAltered();
    public FileTime getTimeLastRead();
    public FileTime getTimeChangedMFT();
    
    
    // Преобразование времени
    public default long convertFILETIMEtoUNIX ( final long value ) {
        // С помощью статических переменных производиться конвертация формата даты 
        // FileTime в Unix
        return ((value/WINDOWS_TICK)-SEC_TO_UNIX_EPOCH)*1_000L;
    }
}