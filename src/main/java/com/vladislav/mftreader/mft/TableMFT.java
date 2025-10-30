package com.vladislav.mftreader.mft;

import com.vladislav.mftreader.Disk.Disk;
import com.vladislav.mftreader.Disk.Tom;
import com.vladislav.mftreader.exeption.InvalidSignatureException;
import com.vladislav.mftreader.mft.record.RecordMFT;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Абстрактное представление таблици записей MFT.
 * @author Vladislav
 */
public class TableMFT {
    private static final Logger LOG = Logger.getLogger( TableMFT.class.getName() );
    
    private final DataInputStream streamMFT;
    private final Disk disk;
    private final Tom tom;
    
    private final boolean mainRecord;
    
    public TableMFT ( final Disk disk, final Tom tom,
                      final long offsetKlaster, final long sum, final boolean mainRecord ) throws IOException {
        this.tom = tom;
        this.mainRecord = mainRecord;
        this.disk = disk;
        this.streamMFT = disk.createStream();
        // Перенос на начало записи MFT
        for ( int a = 1; a<=disk.getSizeCluster(); a++ ) {
            this.streamMFT.skip( offsetKlaster*disk.getSizeSector() );
        }        
        this.streamMFT.skipNBytes( sum );
        // Начало смещения находиться перед записью заголовка таблица MFT. 
        // Запсиь должна начинаться с сигнатуры <FILE>
        
        // Пропускаю первые 24 запписи таблици MFT
        // C 0-11 метафайлы скрытые для системы.
        // с 12-15 помечены как использованные, но на самом деле пустые.
        // с 16-23 неиспользуемые
        // c 24 начинаются пользовательские файлы и каталоги. Так же могут быть: ObjId, $Quota, $Reparse, $UsnJml
        
        // С записи 92 наблюдается ошикба с атрибутом %EA некоректно определяются записи. 
        // Вероятно формат записи атрибута сильно отличается.
        // Перемещение к началу записей MFT.
        //this.streamMFT.skipNBytes( tom.getHeadNTFS().getSizeRecordMFT().intValue() );
        // Первые 400 записей коректны. 
        
        this.streamMFT.skipNBytes( 1024*1000 );
    }
    
    public final List<RecordMFT> readMft ( final int size ) throws IOException {
        final List<RecordMFT> listMFT = new ArrayList(  );
        //this.streamMFT.mark( 1024*size+1 );
        int N = size;
        
        // В копии есть только первые 4 записи.
        if ( !isMainRecord() ) N = 4;
        
        for ( int i = 0; i<N; i++ ) {
            try {
                // Все записи MFT имееют строго заданный размер. Размер записи MFT можно определить из
                // Заголовка записи NTFS. Таким образом можно считать нужное количество записей MFT
                // с заданным смещением. Тем самым можно избежать ошибок связанных с неправельным определением
                // параметров структур атрибутов. Но это увеличивает потребление ОЗУ !!!
                final byte[] buffer = new byte[ this.tom.getHeadNTFS().getSizeRecordMFT().intValue() ];
                this.streamMFT.read( buffer );
                RecordMFT record = new RecordMFT( buffer );
                listMFT.add( record );
            } catch ( InvalidSignatureException ex ) {
                LOG.warning( ex.getMessage() );
            }
        }
        return listMFT;
    }
    
    @Override
    public String toString () {
        return "";
    }
    
    public boolean isMainRecord () {return this.mainRecord;}
    public DataInputStream getStreamMFT() {return streamMFT;}
    public Disk getDisk() {return disk;}        
}