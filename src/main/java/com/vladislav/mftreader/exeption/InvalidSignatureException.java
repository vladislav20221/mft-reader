package com.vladislav.mftreader.exeption;

/**
 * Ошибка генерируемая тогда когда была прочитана не ожидаемая сигнатура.
 * В таком слуачае нужно сбрость поток до маркера. 
 * @author Vladislav
 */
public class InvalidSignatureException extends Exception {
   
    public InvalidSignatureException ( final String message ) {
        super( message );
    }
}